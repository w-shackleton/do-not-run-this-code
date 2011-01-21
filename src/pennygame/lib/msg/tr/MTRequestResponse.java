package pennygame.lib.msg.tr;

import pennygame.lib.msg.data.OpenQuote;

public class MTRequestResponse extends MTRequest {

	private static final long serialVersionUID = 4419834372668816696L;
	
	private final boolean success;
	private final OpenQuote quote;
	
	public MTRequestResponse(int quoteId, boolean success) {
		super(quoteId);
		this.success = success;
		quote = null;
	}

	public MTRequestResponse(int quoteId, boolean success, OpenQuote quote) {
		super(quoteId);
		this.success = success;
		this.quote = quote;
	}

	public boolean wasSuccessful() {
		return success;
	}

	public OpenQuote getQuote() {
		return quote;
	}
}
