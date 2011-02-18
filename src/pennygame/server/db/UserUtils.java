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
 * Utilities for managing users. Only for the admin.
 * 
 * @author william
 * 
 */
public class UserUtils {

	private final Connection conn;
	private final QuoteUtils quotes;
	private final PreparedStatement newUserStatement, checkUserStatement, deleteUserStatement, changePasswordStatement, changeFriendlyNameStatement, changePenniesStatement, changeBottlesStatement, updateWorthGuessStatement;
	
	private final GameUtils parent;

	UserUtils(Connection conn, GameUtils parent, QuoteUtils quotes) throws SQLException {
		this.conn = conn;
		this.quotes = quotes;
		this.parent = parent;
		newUserStatement = conn
				.prepareStatement("INSERT INTO users(username, password, pennies, bottles, friendlyname) VALUES (?, ?, ?, ?, ?);");
		checkUserStatement = conn.prepareStatement("SELECT id, friendlyname FROM users WHERE username=? AND password=?;");
		deleteUserStatement = conn.prepareStatement("DELETE FROM users WHERE id=?;");
		changePasswordStatement = conn.prepareStatement("UPDATE users SET password=? WHERE id=?");
		changeFriendlyNameStatement = conn.prepareStatement("UPDATE users SET friendlyname=? WHERE id=?");
		updateWorthGuessStatement = conn.prepareStatement("INSERT INTO worthguess(userid, guess) VALUES (?, ?);");
		
		changePenniesStatement = conn.prepareStatement("UPDATE users SET pennies=? WHERE id=?");
		changeBottlesStatement = conn.prepareStatement("UPDATE users SET bottles=? WHERE id=?");
	}

	/**
	 * Creates a user with the specified details.
	 * @param username
	 * @param hashPass Hashed password
	 * @param pennies
	 * @param bottles
	 * @throws SQLException
	 */
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

	/**
	 * 
	 * @return A complete list of users, except for their passwords!
	 * @throws SQLException
	 */
	public synchronized LinkedList<User> getUsers() throws SQLException {
		if (userList == null)
			userList = new LinkedList<User>();
		userList.clear();

		Statement statement = conn.createStatement();

		ResultSet rs = statement.executeQuery("SELECT id, username, friendlyname, pennies, bottles, bottles * " + parent.getBottleValue() + " + pennies AS wealth FROM users;");
		while (rs.next()) {
			userList.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("friendlyname"),
					rs.getInt("pennies"), rs.getInt("bottles"), rs.getInt("wealth")));
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

	/**
	 * Deletes a user.
	 * @param userId
	 * @throws SQLException
	 */
	public synchronized void deleteUser(int userId) throws SQLException {
		deleteUserStatement.setInt(1, userId);
		deleteUserStatement.executeUpdate();
	}

	/**
	 * Changes a user's password.
	 * @param userId
	 * @param hashPass
	 * @throws SQLException
	 */
	public synchronized void changePassword(int userId, byte[] hashPass) throws SQLException {
		changePasswordStatement.setString(1, Base64.encodeBytes(hashPass));
		changePasswordStatement.setInt(2, userId);
		changePasswordStatement.executeUpdate();
	}

	/**
	 * Changes a user's friendly name.
	 * @param userId
	 * @param friendlyName
	 * @throws SQLException
	 */
	public synchronized void changeFriendlyName(int userId, String friendlyName) throws SQLException {
		changeFriendlyNameStatement.setString(1, friendlyName);
		changeFriendlyNameStatement.setInt(2, userId);
		changeFriendlyNameStatement.executeUpdate();
		
		quotes.pushOpenQuotes(); // Refresh users
	}
	
	/**
	 * Changes a user's number of pennies
	 * @param userId
	 * @param pennies
	 * @throws SQLException
	 */
	public synchronized void changePennies(int userId, int pennies) throws SQLException {
		changePenniesStatement.setInt(1, pennies);
		changePenniesStatement.setInt(2, userId);
		changePenniesStatement.executeUpdate();
		
		quotes.pushUserMoney(userId);
	}
	
	/**
	 * Changes a user's number of bottles
	 * @param userId
	 * @param bottles
	 * @throws SQLException
	 */
	public synchronized void changeBottles(int userId, int bottles) throws SQLException {
		changeBottlesStatement.setInt(1, bottles);
		changeBottlesStatement.setInt(2, userId);
		changeBottlesStatement.executeUpdate();
		
		quotes.pushUserMoney(userId);
	}
	
	/**
	 * Updates a user's guessed bottle price. Used by clients to update how much they think a bottle is worth.
	 * @param userIdid
	 * @param guess
	 * @throws SQLException
	 */
	public synchronized void updateGuessedWorth(int userId, int guess) throws SQLException {
		updateWorthGuessStatement.setInt(1, userId);
		updateWorthGuessStatement.setInt(2, guess);
		updateWorthGuessStatement.executeUpdate();
		
		quotes.userWorthGuesses.put(userId, guess); // Update cache
		quotes.pushUserMoney(userId);
	}
}
