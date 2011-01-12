package pennygame.server.admin;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.msg.MLoginCompleted;
import pennygame.lib.msg.MLoginInitiate;
import pennygame.lib.queues.MainThread;

public class AdminConnMainThread extends MainThread {
	KeyPair keys;
	
	public AdminConnMainThread(String threadID) {
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
		
		while(!loginCompleted)
		{
			try {
				Thread.sleep(50); // Wait a bit for response
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		putMessage(new MLoginCompleted(loginSuccess, "admin"));
	}
	
	AdminMsgBacks adminMsgBacks = new AdminMsgBacks();
	
	class AdminMsgBacks {
		public PrivateKey getPrivateKey() {
			return keys.getPrivate();
		}
		
		public void loginSuccess(boolean success) {
			loginCompleted = true;
			loginSuccess = success;
		}
	}
	
	protected boolean loginCompleted = false;
	protected boolean loginSuccess = false;
}
