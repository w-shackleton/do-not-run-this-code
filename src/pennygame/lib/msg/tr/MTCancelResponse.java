package pennygame.lib.msg.tr;

public class MTCancelResponse extends MTCancel {

	private static final long serialVersionUID = 8343335149621858L;
	
	/**
	 * The response that the cancel was successful. This usually isn't sent.
	 */
	public static final int RESPONSE_OK = 0;
	public static final int RESPONSE_ALREADY_TAKEN = 1;
	
	private final int response;

	public MTCancelResponse(int quoteId, int response) {
		super(quoteId);
		this.response = response;
	}

	public int getResponse() {
		return response;
	}
}
