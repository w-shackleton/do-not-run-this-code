package pennygame.server.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import pennygame.lib.msg.MPauseGame;
import pennygame.server.client.CMulticaster;

public class GameUtils {
	protected final Connection conn;
	
	public final UserUtils users;
	public final QuoteUtils quotes;
	public final CMulticaster multicast;
	
	private boolean gamePaused = false;
	
	/**
	 * 
	 * @param conn
	 * @param quoteAcceptingConn
	 * @param miscDataConn
	 * @param connPool A pool of 4 connections
	 * @param multicast
	 * @throws SQLException
	 */
	public GameUtils(Connection conn, Connection quoteAcceptingConn, Connection miscDataConn, Connection[] connPool, CMulticaster multicast) throws SQLException {
		this.conn = conn;
		this.multicast = multicast;
		this.quotes = new QuoteUtils(conn, quoteAcceptingConn, miscDataConn, connPool, multicast);
		this.users = new UserUtils(conn, quotes);
	}

	/**
	 * Pauses gameplay
	 * @param gamePaused true to pause, false to resume
	 */
	public void pauseGame(boolean gamePaused) {
		this.gamePaused = gamePaused;
		System.out.println("Pausing / resuming game");
		multicast.multicastMessage(new MPauseGame(gamePaused));
	}

	public boolean isGamePaused() {
		return gamePaused;
	}
	
	public synchronized void beginStopping() {
		quotes.beginStopping();
	}
	
	public synchronized void resetGame() throws SQLException {
		multicast.stopAllClients();
		try {
			Thread.sleep(500); // Give a bit of time to stop.
		} catch (InterruptedException e) { e.printStackTrace(); }
		
		conn.setAutoCommit(false);
		Statement stat = conn.createStatement();
		stat.executeUpdate("DELETE FROM quotes;");
		stat.executeUpdate("DELETE FROM worthguess;");
		stat.executeUpdate("DELETE FROM users;");
		conn.commit();
		conn.setAutoCommit(true);
	}
}
