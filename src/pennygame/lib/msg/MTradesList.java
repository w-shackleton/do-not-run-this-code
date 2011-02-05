package pennygame.lib.msg;

import java.util.LinkedList;

import pennygame.lib.msg.data.ClosedQuote;

public class MTradesList extends PennyMessage {

	private static final long serialVersionUID = -1964663222173942989L;
	
	private final LinkedList<ClosedQuote> trades;
	private final long minTime, maxTime;
	private final int minPennies, maxPennies;

	public MTradesList(LinkedList<ClosedQuote> trades, long minTime, long maxTime, int minPennies, int maxPennies) {
		this.trades = trades;
		this.minTime = minTime;
		this.maxTime = maxTime;
		this.minPennies = minPennies;
		this.maxPennies = maxPennies;
	}
	
	public MTradesList(LinkedList<ClosedQuote> trades) {
		this(trades, 0, 0, 0, 0);
	}

	public LinkedList<ClosedQuote> getTrades() {
		return trades;
	}

	public long getMinTime() {
		return minTime;
	}

	public long getMaxTime() {
		return maxTime;
	}

	public int getMinPennies() {
		return minPennies;
	}

	public int getMaxPennies() {
		return maxPennies;
	}
}
