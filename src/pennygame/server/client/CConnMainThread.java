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
import pennygame.lib.msg.MOSMessage;
import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.lib.msg.tr.MTAcceptResponse;
import pennygame.lib.msg.tr.MTRequestResponse;
import pennygame.lib.queues.MainThread;
import pennygame.server.db.GameUtils;

public class CConnMainThread extends MainThread {
	KeyPair keys;
	final GameUtils gameUtils;
	final CConn parent;
	
	public CConnMainThread(String threadID, GameUtils gameUtils, CConn parent) {
		super(threadID);
		this.gameUtils = gameUtils;
		this.parent = parent;
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
		if(cConnMsgBacks.resendMyInfoList) {
			cConnMsgBacks.resendMyInfoList = false;
			try {
				putMessage(gameUtils.quotes.getUserMoneyMessage(parent.userId)); // TODO: Get user money worth
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(cConnMsgBacks.errorPuttingQuote != 0) {
			if(cConnMsgBacks.errorPuttingQuote == MPutQuote.TYPE_BUY) {
				putMessage(new MOSMessage(MOSMessage.QUOTE_ERROR_NOT_ENOUGH_PENNIES)); // Notify user
			} else {
				putMessage(new MOSMessage(MOSMessage.QUOTE_ERROR_NOT_ENOUGH_BOTTLES));
			}
			cConnMsgBacks.errorPuttingQuote = 0;
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
			if(loginSuccess)
				parent.setMyId(userID);
		}
		
		protected boolean resendOpenQuotesList = false;
		protected boolean resendMyInfoList = false;
		
		/**
		 * Signifies that there was an error putting up a quote.
		 * 0 means success, or a constant from {@link MPutQuote} signifies a fail with that item.
		 */
		protected int errorPuttingQuote = 0;
		
		/**
		 * Tell the client the result of their quote request
		 * @param quoteId
		 * @param quote
		 */
		protected void sendQuoteRequestResponse(int quoteId, OpenQuote quote) {
			if(quote == null) {
				putMessage(new MTRequestResponse(quoteId, false));
			} else {
				putMessage(new MTRequestResponse(quoteId, true, quote));
			}
		}
		
		protected void sendQuoteAcceptResponse(int quoteId, int status) {
			putMessage(new MTAcceptResponse(quoteId, status));
		}
	}
	
	protected boolean loginCompleted = false;
	protected boolean loginSuccess = false;
	protected String loginName = "", loginFriendlyName = "";
	protected int loginId = -1;
	
	protected void sendSerialisedMessage(String m) {
		putMessage(m);
	}
}
