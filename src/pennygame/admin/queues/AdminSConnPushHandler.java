package pennygame.admin.queues;

import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class AdminSConnPushHandler extends SConnPushHandler {

	public AdminSConnPushHandler(NetReceiver producer,
			OnLoginHandler loginHandler, String threadID, MsgBacks msgBacks) {
		super(producer, loginHandler, threadID, msgBacks);
	}
}
