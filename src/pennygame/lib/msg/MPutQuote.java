package pennygame.lib.msg;

/**
 * {@link PennyMessage} to put a new quote onto the system
 * @author william
 *
 */
public class MPutQuote extends PennyMessage {

	private static final long serialVersionUID = 2696611065441646931L;
	
	public static final int TYPE_BUY = 1;
	public static final int TYPE_SELL = 2;
	
	private final int type, pennies, bottles;

	public MPutQuote(int type, int pennies, int bottles) {
		this.type = type;
		this.pennies = Math.abs(pennies);
		this.bottles = Math.abs(bottles);
	}

	public int getType() {
		return type;
	}

	public int getPennies() {
		return pennies;
	}

	public int getBottles() {
		return bottles;
	}
}
