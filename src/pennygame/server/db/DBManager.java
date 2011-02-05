package pennygame.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import pennygame.lib.queues.LoopingThread;

public class DBManager extends LoopingThread {
	// TODO: Change to not a thread!
	
	private final Connection conn, quoteAcceptingConn, miscDataConn;
	private final Connection[] connectionPool;

	public DBManager(String server, String database, String username, String password) throws SQLException {
		super("DB Mgmt");
		
		String dbUrl = "jdbc:mysql://" + server + "/" + database;
		
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
		miscDataConn = DriverManager.getConnection(dbUrl, username, password);
		
		connectionPool = new Connection[] {
				DriverManager.getConnection(dbUrl, username, password),
				DriverManager.getConnection(dbUrl, username, password),
				DriverManager.getConnection(dbUrl, username, password),
				DriverManager.getConnection(dbUrl, username, password)
				};
		
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
		for(int i = 0; i < 240; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(stopping) break;
		}
		Statement stat;
		try {
			stat = conn.createStatement();
			stat.executeQuery("SELECT 0;"); // Keepalive
			
			stat = quoteAcceptingConn.createStatement();
			stat.executeQuery("SELECT 0;"); // Keepalive
			quoteAcceptingConn.commit();
			
			stat = miscDataConn.createStatement();
			stat.executeQuery("SELECT 0;"); // Keepalive
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		stat.executeUpdate("drop table if exists worthguess;");

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
		stat.executeUpdate("CREATE TABLE IF NOT EXISTS worthguess (" +
				"id INT(32) UNSIGNED PRIMARY KEY AUTO_INCREMENT," +
				"userid INT(16) NOT NULL," +
				"guess INT(32) NOT NULL," +
				"time TIMESTAMP NOT NULL DEFAULT NOW()," +
				"" +
				"INDEX(userid)" +
				") TYPE=INNODB;");
		stat.executeUpdate(
				"CREATE OR REPLACE VIEW tradehistory AS " +
				"SELECT id, status, type, pennies, bottles, timecreated, timeaccepted FROM quotes " +
				"WHERE status!='open'");
		stat.executeUpdate(
				"CREATE OR REPLACE VIEW detailedtradehistory AS " +
				"SELECT quotes.id AS id, quotes.status, quotes.type, userfrom.friendlyname AS fromname, userto.friendlyname AS toname, quotes.pennies, quotes.bottles, timecreated, timeaccepted FROM quotes " +
				"LEFT JOIN users AS userfrom ON idfrom=userfrom.id LEFT JOIN users AS userto ON idto=userto.id WHERE quotes.status='closed' ORDER BY quotes.id;");
	}

	public Connection getConnection() {
		return conn;
	}

	public Connection getQuoteAcceptingConnection() {
		return quoteAcceptingConn;
	}

	public Connection getMiscDataConnection() {
		return miscDataConn;
	}

	public Connection[] getConnectionPool() {
		return connectionPool;
	}
}
