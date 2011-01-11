package pennygame.server.admin;

import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;

public class AdminConnPushHandler extends PushHandler {

	public AdminConnPushHandler(NetReceiver producer, String threadID) {
		super(producer, threadID);
	}

	@Override
	protected void processMessage(PennyMessage msg) {

	}
}
