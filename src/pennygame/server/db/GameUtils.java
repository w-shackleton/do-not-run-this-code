package pennygame.server.db;

import java.sql.Connection;
import java.sql.SQLException;

public class GameUtils {
	protected final Connection conn;
	
	public final UserUtils users;
	
	private boolean gamePaused = false;
	
	public GameUtils(Connection conn) throws SQLException {
		this.conn = conn;
		this.users = new UserUtils(conn);
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
