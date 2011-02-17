package pennygame.server.projector;

import pennygame.lib.ext.Base64;
import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.MRefresher;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.server.db.GameUtils;

/**
 * Incoming connection handler from a Projector to this Server
 * @author william
 *
 */
public class ProjectorConnPushHandler extends PushHandler {
	final ProjectorConnMainThread.ProjectorMsgBacks projectorMsgBacks;

	protected final ConnectionEnder connEnder;

	protected final GameUtils gameUtils;
	protected final String pass;

	public ProjectorConnPushHandler(NetReceiver producer, String threadID, ProjectorConnMainThread.ProjectorMsgBacks msgBacks,
			ConnectionEnder connEnder, GameUtils gameUtils, String adminPass) {
		super(producer, threadID);
		projectorMsgBacks = msgBacks;
		this.connEnder = connEnder;
		this.gameUtils = gameUtils;
		this.pass = adminPass;
	}

	protected boolean loggedIn = false;

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if (cls.equals(MLoginRequest.class)) // Only once (contains RSA key to
												// use)
		{
			MLoginRequest logReq = (MLoginRequest) msg;
			byte[] hashText = PasswordUtils.decryptPassword(projectorMsgBacks.getPrivateKey(), logReq.pass);
			String hashedPass = Base64.encodeBytes(hashText);
			if (hashedPass.equals(pass) && logReq.username.equals("projector"))
			{
				projectorMsgBacks.loginSuccess(true);
				loggedIn = true;
			} else {
				projectorMsgBacks.loginSuccess(false);
				try {
					Thread.sleep(1000); // Leave a bit of time for message to
										// get through
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				connEnder.endConnection();
			}
		}
		if (!loggedIn)
			return; // Don't do anything until so.

		if (cls.equals(MRefresher.class)) {
			int what = ((MRefresher) msg).what;
			switch (what) {
			case MRefresher.REF_PASTTRADES:
				projectorMsgBacks.refreshPastTrades();
				break;
			}
		}
	}
}
