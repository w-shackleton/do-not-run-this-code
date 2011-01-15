package pennygame.server.db;

import java.sql.Connection;
import java.sql.SQLException;

import pennygame.server.client.CMulticaster;

public class GameUtils {
	protected final Connection conn;
	
	public final UserUtils users;
	public final QuoteUtils quotes;
	public final CMulticaster multicast;
	
	private boolean gamePaused = false;
	
	public GameUtils(Connection conn, CMulticaster multicast) throws SQLException {
		this.conn = conn;
		this.multicast = new CMulticaster();
		this.users = new UserUtils(conn);
		this.quotes = new QuoteUtils(conn, multicast);
	}

	/**
	 * Pauses gameplay
	 * @param gamePaused true to pause, false to resume
	 */
	public void pauseGame(boolean gamePaused) {
		this.gamePaused = gamePaused;
		// TODO: Notify clients of this!
	}

	public boolean isGamePaused() {
		return gamePaused;
	}
}
