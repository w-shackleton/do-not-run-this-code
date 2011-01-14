package pennygame.lib.clientutils;

import java.security.PublicKey;

import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.PennyMessage;
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
	}
	
	@Override
	protected void setup() {
		// TODO: Add login processing (Wait until request received from PushHandler)
	}
	
	/**
	 * Puts a message as if it came from this queue. This is here to allow protected access from package classes
	 * @param msg
	 */
	protected void sendMessage(PennyMessage msg) {
		putMessage(msg);
	}
	
	public MsgBacks msgBacks = new MsgBacks();
	public PublicKey rsaKey;
	
	public class MsgBacks // This is a container of functions called by the PushHandler
	{
		public synchronized void onLoginKey(PublicKey rsaKey) {
			
			MLoginRequest req = new MLoginRequest(username, PasswordUtils.encryptPassword(rsaKey, pass));
			SConnMainThread.this.rsaKey = rsaKey;
			putMessage(req);
		}
	};
}
