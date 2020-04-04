package pennygame.lib.queues.handlers;

import pennygame.lib.msg.data.User;

/**
 * An interface to notify something that the login has completed or failed.
 * @author william
 *
 */
public interface OnLoginHandler {

	/**
	 * Called when login has completed; gameplay can begin
	 */
	public void onLoginCompleted(User userInfo, boolean isPaused);
	
	/**
	 * Called when invalid username & password found
	 */
	public void onLoginFailed();
}
