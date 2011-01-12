package pennygame.server.admin;

import pennygame.lib.ext.Base64;
import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.QueuePair.ConnectionEnder;

public class AdminConnPushHandler extends PushHandler {
	final AdminConnMainThread.AdminMsgBacks adminMsgBacks;
	
	protected final ConnectionEnder connEnder;

	public AdminConnPushHandler(NetReceiver producer, String threadID, AdminConnMainThread.AdminMsgBacks msgBacks, ConnectionEnder connEnder) {
		super(producer, threadID);
		adminMsgBacks = msgBacks;
		this.connEnder = connEnder;
	}

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MLoginRequest.class)) // Only once (contains RSA key to use)
		{
			MLoginRequest logReq = (MLoginRequest) msg;
			byte[] hashText = PasswordUtils.decryptPassword(adminMsgBacks.getPrivateKey(), logReq.pass);
			String hashedPass = Base64.encodeBytes(hashText);
			if(hashedPass.equals("nU4eI71bcnBGqeO0t9tXvY1u5oQ=")) // Current pass is 'pass'
				adminMsgBacks.loginSuccess(true);
			else
			{
				adminMsgBacks.loginSuccess(false);
				try {
					Thread.sleep(1000); // Leave a bit of time for message to get through
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				connEnder.endConnection();
			}
		}
	}
}
