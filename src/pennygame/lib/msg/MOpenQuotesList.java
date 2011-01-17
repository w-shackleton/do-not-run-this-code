package pennygame.lib.msg;

import java.util.LinkedList;

import pennygame.lib.msg.data.OpenQuote;

public class MOpenQuotesList extends PennyMessage {

	private static final long serialVersionUID = -5122812883131298470L;
	
	private final LinkedList<OpenQuote> list;
	private final int total;

	public MOpenQuotesList(LinkedList<OpenQuote> list, int total) {
		this.list = list;
		this.total = total;
	}

	public LinkedList<OpenQuote> getList() {
		return list;
	}

	public int getTotal() {
		return total;
	}
}
