package pennygame.server.client;

import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;

/**
 * Perhaps this should be made non-threaded, and pass its messages to the other main thread (too many...)
 * @author william
 *
 */
public class CConnPushHandler extends PushHandler {
	public CConnPushHandler(NetReceiver producer, String threadID) {
		super(producer, threadID);
	}

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MLoginRequest.class)) // Only once (contains RSA key to use)
		{
			System.out.println("Password received, processing");
		}
	}

	@Override
	protected void setup() {
		// Do nowt.
	}
}
