package pennygame.client.queues;

import pennygame.client.PennyFrame;
import pennygame.lib.clientutils.SConn;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.handlers.OnConnectionListener;
import pennygame.lib.queues.handlers.OnLoginHandler;


public class CSConn extends SConn<CSConnMainThread, CSConnPushHandler> {
	protected final OnLoginHandler loginHandler;
	protected final OnConnectionListener connectionListener;

	public CSConn(String server, int port, String username, String password, OnLoginHandler loginHandler, OnConnectionListener connectionListener) {
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
	protected CSConnMainThread createMainThread(String threadID) {
		return new CSConnMainThread(username, password, threadID);
	}

	@Override
	protected CSConnPushHandler createPushHandler(NetReceiver nr, String threadID) {
		return new CSConnPushHandler(nr, this, threadID, mainThread.msgBacks, connectionEnder); // Give it loginHandler, so it can tell us when login is completed
	}
	
	PennyFrame frame = null;
	
	public void setParentFrame(PennyFrame frame) {
		this.frame = frame;
		pushHandler.setParentFrame(frame);
	}
	
	/**
	 * Puts a new quote into the system.
	 * @param type The type of quote, found in {@link MPutQuote}
	 * @param pennies Number of pennies to put
	 * @param bottles Number of bottles to put
	 */
	public void putQuote(int type, int pennies, int bottles) {
		sendMessage(new MPutQuote(type, pennies, bottles));
	}
}
