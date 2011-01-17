package pennygame.client.queues;

import pennygame.client.PennyFrame;
import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPauseGame;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class CSConnPushHandler extends SConnPushHandler {
	PennyFrame frame;

	public CSConnPushHandler(NetReceiver producer, OnLoginHandler loginHandler,
			String threadID, MsgBacks msgBacks, ConnectionEnder connEnder) {
		super(producer, loginHandler, threadID, msgBacks, connEnder);
	}

	protected void setParentFrame(PennyFrame frame) {
		this.frame = frame;
	}
	
	
	@Override
	protected void processMessage(PennyMessage msg) {
		super.processMessage(msg);
		Class<? extends PennyMessage> cls = msg.getClass();
		
		if(cls.equals(MOpenQuotesList.class)) {
			System.out.println("New open quote list received");
			MOpenQuotesList q = (MOpenQuotesList) msg;
			frame.cp.updateOpenQuoteList(q.getList(), q.getTotal());
		}
		else if(cls.equals(MPauseGame.class)) {
			System.out.println("Pausing / resuming");
			frame.pauseGame(((MPauseGame)msg).isPaused());
		}
	}
}
