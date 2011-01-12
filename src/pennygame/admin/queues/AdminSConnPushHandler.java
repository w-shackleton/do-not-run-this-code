package pennygame.admin.queues;

import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class AdminSConnPushHandler extends SConnPushHandler {

	public AdminSConnPushHandler(NetReceiver producer,
			OnLoginHandler loginHandler, String threadID, MsgBacks msgBacks, ConnectionEnder connEnder) {
		super(producer, loginHandler, threadID, msgBacks, connEnder);
	}
}
