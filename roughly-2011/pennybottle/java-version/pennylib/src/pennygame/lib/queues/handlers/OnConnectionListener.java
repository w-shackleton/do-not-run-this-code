package pennygame.lib.queues.handlers;

/**
 * An interface to notify something that a connection has been established or lost.
 * @author william
 *
 */
public interface OnConnectionListener {
	
	/**
	 * Called when any part of the connection has been lost - usually detected by the keepalives
	 */
	public void onConnectionLost(); // Pass some variables?
	
	/**
	 * Called when connection is made
	 */
	public void onConnected();
}
