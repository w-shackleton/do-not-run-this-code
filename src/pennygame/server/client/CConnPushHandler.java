package pennygame.server.client;

import java.sql.SQLException;

import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MChangeMyName;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.MRefresher;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.data.User;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.server.db.GameUtils;

/**
 * Perhaps this should be made non-threaded, and pass its messages to the other main thread (too many...)
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
				gameUtils.quotes.putQuote(req.getType(), user.getId(), req.getPennies(), req.getBottles());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		else if(cls.equals(MRefresher.class)) { // Refresh signals
			switch(((MRefresher)msg).what) {
			case MRefresher.REF_OPENQUOTELIST:
				cConnMsgBacks.resendOpenQuotesList = true;
			}
		}
		else if(cls.equals(MChangeMyName.class)) {
			try {
				gameUtils.users.changeFriendlyName(user.getId(), ((MChangeMyName)msg).getNewName());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
