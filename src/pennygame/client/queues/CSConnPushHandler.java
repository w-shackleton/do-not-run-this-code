package pennygame.client.queues;

import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class CSConnPushHandler extends SConnPushHandler {

	public CSConnPushHandler(NetReceiver producer, OnLoginHandler loginHandler,
			String threadID, MsgBacks msgBacks) {
		super(producer, loginHandler, threadID, msgBacks);
	}

}
