package pennygame.lib.queues.handlers;

import pennygame.lib.msg.data.User;

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
