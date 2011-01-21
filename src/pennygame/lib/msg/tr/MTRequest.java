package pennygame.lib.msg.tr;

import pennygame.lib.msg.PennyMessage;

/**
 * A request to the server to lock a quote for accepting
 * @author william
 *
 */
public class MTRequest extends PennyMessage {

	private static final long serialVersionUID = -6618252632304162521L;

	private final int quoteId;
	
	public MTRequest(int quoteId) {
		this.quoteId = quoteId;
	}

	public int getQuoteId() {
		return quoteId;
	}
}
