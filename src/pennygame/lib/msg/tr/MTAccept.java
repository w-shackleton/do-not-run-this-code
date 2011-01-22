package pennygame.lib.msg.tr;

import pennygame.lib.msg.PennyMessage;

public class MTAccept extends PennyMessage {

	private static final long serialVersionUID = -7725108250334478474L;

	private final int quoteId;
	private final boolean accept;
	
	public MTAccept(int quoteId, boolean accept) {
		this.quoteId = quoteId;
		this.accept = accept;
	}

	public int getQuoteId() {
		return quoteId;
	}

	public boolean isAccepted() {
		return accept;
	}
}
