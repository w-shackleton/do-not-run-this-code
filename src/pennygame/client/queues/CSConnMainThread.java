package pennygame.client.queues;

import pennygame.lib.clientutils.SConnMainThread;
import pennygame.lib.msg.MRefresher;

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
	
	protected boolean refreshOpenQuoteList = false;
	protected boolean refreshMyInfo = false;
	protected boolean refreshMyQuotes = false;
}
