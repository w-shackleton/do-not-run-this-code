package pennygame.lib.clientutils;

import java.security.PublicKey;

import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.queues.MainThread;

public class SConnMainThread extends MainThread {
	
	final String pass, username;
	
	public SConnMainThread(String username, String pass, String threadID) {
		super(threadID);
		this.pass = pass;
		this.username = username;
	}
	
	@Override
	protected void loop() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void setup() {
		// TODO: Add login processing (Wait until request received from PushHandler)
	}
	
	public MsgBacks msgBacks = new MsgBacks();
	
	public class MsgBacks // This is a container of functions called by the PushHandler
	{
		public synchronized void onLoginKey(PublicKey rsaKey) {
			
			MLoginRequest req = new MLoginRequest(username, PasswordUtils.encryptPassword(rsaKey, pass));
			putMessage(req);
		}
	};
}
