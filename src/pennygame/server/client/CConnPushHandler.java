package pennygame.server.client;

import java.sql.SQLException;

import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.PennyMessage;
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
				if(gameUtils.users.checkLogin(logReq.username, hashText)) { // User is valid
					cConnMsgBacks.loginSuccess(true, logReq.username);
					loggedIn = true;
				}
				else {
					cConnMsgBacks.loginSuccess(false, logReq.username);
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
		
		if(!loggedIn) return;
	}
}
