package pennygame.lib.queues.handlers;

public interface OnLoginHandler {

	/**
	 * Called when login has completed; gameplay can begin
	 */
	public void onLoginCompleted();
	
	/**
	 * Called when invalid username & password found
	 */
	public void onLoginFailed();
}
