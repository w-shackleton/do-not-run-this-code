package pennygame.client.queues;

import pennygame.client.PennyFrame;
import pennygame.lib.clientutils.SConnMainThread.MsgBacks;
import pennygame.lib.clientutils.SConnPushHandler;
import pennygame.lib.msg.MMyInfo;
import pennygame.lib.msg.MMyQuotesList;
import pennygame.lib.msg.MOSMessage;
import pennygame.lib.msg.MOpenQuotesList;
import pennygame.lib.msg.MPauseGame;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.tr.MTAcceptResponse;
import pennygame.lib.msg.tr.MTCancelResponse;
import pennygame.lib.msg.tr.MTRequestResponse;
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
		else if(cls.equals(MMyInfo.class)) {
			System.out.println("Refreshing user info");
			frame.lp.updateUserInfo((MMyInfo) msg);
		}
		else if(cls.equals(MMyQuotesList.class)) {
			System.out.println("Refreshing user quotes");
			frame.lp.updateUserQuotes((MMyQuotesList) msg);
		}
		else if(cls.equals(MOSMessage.class)) {
			MOSMessage mmsg = (MOSMessage) msg;
			
			switch(mmsg.getMessage()) {
			case MOSMessage.QUOTE_ERROR_NOT_ENOUGH_BOTTLES:
				frame.notifyUserOfError("Not enough potential bottles to place transaction\n" +
						"(Try deleting some quotes first)");
				break;
			case MOSMessage.QUOTE_ERROR_NOT_ENOUGH_PENNIES:
				frame.notifyUserOfError("Not enough potential pennies to place transaction\n" +
						"(Try deleting some quotes first)");
				break;
			}
		}
		
		else if(cls.equals(MTRequestResponse.class)) {
			MTRequestResponse rMsg = (MTRequestResponse) msg;
			if(rMsg.wasSuccessful()) {
				frame.askIfQuoteAccept(rMsg.getQuote());
			} else {
				frame.notifyUserOfError("Quote already taken");
			}
		}
		else if(cls.equals(MTAcceptResponse.class)) {
			MTAcceptResponse rMsg = (MTAcceptResponse) msg;
			switch(rMsg.getStatus()) {
			case MTAcceptResponse.ACCEPT_QUOTE_FAIL:
				frame.notifyUserOfError("Could not accept quote; unknown error occurred.");
				break;
			case MTAcceptResponse.ACCEPT_QUOTE_NOMONEY:
				frame.notifyUserOfError("Not enough money to accept quote");
				break;
			case MTAcceptResponse.ACCEPT_QUOTE_GAME_PAUSED:
				frame.notifyUserOfError("Game paused");
				break;
			}
		}
		else if(cls.equals(MTCancelResponse.class)) {
			MTCancelResponse cMsg = (MTCancelResponse) msg;
			if(cMsg.getResponse() == MTCancelResponse.RESPONSE_ALREADY_TAKEN) {
				frame.notifyUserOfError("Quote already taken whilst trying to cancel");
			}
			else if(cMsg.getResponse() == MTCancelResponse.RESPONSE_GAME_PAUSED) {
				frame.notifyUserOfError("Game Paused");
			}
		}
	}
}
