package pennygame.lib.msg.adm;

import pennygame.lib.msg.PennyMessage;

/**
 * An admin {@link PennyMessage} to tell the server to pause the game.
 * @author william
 *
 */
public class MAGamePause extends PennyMessage {

	private static final long serialVersionUID = -3731762865649614861L;

	private final boolean pause;
	
	public MAGamePause(boolean pause) {
		this.pause = pause;
	}

	/**
	 * Returns TRUE if the game should be paused
	 * @return
	 */
	public boolean shouldPause() {
		return pause;
	}
}
