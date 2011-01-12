package pennygame.lib.msg;

public class MLoginCompleted extends PennyMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5996629046507073081L;
	
	public final boolean success;
	
	public final String username;

	public MLoginCompleted(boolean success, String username) {
		this.success = success;
		this.username = username;
	}
}
