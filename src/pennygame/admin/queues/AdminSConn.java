package pennygame.admin.queues;

import pennygame.lib.clientutils.SConn;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.handlers.OnConnectionListener;
import pennygame.lib.queues.handlers.OnLoginHandler;

public class AdminSConn extends SConn<AdminSConnMainThread, AdminSConnPushHandler> {
	protected final OnLoginHandler loginHandler;
	protected final OnConnectionListener connectionListener;

	public AdminSConn(String server, int port, String username, String password, OnLoginHandler loginHandler, OnConnectionListener connectionListener) {
		super(server, port, username, password);
		this.loginHandler = loginHandler;
		this.connectionListener = connectionListener;
	}

	@Override
	public void onLoginCompleted() {
		
		loginHandler.onLoginCompleted();
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

}
