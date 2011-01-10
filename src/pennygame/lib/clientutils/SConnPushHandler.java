package pennygame.lib.clientutils;

import pennygame.lib.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;

public class SConnPushHandler extends PushHandler {

	public SConnPushHandler(NetReceiver producer) {
		super(producer);
	}

	@Override
	protected void processMessage(PennyMessage msg) {

	}
}
