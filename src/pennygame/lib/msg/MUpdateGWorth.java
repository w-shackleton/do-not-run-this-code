package pennygame.lib.msg;

public class MUpdateGWorth extends PennyMessage {

	private static final long serialVersionUID = -3984895415497079739L;

	private final int gWorth;
	
	public MUpdateGWorth(int guessedWorth) {
		this.gWorth = guessedWorth;
	}

	public int getgWorth() {
		return gWorth;
	}
}
