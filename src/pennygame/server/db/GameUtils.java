package pennygame.server.db;

import java.sql.Connection;
import java.sql.SQLException;

import pennygame.lib.msg.MPauseGame;
import pennygame.server.client.CMulticaster;

public class GameUtils {
	protected final Connection conn;
	
	public final UserUtils users;
	public final QuoteUtils quotes;
	public final CMulticaster multicast;
	
	private boolean gamePaused = false;
	
	public GameUtils(Connection conn, Connection quoteAcceptingConn, CMulticaster multicast) throws SQLException {
		this.conn = conn;
		this.multicast = multicast;
		this.quotes = new QuoteUtils(conn, quoteAcceptingConn, multicast);
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
	
	public synchronized void resetGame() {
	}
}
