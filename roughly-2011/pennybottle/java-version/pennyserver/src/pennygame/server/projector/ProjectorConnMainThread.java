package pennygame.server.projector;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.sql.SQLException;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.msg.MLoginCompleted;
import pennygame.lib.msg.MLoginInitiate;
import pennygame.lib.queues.MainThread;
import pennygame.server.db.GameUtils;

/**
 * Outgoing connection handler from this Server to a Projector
 * @author william
 *
 */
public class ProjectorConnMainThread extends MainThread {
	KeyPair keys;
	
	protected final GameUtils gameUtils;
	
	public ProjectorConnMainThread(String threadID, GameUtils gameUtils) {
		super(threadID);
		this.gameUtils = gameUtils;
	}

	@Override
	protected void loop() {
		try {
			Thread.sleep(500); // Not very urgent here
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(refreshPastTrades) {
			refreshPastTrades = false;
			try {
				putMessage(gameUtils.quotes.getTradeHistory());
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
		putMessage(new MLoginCompleted(loginSuccess, -2, "projector", "projector", false));
	}
	
	ProjectorMsgBacks projectorMsgBacks = new ProjectorMsgBacks();
	
	class ProjectorMsgBacks {
		public PrivateKey getPrivateKey() {
			return keys.getPrivate();
		}
		
		public void loginSuccess(boolean success) {
			loginCompleted = true;
			loginSuccess = success;
		}
		
		public void refreshPastTrades() {
			refreshPastTrades = true;
		}
	}
	
	protected boolean loginCompleted = false;
	protected boolean loginSuccess = false;
	
	protected boolean refreshPastTrades = false;
}
