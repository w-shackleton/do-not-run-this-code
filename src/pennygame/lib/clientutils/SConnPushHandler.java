package pennygame.lib.clientutils;

import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.msg.MLoginCompleted;
import pennygame.lib.msg.MLoginInitiate;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class SConnPushHandler extends PushHandler {
	protected final OnLoginHandler loginHandler;
	
	protected final MsgBacks msgBacks;
	protected final ConnectionEnder connEnder;

	public SConnPushHandler(NetReceiver producer, OnLoginHandler loginHandler, String threadID, MsgBacks msgBacks, ConnectionEnder connEnder) {
		super(producer, threadID);
		this.loginHandler = loginHandler;
		this.msgBacks = msgBacks;
		this.connEnder = connEnder;
	}

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MLoginInitiate.class)) { // Only once (contains RSA key to use)
			System.out.println("Received RSA key, sending password");
			msgBacks.onLoginKey(((MLoginInitiate)msg).publicKey);
		}
		else if(cls.equals(MLoginCompleted.class)) {
			MLoginCompleted lMsg = (MLoginCompleted) msg;
			System.out.println("Received login completion message");
			if(lMsg.success)
				loginHandler.onLoginCompleted();
			else
			{
				loginHandler.onLoginFailed();
				connEnder.endConnection();
			}
		}
	}
}
