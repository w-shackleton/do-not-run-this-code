package pennygame.lib.msg;

import java.util.LinkedList;

import pennygame.lib.msg.data.OpenQuote;

public class MOpenQuotesList extends PennyMessage {

	private static final long serialVersionUID = -5122812883131298470L;
	
	private final LinkedList<OpenQuote> list;

	public MOpenQuotesList(LinkedList<OpenQuote> list) {
		this.list = list;
	}

	public LinkedList<OpenQuote> getList() {
		return list;
	}
}
