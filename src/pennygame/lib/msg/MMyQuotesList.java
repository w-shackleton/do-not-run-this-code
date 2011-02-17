package pennygame.lib.msg;

import java.util.LinkedList;

import pennygame.lib.msg.data.ClosedQuote;
import pennygame.lib.msg.data.OpenQuote;

/**
 * A {@link PennyMessage} containing the user's open and closed quotes.
 * @author william
 *
 */
public class MMyQuotesList extends PennyMessage {

	private static final long serialVersionUID = 1835581074308161449L;
	
	private final LinkedList<OpenQuote> currentQuotes;
	private final LinkedList<ClosedQuote> pastQuotes;

	public MMyQuotesList(LinkedList<OpenQuote> currentQuotes, LinkedList<ClosedQuote> pastQuotes) {
		this.currentQuotes = currentQuotes;
		this.pastQuotes = pastQuotes;
	}

	public LinkedList<OpenQuote> getCurrentQuotes() {
		return currentQuotes;
	}

	public LinkedList<ClosedQuote> getPastQuotes() {
		return pastQuotes;
	}
}
