package pennygame.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

import pennygame.lib.ext.Base64;
import pennygame.lib.msg.data.User;

/**
 * Utilities for managing users. Only for the admin
 * 
 * @author william
 * 
 */
public class UserUtils {

	private final Connection conn;
	private final QuoteUtils quotes;
	private final PreparedStatement newUserStatement, checkUserStatement, deleteUserStatement, changePasswordStatement, changeFriendlyNameStatement;

	UserUtils(Connection conn, QuoteUtils quotes) throws SQLException {
		this.conn = conn;
		this.quotes = quotes;
		newUserStatement = conn
				.prepareStatement("INSERT INTO users(username, password, pennies, bottles, friendlyname) VALUES (?, ?, ?, ?, ?);");
		checkUserStatement = conn.prepareStatement("SELECT id, friendlyname FROM users WHERE username=? AND password=?;");
		deleteUserStatement = conn.prepareStatement("DELETE FROM users WHERE id=?;");
		changePasswordStatement = conn.prepareStatement("UPDATE users SET password=? WHERE id=?");
		changeFriendlyNameStatement = conn.prepareStatement("UPDATE users SET friendlyname=? WHERE id=?");
	}

	public synchronized void createUser(String username, byte[] hashPass, int pennies, int bottles) throws SQLException {
		System.out.println("Added new user");
		newUserStatement.setString(1, username.toLowerCase());
		newUserStatement.setString(2, Base64.encodeBytes(hashPass));
		newUserStatement.setInt(3, pennies);
		newUserStatement.setInt(4, bottles);
		newUserStatement.setString(5, username);

		newUserStatement.executeUpdate();
	}

	private LinkedList<User> userList;

	public synchronized LinkedList<User> getUsers() throws SQLException {
		if (userList == null)
			userList = new LinkedList<User>();
		userList.clear();

		Statement statement = conn.createStatement();

		ResultSet rs = statement.executeQuery("SELECT id, username, friendlyname, pennies, bottles FROM users;");
		while (rs.next()) {
			userList.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("friendlyname"),
					rs.getInt("pennies"), rs.getInt("bottles")));
		}
		rs.close();
		statement.close();

		return userList;
	}

	/**
	 * Checks if the username and password are valid
	 * 
	 * @param username
	 * @param hashPass
	 * @return the user's details if the user is valid, or null if they aren't
	 * @throws SQLException
	 */
	public synchronized User checkLogin(String username, byte[] hashPass) throws SQLException {
		checkUserStatement.setString(1, username.toLowerCase());
		checkUserStatement.setString(2, Base64.encodeBytes(hashPass));

		ResultSet rs = checkUserStatement.executeQuery();
		if(!rs.last()) return null;
		
		return new User(rs.getInt("id"), username, rs.getString("friendlyname"));
	}

	public synchronized void deleteUser(int userId) throws SQLException {
		deleteUserStatement.setInt(1, userId);
		deleteUserStatement.executeUpdate();
	}

	public synchronized void changePassword(int userId, byte[] hashPass) throws SQLException {
		changePasswordStatement.setString(1, Base64.encodeBytes(hashPass));
		changePasswordStatement.setInt(2, userId);
		changePasswordStatement.executeUpdate();
	}

	public synchronized void changeFriendlyName(int userId, String friendlyName) throws SQLException {
		changeFriendlyNameStatement.setString(1, friendlyName);
		changeFriendlyNameStatement.setInt(2, userId);
		changeFriendlyNameStatement.executeUpdate();
		
		quotes.pushOpenQuotes(); // Refresh users
	}
}
