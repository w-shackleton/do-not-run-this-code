package pennygame.admin.queues;

import pennygame.lib.clientutils.SConnMainThread;
import pennygame.lib.msg.MRefresher;

public class AdminSConnMainThread extends SConnMainThread {

	public AdminSConnMainThread(String username, String pass, String threadID) {
		super(username, pass, threadID);
	}
	
	@Override
	protected void loop() {
		super.loop();
		if(refreshUsers) {
			putMessage(new MRefresher(MRefresher.REF_USERLIST));
			refreshUsers = false;
		}
		if(refreshPastTrades) {
			putMessage(new MRefresher(MRefresher.REF_PASTTRADES));
			refreshPastTrades = false;
		}
	}
	
	public boolean refreshUsers = false; // Request user list immediately
	public boolean refreshPastTrades = false;
}
