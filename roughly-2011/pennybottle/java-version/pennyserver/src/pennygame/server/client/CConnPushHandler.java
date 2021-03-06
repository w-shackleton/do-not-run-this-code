package pennygame.server.client;

import java.sql.SQLException;

import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MChangeMyName;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.MRefresher;
import pennygame.lib.msg.MUpdateGWealth;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.data.User;
import pennygame.lib.msg.tr.MTAccept;
import pennygame.lib.msg.tr.MTAcceptResponse;
import pennygame.lib.msg.tr.MTCancel;
import pennygame.lib.msg.tr.MTCancelResponse;
import pennygame.lib.msg.tr.MTRequest;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.server.db.GameUtils;

/**
 * <p>Incoming connection handler from a Client to this Server</p>
 * <p>Perhaps this should be made non-threaded, and pass its messages to the other main thread (too many...)</p>
 * @author william
 *
 */

public class CConnPushHandler extends PushHandler {
	private final CConnMainThread.CConnMsgBacks cConnMsgBacks;
	
	private final GameUtils gameUtils;
	private final ConnectionEnder connEnder;
	
	private User user = null;
	
	public CConnPushHandler(NetReceiver producer, String threadID, CConnMainThread.CConnMsgBacks msgBacks, ConnectionEnder connEnder, GameUtils gameUtils) {
		super(producer, threadID);
		cConnMsgBacks = msgBacks;
		this.gameUtils = gameUtils;
		this.connEnder = connEnder;
	}

	protected boolean loggedIn = false;

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MLoginRequest.class)) // Only once (contains RSA key to use)
		{
			MLoginRequest logReq = (MLoginRequest) msg;
			byte[] hashText = PasswordUtils.decryptPassword(cConnMsgBacks.getPrivateKey(), logReq.pass);
			try {
				user = gameUtils.users.checkLogin(logReq.username, hashText);
				if(user != null) { // User is valid
					cConnMsgBacks.loginSuccess(true, user.getId(), logReq.username, user.getFriendlyname());
					loggedIn = true;
				}
				else {
					cConnMsgBacks.loginSuccess(false, -1, logReq.username, "");
					try {
						Thread.sleep(1000); // Leave a bit of time for message to get through
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					connEnder.endConnection();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(!loggedIn) return; // To stop messages getting through before login completed
		
		if(cls.equals(MPutQuote.class)) {
			MPutQuote req = (MPutQuote) msg;
			try {
				if(!gameUtils.quotes.putQuote(req.getType(), user.getId(), req.getPennies(), req.getBottles())) {
					// Not enough money to put up quote
					cConnMsgBacks.errorPuttingQuote = req.getType();
				} else {
					cConnMsgBacks.resendMyInfoList = true;
					cConnMsgBacks.resendMyQuotesList = true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		else if(cls.equals(MRefresher.class)) { // Refresh signals
			switch(((MRefresher)msg).what) {
			case MRefresher.REF_OPENQUOTELIST:
				cConnMsgBacks.resendOpenQuotesList = true;
				break;
			case MRefresher.REF_MYINFO:
				cConnMsgBacks.resendMyInfoList = true;
				break;
			case MRefresher.REF_MYQUOTES:
				cConnMsgBacks.resendMyQuotesList = true;
				break;
			}
		}
		else if(cls.equals(MChangeMyName.class)) {
			try {
				gameUtils.users.changeFriendlyName(user.getId(), ((MChangeMyName)msg).getNewName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if(cls.equals(MTRequest.class)) {
			MTRequest tMsg = (MTRequest) msg;
			boolean completed = false;
			try {
				completed = gameUtils.quotes.requestLockQuote(user.getId(), tMsg.getQuoteId());
			} catch (SQLException e1) { e1.printStackTrace(); }
			if(completed) {
				try {
					cConnMsgBacks.sendQuoteRequestResponse(tMsg.getQuoteId(), gameUtils.quotes.getQuoteInfo(tMsg.getQuoteId()));
				} catch (SQLException e) { e.printStackTrace(); }
			} else {
					cConnMsgBacks.sendQuoteRequestResponse(tMsg.getQuoteId(), null);
			}
		}
		else if(cls.equals(MTAccept.class)) {
			MTAccept tMsg = (MTAccept) msg;
			int retVal = MTAcceptResponse.ACCEPT_QUOTE_FAIL;
			try {
				retVal = gameUtils.quotes.acceptLockedQuote(user.getId(), tMsg.getQuoteId(), tMsg.isAccepted());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			switch(retVal) {
			case MTAcceptResponse.ACCEPT_QUOTE_SUCCESS:
				break;
			case MTAcceptResponse.ACCEPT_QUOTE_FAIL:
			case MTAcceptResponse.ACCEPT_QUOTE_GAME_PAUSED:
			case MTAcceptResponse.ACCEPT_QUOTE_NOMONEY:
				cConnMsgBacks.sendQuoteAcceptResponse(tMsg.getQuoteId(), retVal);
				break;
			}
		}
		else if(cls.equals(MUpdateGWealth.class)) {
			MUpdateGWealth uMsg = (MUpdateGWealth) msg;
			try {
				gameUtils.users.updateGuessedWorth(user.getId(), uMsg.getgWorth());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if(cls.equals(MTCancel.class)) {
			
			try {
				int status = gameUtils.quotes.cancelOpenQuote(user.getId(), ((MTCancel)msg).getQuoteId());
				if(status == MTCancelResponse.RESPONSE_ALREADY_TAKEN) {
					cConnMsgBacks.sendQuoteCancelFailResponse(((MTCancel)msg).getQuoteId(), status);
				}
				else if(status == MTCancelResponse.RESPONSE_GAME_PAUSED) {
					cConnMsgBacks.sendQuoteCancelFailResponse(((MTCancel)msg).getQuoteId(), status);
				}
				else {
					cConnMsgBacks.resendMyQuotesList = true;
					cConnMsgBacks.resendMyInfoList = true;
					gameUtils.quotes.pushOpenQuotes();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
