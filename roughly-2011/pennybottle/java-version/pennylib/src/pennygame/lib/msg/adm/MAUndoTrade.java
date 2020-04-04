package pennygame.lib.msg.adm;

import pennygame.lib.msg.PennyMessage;

/**
 * A message to 'undo' (reverse the action of) a completed trade.
 * @author william
 *
 */
public class MAUndoTrade extends PennyMessage {

	private static final long serialVersionUID = -7683235604783208273L;

	private final int tradeId;
	
	public MAUndoTrade(int tradeId) {
		this.tradeId = tradeId;
	}

	public int getTradeId() {
		return tradeId;
	}
}
