package pennygame.lib.clientutils;

import pennygame.lib.msg.MLoginInitiate;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class SConnPushHandler extends PushHandler {
	protected final OnLoginHandler loginHandler;
	
	protected final SConnMainThread.MsgBacks msgBacks;

	public SConnPushHandler(NetReceiver producer, OnLoginHandler loginHandler, String threadID, SConnMainThread.MsgBacks msgBacks) {
		super(producer, threadID);
		this.loginHandler = loginHandler;
		this.msgBacks = msgBacks;
	}

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MLoginInitiate.class)) // Only once (contains RSA key to use)
		{
			System.out.println("Received RSA key, sending password");
			msgBacks.onLoginKey(((MLoginInitiate)msg).publicKey);
		}
	}
}
