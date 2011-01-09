package pennygame.lib.queues.handlers;

public interface OnConnectionLostListener {
	
	/**
	 * Called when any part of the connection has been lost - usually detected by the keepalives
	 */
	public void onConnectionLost(); // Pass some variables?
}
