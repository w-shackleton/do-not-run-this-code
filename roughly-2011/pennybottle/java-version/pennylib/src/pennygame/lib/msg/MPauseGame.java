package pennygame.lib.msg;

/**
 * A {@link PennyMessage} to tell a client to pause their game.
 * @author william
 *
 */
public class MPauseGame extends PennyMessage {

	private static final long serialVersionUID = 962349416670502526L;

	private final boolean pause;
	
	public MPauseGame(boolean pause) {
		this.pause = pause;
	}

	public boolean isPaused() {
		return pause;
	}
}
