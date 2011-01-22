package pennygame.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import pennygame.lib.queues.LoopingThread;

public class DBManager extends LoopingThread {
	private static final String server = "10.4.1.9";
	private static final String database = "penny";
	private static final String username = "penny";
	private static final String password = "f7qMfKej0Lmfzi4B76";
	private static final String dbUrl = "jdbc:mysql://" + server + "/"
			+ database;

	private final Connection conn, quoteAcceptingConn;

	public DBManager() throws SQLException {
		super("DB Mgmt");
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		conn = DriverManager.getConnection(dbUrl, username, password);
		quoteAcceptingConn = DriverManager.getConnection(dbUrl, username, password);
		quoteAcceptingConn.setAutoCommit(false); // Uses transactions
		System.out.println("DB connected");
		// conn.setAutoCommit(true);

		// DEBUG ONLY!!!!! (resets DB)
		final boolean DEBUG = false;
		if(DEBUG)
			resetDB();
		else
			initialiseDB();
	}

	@Override
	protected void setup() {
	}

	@Override
	protected void loop() {

	}

	@Override
	protected void finish() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("Database closed");
	}

	public void resetDB() throws SQLException {
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists users;");
		stat.executeUpdate("drop table if exists quotes;");

		initialiseDB();
	}

	protected void initialiseDB() throws SQLException {
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists users (" +
				"id INT(16) UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
				"username VARCHAR(20) UNIQUE NOT NULL," +
				"password VARCHAR(50) NOT NULL," +
				"friendlyname VARCHAR(20) NOT NULL," +
				"pennies INT(32) NOT NULL," +
				"bottles INT(32) NOT NULL," +
				"INDEX(id)," +
				"INDEX(username(10), password)" +
				") TYPE=INNODB;");
		stat.executeUpdate("CREATE TABLE IF NOT EXISTS quotes (" +
				"id INT(32) UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
				"status ENUM('open', 'closed', 'cancelled', 'timeout') NOT NULL DEFAULT 'open'," +
				"type ENUM('buy', 'sell') NOT NULL," +
				"idfrom INT(16) UNSIGNED NOT NULL," +
				"idto   INT(16) NULL," +
				"bottles INT(32) NOT NULL," +
				"pennies INT(32) UNSIGNED NOT NULL," +
				"lockid INT(16) UNSIGNED," +
				"timecreated TIMESTAMP NOT NULL DEFAULT NOW()," +
				"timeaccepted TIMESTAMP NULL," +
				"" +
				"INDEX(status, pennies)," +
				"INDEX(status, timecreated)," +
				"INDEX(type)," +
				"INDEX(pennies)," +
				"INDEX(idfrom, idto)" +
				") TYPE=INNODB;");
	}

	public Connection getConnection() {
		return conn;
	}

	public Connection getQuoteAcceptingConnection() {
		return quoteAcceptingConn;
	}
}
