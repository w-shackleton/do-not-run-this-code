package pennygame.lib.msg;

import pennygame.lib.msg.data.User;

public class MLoginCompleted extends PennyMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5996629046507073081L;
	
	public final boolean success;
	
	public final User user;

	public MLoginCompleted(boolean success, int id, String username, String friendlyName) {
		this.success = success;
		user = new User(id, username, friendlyName);
	}
}
