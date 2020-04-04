package pennygame.lib.msg;

/**
 * A {@link PennyMessage} from a Client telling the server to update their guessed wealth.
 * @author william
 *
 */
public class MUpdateGWealth extends PennyMessage {

	private static final long serialVersionUID = -3984895415497079739L;

	private final int gWorth;
	
	public MUpdateGWealth(int guessedWorth) {
		this.gWorth = guessedWorth;
	}

	public int getgWorth() {
		return gWorth;
	}
}
