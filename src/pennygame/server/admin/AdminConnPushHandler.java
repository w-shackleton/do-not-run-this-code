package pennygame.server.admin;

import java.sql.SQLException;

import pennygame.lib.ext.Base64;
import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.MLoginRequest;
import pennygame.lib.msg.MRefresher;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.adm.MAGamePause;
import pennygame.lib.msg.adm.MAGameSetting;
import pennygame.lib.msg.adm.MAUserAdd;
import pennygame.lib.msg.adm.MAUserModifyRequest;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.PushHandler;
import pennygame.lib.queues.QueuePair.ConnectionEnder;
import pennygame.server.db.GameUtils;

public class AdminConnPushHandler extends PushHandler {
	final AdminConnMainThread.AdminMsgBacks adminMsgBacks;

	protected final ConnectionEnder connEnder;

	protected final GameUtils gameUtils;

	public AdminConnPushHandler(NetReceiver producer, String threadID, AdminConnMainThread.AdminMsgBacks msgBacks,
			ConnectionEnder connEnder, GameUtils gameUtils) {
		super(producer, threadID);
		adminMsgBacks = msgBacks;
		this.connEnder = connEnder;
		this.gameUtils = gameUtils;
	}

	protected boolean loggedIn = false;

	@Override
	protected void processMessage(PennyMessage msg) {
		Class<? extends PennyMessage> cls = msg.getClass();
		if (cls.equals(MLoginRequest.class)) // Only once (contains RSA key to
												// use)
		{
			MLoginRequest logReq = (MLoginRequest) msg;
			byte[] hashText = PasswordUtils.decryptPassword(adminMsgBacks.getPrivateKey(), logReq.pass);
			String hashedPass = Base64.encodeBytes(hashText);
			if (hashedPass.equals("nU4eI71bcnBGqeO0t9tXvY1u5oQ=") && logReq.username.equals("admin"))// Current
																										// pass
																										// is
																										// 'pass'
			{
				adminMsgBacks.loginSuccess(true);
				loggedIn = true;
			} else {
				adminMsgBacks.loginSuccess(false);
				try {
					Thread.sleep(1000); // Leave a bit of time for message to
										// get through
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				connEnder.endConnection();
			}
		}
		if (!loggedIn)
			return; // Don't do anything until so.

		if (cls.equals(MAUserAdd.class)) {
			System.out.println("Adding new user to DB");
			MAUserAdd usrReq = (MAUserAdd) msg;
			byte[] hashText = PasswordUtils.decryptPassword(adminMsgBacks.getPrivateKey(), usrReq.newPassword);

			try {
				System.out.println("222");
				gameUtils.users.createUser(usrReq.user.getUsername(), hashText, usrReq.user.getPennies(),
						usrReq.user.getBottles());
				System.out.println("222");
			} catch (SQLException e) {
				System.out.println("Couldn't create user!");
				e.printStackTrace();
			}

			adminMsgBacks.refreshUserList();
		} else if (cls.equals(MRefresher.class)) {
			int what = ((MRefresher) msg).what;
			switch (what) {
			case MRefresher.REF_USERLIST:
				adminMsgBacks.refreshUserList();
				break;
			}
		} else if (cls.equals(MAUserModifyRequest.class)) {
			MAUserModifyRequest usrReq = (MAUserModifyRequest) msg;

			switch (usrReq.getAction()) {
			case MAUserModifyRequest.DELETE_USER:
				try {
					gameUtils.users.deleteUser(usrReq.getId());
					adminMsgBacks.refreshUserList();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case MAUserModifyRequest.CHANGE_PASSWORD:
				try {
					gameUtils.users.changePassword(usrReq.getId(),
							PasswordUtils.decryptPassword(adminMsgBacks.getPrivateKey(), (byte[]) usrReq.getData()));
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case MAUserModifyRequest.CHANGE_FRIENDLYNAME:
				try {
					gameUtils.users.changeFriendlyName(usrReq.getId(), (String) usrReq.getData());
					adminMsgBacks.refreshUserList();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
		} else if (cls.equals(MAGamePause.class)) {
			gameUtils.pauseGame(((MAGamePause) msg).shouldPause());
		} else if(cls.equals(MAGameSetting.class)) {
			
			MAGameSetting s = (MAGameSetting) msg;
			
			switch(s.what()) {
			case MAGameSetting.WHAT_QUOTE_TIMEOUT:
				if(s.setOrGet())
					gameUtils.quotes.setQuoteTimeout((Integer) s.getData());
				else
					adminMsgBacks.sendQuoteTimeout(gameUtils.quotes.getQuoteTimeout());
				break;
			case MAGameSetting.WHAT_QUOTE_NUMBER:
				if(s.setOrGet())
					gameUtils.quotes.setNumQuotes((Integer) s.getData());
				else
					adminMsgBacks.sendNumQuotes(gameUtils.quotes.getNumQuotes());
				break;
			case MAGameSetting.WHAT_RESET_GAME:
				try {
					gameUtils.resetGame();
					adminMsgBacks.refreshUserList();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
