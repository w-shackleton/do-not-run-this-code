package pennygame.server.client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.sql.SQLException;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.msg.MLoginCompleted;
import pennygame.lib.msg.MLoginInitiate;
import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.queues.MainThread;
import pennygame.server.db.GameUtils;

public class CConnMainThread extends MainThread {
	KeyPair keys;
	final GameUtils gameUtils;
	
	public CConnMainThread(String threadID, GameUtils gameUtils) {
		super(threadID);
		this.gameUtils = gameUtils;
	}
	
	@Override
	protected void loop() {
		if(cConnMsgBacks.resendOpenQuotesList) {
			cConnMsgBacks.resendOpenQuotesList = false;
			try {
				putMessage(new MOpenQuotesList(gameUtils.quotes.getOpenQuotes(), gameUtils.quotes.getTotalNumQuotes())); // Resend
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
		
		while(!loginCompleted && !stopping)
		{
			try {
				Thread.sleep(50); // Wait a bit for response
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		putMessage(new MLoginCompleted(loginSuccess, loginId, loginName, loginFriendlyName));
	}
	
	CConnMsgBacks cConnMsgBacks = new CConnMsgBacks();
	
	class CConnMsgBacks {
		public PrivateKey getPrivateKey() {
			return keys.getPrivate();
		}
		
		public void loginSuccess(boolean success, int userID, String userName, String friendlyName) {
			loginCompleted = true;
			loginSuccess = success;
			loginName = userName;
			loginFriendlyName = friendlyName;
			loginId = userID;
		}
		
		protected boolean resendOpenQuotesList = false;
	}
	
	protected boolean loginCompleted = false;
	protected boolean loginSuccess = false;
	protected String loginName = "", loginFriendlyName = "";
	protected int loginId = -1;
	
	protected void sendSerialisedMessage(String m) {
		putMessage(m);
	}
}
