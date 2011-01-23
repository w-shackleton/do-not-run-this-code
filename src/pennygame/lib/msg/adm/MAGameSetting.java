package pennygame.lib.msg.adm;

import pennygame.lib.msg.PennyMessage;

/**
 * Admin message requesting a value be retrieved from the server or set.
 * The server will return the same object with {@link #data} set when this is a get.
 * @author william
 *
 */
public class MAGameSetting extends PennyMessage {

	private static final long serialVersionUID = 276139312369983643L;
	
	public static final int WHAT_QUOTE_TIMEOUT = 1;
	public static final int WHAT_QUOTE_NUMBER = 2;
	public static final int WHAT_RESET_GAME = 3;

	private final boolean set;
	
	/**
	 * What this message is for.
	 */
	private final int what;
	private final Object data;
	
	public MAGameSetting(int what) {
		this.what = what;
		set = false;
		data = null;
	}
	
	public MAGameSetting(boolean set, int what, Object data) {
		this.set = set;
		this.what = what;
		this.data = data;
	}

	/**
	 * 
	 * @return true if this is a 'set' command, false if it is a 'get'
	 */
	public boolean setOrGet() {
		return set;
	}

	public int what() {
		return what;
	}

	public Object getData() {
		return data;
	}
}
