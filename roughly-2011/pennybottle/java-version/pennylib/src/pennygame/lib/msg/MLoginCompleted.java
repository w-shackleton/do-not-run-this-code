package pennygame.lib.msg;

import pennygame.lib.msg.data.User;

/**
 * Tells the (non-server) component that the login has completed
 * @author william
 *
 */
public class MLoginCompleted extends PennyMessage {
	
	private static final long serialVersionUID = -5996629046507073081L;
	
	public final boolean success;
	
	public final User user;
	
	private final boolean paused;

	public MLoginCompleted(boolean success, int id, String username, String friendlyName, boolean paused) {
		this.success = success;
		user = new User(id, username, friendlyName);
		this.paused = paused;
	}

	public boolean isPaused() {
		return paused;
	}
}
