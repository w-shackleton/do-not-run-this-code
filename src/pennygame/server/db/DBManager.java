package pennygame.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import pennygame.lib.queues.LoopingThread;

public class DBManager extends LoopingThread {
	final Connection conn;
	
	public DBManager() throws SQLException {
		super("DB Mgmt");
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		conn = DriverManager.getConnection("jdbc:sqlite:penny.db");
		conn.setAutoCommit(true);
	}
	
	@Override
	protected void setup() {
		try {
			resetDB();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	protected void resetDB() throws SQLException {
		Statement stat = conn.createStatement();
		stat.executeUpdate("drop table if exists users;");
		
		initialiseDB();
	}
	
	protected void initialiseDB() throws SQLException {
		Statement stat = conn.createStatement();
		stat.executeUpdate("create table if not exists users (id INTEGER PRIMARY KEY, user TEXT, pass TEXT);");
	}
}
