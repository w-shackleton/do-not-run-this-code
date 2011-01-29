package pennygame.projector.queues;

import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.msg.MTradesList;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.lib.queues.handlers.OnLoginHandler;
import pennygame.projector.ProjectorFrame;

public class PSConnPushHandler extends SConnPushHandler {
	ProjectorFrame frame;

	public PSConnPushHandler(NetReceiver producer, OnLoginHandler loginHandler,
			String threadID, MsgBacks msgBacks, ConnectionEnder connEnder) {
		super(producer, loginHandler, threadID, msgBacks, connEnder);
	}

	protected void setParentFrame(ProjectorFrame frame) {
		this.frame = frame;
	}
	
	
	@Override
	protected void processMessage(PennyMessage msg) {
		super.processMessage(msg);
		Class<? extends PennyMessage> cls = msg.getClass();
		
		if(cls.equals(MTradesList.class)) {
			MTradesList tMsg = (MTradesList) msg;
			frame.getGraph().setData(tMsg);
		}
	}
}
