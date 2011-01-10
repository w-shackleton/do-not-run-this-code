package pennygame.server.client;

import pennygame.lib.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;

/**
 * Perhaps this should be made non-threadded, and pass its messages to the other main thread (too many...)
 * @author william
 *
 */
public class CConnPushHandler extends PushHandler {
	public CConnPushHandler(NetReceiver producer) {
		super(producer);
	}

	@Override
	protected void processMessage(PennyMessage msg) {
		System.out.println("Received msg " + msg.id + " with txt: " + msg.message);
	}

	@Override
	protected void setup() {
		// Do nowt.
	}
}
