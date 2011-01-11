package pennygame.server.admin;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import pennygame.lib.GlobalPreferences;
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
	}
}
