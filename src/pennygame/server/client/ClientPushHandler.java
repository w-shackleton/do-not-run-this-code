package pennygame.server.client;

import pennygame.lib.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;

public class ClientPushHandler extends PushHandler {
	public ClientPushHandler(NetReceiver producer) {
		super(producer);
	}

	@Override
	protected void processMessage(PennyMessage msg) {
	}
}
