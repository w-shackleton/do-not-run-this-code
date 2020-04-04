package pennygame.lib.msg;

/**
 * An on-screen message to the user about something.
 * @author william
 *
 */
public class MOSMessage extends PennyMessage {
	
	private static final long serialVersionUID = -1119807946169591443L;
	
	public static final int QUOTE_ERROR_NOT_ENOUGH_PENNIES = 1;
	public static final int QUOTE_ERROR_NOT_ENOUGH_BOTTLES = 2;
	
	private final int message;

	public MOSMessage(int message) {
		this.message = message;
	}

	/**
	 * One of the constants from this class
	 * @return
	 */
	public int getMessage() {
		return message;
	}
}
