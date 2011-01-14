package pennygame.admin.queues;

import pennygame.admin.AdminFrame;
import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.adm.MAUserList;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class AdminSConnPushHandler extends SConnPushHandler {
	AdminFrame frame;

	public AdminSConnPushHandler(NetReceiver producer,
			OnLoginHandler loginHandler, String threadID, MsgBacks msgBacks, ConnectionEnder connEnder) {
		super(producer, loginHandler, threadID, msgBacks, connEnder);
	}
	
	protected void setParentFrame(AdminFrame frame) {
		this.frame = frame;
	}
	
	@Override
	protected void processMessage(PennyMessage msg) {
		super.processMessage(msg);
		Class<? extends PennyMessage> cls = msg.getClass();
		if(cls.equals(MAUserList.class)) {
			if(frame != null) {
				frame.userTab.updateUserList(((MAUserList)msg).getUsers());
			}
		}
	}
}
