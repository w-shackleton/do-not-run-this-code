package pennygame.server.client;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;
import pennygame.server.db.GameUtils;

public class CConn extends QueuePair<CConnMainThread, CConnPushHandler> {
	protected Clients parent;
	
	private final GameUtils gameUtils;
	
	int userId;

	public CConn(Socket sock, Clients parent, int id, GameUtils gameUtils) {
		super(sock, id);
		this.parent = parent;
		this.gameUtils = gameUtils;
	}

	@Override
	protected CConnMainThread createMainThread(String threadID) {
		CConnMainThread cMainThread = new CConnMainThread(threadID, gameUtils, this);
		return cMainThread;
	}

	@Override
	protected CConnPushHandler createPushHandler(NetReceiver nr, String threadID) {
		CConnPushHandler cPushHandler = new CConnPushHandler(nr, threadID, mainThread.cConnMsgBacks, connectionEnder, gameUtils);
		return cPushHandler;
	}
	
	@Override
	public synchronized void onConnectionLost() {
		super.onConnectionLost();
		parent.onClientEnd(this); // Tell parent to delete me (to be gc'd)!
	}
	
	/**
	 * Stops this connection to the client
	 */
	public synchronized void stopConnection() {
		onConnectionLost();
	}

	@Override
	public void onConnected() {
		// Not used
	}
	
	public void sendSerialisedMessage(String msg) {
		mainThread.sendSerialisedMessage(msg);
	}
	
	void setMyId(int id) {
		userId = id;
		parent.onClientLogin(this, userId);
	}
}
