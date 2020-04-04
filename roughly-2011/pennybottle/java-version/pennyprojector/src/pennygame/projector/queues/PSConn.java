package pennygame.projector.queues;

import pennygame.lib.clientutils.SConn;
import pennygame.lib.msg.MChangeMyName;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.MUpdateGWealth;
import pennygame.lib.msg.data.User;
import pennygame.lib.msg.tr.MTAccept;
import pennygame.lib.msg.tr.MTCancel;
import pennygame.lib.msg.tr.MTRequest;
import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.handlers.OnConnectionListener;
import pennygame.lib.queues.handlers.OnLoginHandler;
import pennygame.projector.ProjectorFrame;


/**
 * The connection to the Server from this Projector
 * @author william
 *
 */
public class PSConn extends SConn<PSConnMainThread, PSConnPushHandler> {
	protected final OnLoginHandler loginHandler;
	protected final OnConnectionListener connectionListener;
	
	public PSConn(String server, int port, String username, String password, OnLoginHandler loginHandler, OnConnectionListener connectionListener) {
		super(server, port, username, password);
		this.loginHandler = loginHandler;
		this.connectionListener = connectionListener;
	}

	@Override
	public void onLoginCompleted(User userInfo, boolean paused) {
		loginHandler.onLoginCompleted(userInfo, paused);
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
	protected PSConnMainThread createMainThread(String threadID) {
		return new PSConnMainThread(username, password, threadID);
	}

	@Override
	protected PSConnPushHandler createPushHandler(NetReceiver nr, String threadID) {
		return new PSConnPushHandler(nr, this, threadID, mainThread.msgBacks, connectionEnder); // Give it loginHandler, so it can tell us when login is completed
	}
	
	ProjectorFrame frame = null;
	
	public void setParentFrame(ProjectorFrame frame) {
		this.frame = frame;
		pushHandler.setParentFrame(frame);
		mainThread.refreshPastTrades = true; // List to graph
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
	
	public void changeMyName(String newName) {
		sendMessage(new MChangeMyName(newName));
	}
	
	/**
	 * Accepts the quote with the given ID number
	 * @param id
	 */
	public void acceptQuote(int id) {
		sendMessage(new MTRequest(id));
	}
	
	/**
	 * Cancels a currently open quote of the user.
	 * @param id
	 */
	public void cancelQuote(int id) {
		sendMessage(new MTCancel(id));
	}
	
	public void confirmAcceptQuote(int id, boolean accept) {
		sendMessage(new MTAccept(id, accept));
	}
	
	public void setWorthGuess(int guess) {
		sendMessage(new MUpdateGWealth(guess));
	}
}
