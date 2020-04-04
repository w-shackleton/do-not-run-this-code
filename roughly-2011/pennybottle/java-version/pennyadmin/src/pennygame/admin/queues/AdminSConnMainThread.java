package pennygame.admin.queues;

import pennygame.lib.clientutils.SConnMainThread;
import pennygame.lib.msg.MRefresher;

/**
 * Outgoing connection handler from this Admin to the Server
 * @author william
 *
 */
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
	
	/**
	 * When <code>true</code>, the users list will be resent
	 */
	public boolean refreshUsers = false;
	/**
	 * When <code>true</code>, the past trades list will be resent
	 */
	public boolean refreshPastTrades = false;
}
