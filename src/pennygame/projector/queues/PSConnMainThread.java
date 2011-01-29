package pennygame.projector.queues;

import pennygame.lib.clientutils.SConnMainThread;
import pennygame.lib.msg.MRefresher;

public class PSConnMainThread extends SConnMainThread {

	public PSConnMainThread(String username, String pass, String threadID) {
		super(username, pass, threadID);
	}
	
	@Override
	protected void loop() {
		if(refreshPastTrades)
		{
			putMessage(new MRefresher(MRefresher.REF_PASTTRADES));
			refreshPastTrades = false;
		}
	}
	
	protected boolean refreshPastTrades = false;
}
