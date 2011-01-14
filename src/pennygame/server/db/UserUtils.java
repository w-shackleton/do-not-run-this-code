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
	private final PreparedStatement newUserStatement, checkUserStatement, deleteUserStatement;

	UserUtils(Connection conn) throws SQLException {
		this.conn = conn;
		newUserStatement = conn
				.prepareStatement("INSERT INTO users(username, password, pennies, bottles) VALUES (?, ?, ?, ?);");
		checkUserStatement = conn.prepareStatement("SELECT id FROM users WHERE username=? AND password=?;");
		deleteUserStatement = conn.prepareStatement("DELETE FROM users WHERE id=?;");
	}

	public synchronized void createUser(String username, byte[] hashPass, int pennies,
			int bottles) throws SQLException {
		newUserStatement.setString(1, username.toLowerCase());
		newUserStatement.setString(2, Base64.encodeBytes(hashPass));
		newUserStatement.setInt(3, pennies);
		newUserStatement.setInt(4, bottles);

		newUserStatement.executeUpdate();
	}

	private LinkedList<User> userList;

	public synchronized LinkedList<User> getUsers() throws SQLException {
		if (userList == null)
			userList = new LinkedList<User>();
		userList.clear();

		Statement statement = conn.createStatement();

		ResultSet rs = statement
				.executeQuery("SELECT id, username, pennies, bottles FROM users;");
		while (rs.next()) {
			userList.add(new User(rs.getInt("id"), rs.getString("username"), rs
					.getInt("pennies"), rs.getInt("bottles")));
		}
		rs.close();
		statement.close();

		return userList;
	}
	
	/**
	 * Checks if the username and password are valid
	 * @param username
	 * @param hashPass
	 * @return true if the user & password combo are valid
	 * @throws SQLException 
	 */
	public synchronized boolean checkLogin(String username, byte[] hashPass) throws SQLException {
		checkUserStatement.setString(1, username);
		checkUserStatement.setString(2, Base64.encodeBytes(hashPass));
		
		ResultSet rs = checkUserStatement.executeQuery();
		return rs.last(); // Returns false if no rows
		// TODO: Check this works!
	}
	
	public synchronized void deleteUser(int userId) throws SQLException {
		deleteUserStatement.setInt(1, userId);
		deleteUserStatement.executeUpdate();
	}
}
