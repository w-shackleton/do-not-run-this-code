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

	final Connection conn;

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
		System.out.println("DB connected");
		// conn.setAutoCommit(true);

		// DEBUG ONLY!!!!! (one or the other)
		// resetDB();
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

		initialiseDB();
	}

	protected void initialiseDB() throws SQLException {
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists users (" +
				"id INT(16) UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
				"username VARCHAR(40) UNIQUE NOT NULL," +
				"password VARCHAR(50) NOT NULL," +
				"pennies INT(32) NOT NULL," +
				"bottles INT(32) NOT NULL," +
				"INDEX(username(10), password)," +
				"INDEX(username(10))" +
				") TYPE=INNODB;");
	}

	public Connection getConnection() {
		return conn;
	}
}
