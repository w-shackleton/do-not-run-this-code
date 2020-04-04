package pennygame.lib.msg.tr;

import pennygame.lib.msg.PennyMessage;

/**
 * Tells the Server to cancel a quote.
 * @author william
 *
 */
public class MTCancel extends PennyMessage {
	
	private static final long serialVersionUID = 2846923108551396000L;
	
	private final  int quoteId;

	public MTCancel(int quoteId) {
		this.quoteId = quoteId;
	}

	public int getQuoteId() {
		return quoteId;
	}
}
