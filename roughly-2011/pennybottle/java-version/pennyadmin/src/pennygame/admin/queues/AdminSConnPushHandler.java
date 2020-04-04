package pennygame.admin.queues;

import pennygame.admin.AdminFrame;
import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.msg.MTradesList;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.adm.MAGameSetting;
import pennygame.lib.msg.adm.MAUserList;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.lib.queues.handlers.OnLoginHandler;

/**
 * Incoming connection handler from the Server to this Admin
 * @author william
 *
 */
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
		else if(cls.equals(MTradesList.class)) {
			if(frame != null) {
				frame.tradesTab.setPastTrades(((MTradesList)msg).getTrades());
			}
		}
		
		else if(cls.equals(MAGameSetting.class)) {
			MAGameSetting s = (MAGameSetting) msg;
			
			if(!s.setOrGet()) {
				switch(s.what()) {
				case MAGameSetting.WHAT_QUOTE_TIMEOUT:
					frame.setQuoteTimeout((Integer) s.getData());
					break;
				case MAGameSetting.WHAT_QUOTE_NUMBER:
					frame.setQuoteNumber((Integer) s.getData());
					break;
				case MAGameSetting.WHAT_GRAPH_LENGTH_MINUTES:
					frame.otherTab.setGraphLength((Integer) s.getData());
					break;
				}
			}
		}
	}
}
