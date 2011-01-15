package pennygame.server.client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.msg.MLoginCompleted;
import pennygame.lib.msg.MLoginInitiate;
import pennygame.lib.queues.MainThread;

public class CConnMainThread extends MainThread {
	KeyPair keys;
	
	public CConnMainThread(String threadID) {
		super(threadID);
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
		// Send a new RSA Key
		{
			KeyPairGenerator kpg = null;
			try {
				kpg = KeyPairGenerator.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace(); // There is such an algorithm
			}
			kpg.initialize(GlobalPreferences.getKeysize(), new SecureRandom());
			keys = kpg.genKeyPair();
			
			MLoginInitiate loginInitiate = new MLoginInitiate(keys.getPublic());
			putMessage(loginInitiate);
		}
		
		while(!loginCompleted && !stopping)
		{
			try {
				Thread.sleep(50); // Wait a bit for response
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		putMessage(new MLoginCompleted(loginSuccess, loginName));
	}
	
	CConnMsgBacks cConnMsgBacks = new CConnMsgBacks();
	
	class CConnMsgBacks {
		public PrivateKey getPrivateKey() {
			return keys.getPrivate();
		}
		
		public void loginSuccess(boolean success, int userID, String userName) {
			loginCompleted = true;
			loginSuccess = success;
			loginName = userName;
			loginId = userID;
		}
	}
	
	protected boolean loginCompleted = false;
	protected boolean loginSuccess = false;
	protected String loginName = "";
	protected int loginId = -1;
	
	protected void sendSerialisedMessage(String m) {
		putMessage(m);
	}
}
