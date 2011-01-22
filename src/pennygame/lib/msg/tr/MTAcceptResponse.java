package pennygame.lib.msg.tr;

public class MTAcceptResponse extends MTAccept {
	
	public static final int ACCEPT_QUOTE_SUCCESS = 0;
	public static final int ACCEPT_QUOTE_FAIL = 1;
	public static final int ACCEPT_QUOTE_NOMONEY = 2;

	private static final long serialVersionUID = -6725203614601318896L;
	
	private final int status;

	public MTAcceptResponse(int quoteId, int status) {
		super(quoteId, true);
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
