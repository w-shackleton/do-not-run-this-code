package pennygame.client.queues;

import pennygame.lib.clientutils.SConnMainThread;
import pennygame.lib.msg.MRefresher;

/**
 * Outgoing connection handler from this Client to the Server
 * @author william
 *
 */
public class CSConnMainThread extends SConnMainThread {

	public CSConnMainThread(String username, String pass, String threadID) {
		super(username, pass, threadID);
	}
	
	@Override
	protected void loop() {
		if(refreshOpenQuoteList)
		{
			putMessage(new MRefresher(MRefresher.REF_OPENQUOTELIST));
			refreshOpenQuoteList = false;
		}
		if(refreshMyInfo)
		{
			putMessage(new MRefresher(MRefresher.REF_MYINFO));
			refreshMyInfo = false;
		}
		if(refreshMyQuotes)
		{
			putMessage(new MRefresher(MRefresher.REF_MYQUOTES));
			refreshMyQuotes = false;
		}
	}
	
	/**
	 * When true, will refresh the list of open quotes.
	 */
	protected boolean refreshOpenQuoteList = false;
	/**
	 * When true, will refresh this user's info.
	 */
	protected boolean refreshMyInfo = false;
	/**
	 * When true, will refresh this user's quotes.
	 */
	// TODO: Combine this into refreshMyInfo?
	protected boolean refreshMyQuotes = false;
}
