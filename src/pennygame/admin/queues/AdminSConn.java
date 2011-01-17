package pennygame.admin.queues;

import pennygame.admin.AdminFrame;
import pennygame.admin.CommInterfaces.CreateNewUser;
import pennygame.lib.Utils;
import pennygame.lib.clientutils.SConn;
import pennygame.lib.ext.PasswordUtils;
import pennygame.lib.msg.adm.MAGamePause;
import pennygame.lib.msg.adm.MAGameSetting;
import pennygame.lib.msg.adm.MAUserAdd;
import pennygame.lib.msg.adm.MAUserModifyRequest;
import pennygame.lib.msg.data.User;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.handlers.OnConnectionListener;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class AdminSConn extends SConn<AdminSConnMainThread, AdminSConnPushHandler> {
	protected final OnLoginHandler loginHandler;
	protected final OnConnectionListener connectionListener;
	
	protected AdminFrame frame;

	public AdminSConn(String server, int port, String username, String password, OnLoginHandler loginHandler, OnConnectionListener connectionListener) {
		super(server, port, username, password);
		this.loginHandler = loginHandler;
		this.connectionListener = connectionListener;
	}

	@Override
	public void onLoginCompleted(User userInfo) {
		
		loginHandler.onLoginCompleted(userInfo);
	}

	@Override
	public void onLoginFailed() {
		
		loginHandler.onLoginFailed();
	}

	@Override
	public void onConnected() {
		
		connectionListener.onConnected();
	}
	
	@Override
	public void onConnectionLost() {
		connectionListener.onConnectionLost();
		super.onConnectionLost();
	}

	@Override
	protected AdminSConnMainThread createMainThread(String threadIdentifier) {
		return new AdminSConnMainThread(username, password, threadIdentifier);
	}

	@Override
	protected AdminSConnPushHandler createPushHandler(NetReceiver nr,
			String threadIdentifier) {
		return new AdminSConnPushHandler(nr, loginHandler, threadIdentifier, mainThread.msgBacks, connectionEnder);
	}
	
	public CreateNewUser createNewUserHandler = new CreateNewUser() {
		
		@Override
		public void createNewUser(String username, String password, int bottles,
				int pennies) {
			MAUserAdd userReq = new MAUserAdd(username, PasswordUtils.encryptPassword(mainThread.rsaKey, password), pennies, bottles);
			
			sendMessage(userReq);
		}

		@Override
		public void changePassword(int id, String password) {
			sendMessage(new MAUserModifyRequest(id, MAUserModifyRequest.CHANGE_PASSWORD, PasswordUtils.encryptPassword(mainThread.rsaKey, password)));
		}
	};
	
	public void setParentFrame(AdminFrame frame) {
		this.frame = frame;
		pushHandler.setParentFrame(frame);
		mainThread.refreshUsers = true;
	}
	
	public void refreshUserList() {
		mainThread.refreshUsers = true;
	}
	
	public void modifyUser(int userId, int action) {
		sendMessage(new MAUserModifyRequest(userId, action));
	}
	
	public void modifyUser(int userId, int action, Object data) {
		sendMessage(new MAUserModifyRequest(userId, action, data));
	}
	
	public void pause(boolean pause) {
		sendMessage(new MAGamePause(pause));
	}
	
	public void setQuoteTimeout(int timeout) {
		timeout = Utils.trimMin(timeout, 10);
		sendMessage(new MAGameSetting(true, MAGameSetting.WHAT_QUOTE_TIMEOUT, timeout));
	}
	
	public void getQuoteTimeout() {
		sendMessage(new MAGameSetting(MAGameSetting.WHAT_QUOTE_TIMEOUT));
	}
	
	public void setQuoteNumber(int num) {
		num = Utils.trimMinMax(num, 5, 20);
		sendMessage(new MAGameSetting(true, MAGameSetting.WHAT_QUOTE_NUMBER, num));
	}
	
	public void getQuoteNumber() {
		sendMessage(new MAGameSetting(MAGameSetting.WHAT_QUOTE_NUMBER));
	}
}
