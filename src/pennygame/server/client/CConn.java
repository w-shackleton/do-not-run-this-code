package pennygame.server.client;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;

public class CConn extends QueuePair<CConnMainThread, CConnPushHandler> {
	protected Clients parent;

	public CConn(Socket sock, Clients parent) {
		super(sock);
		this.parent = parent;
	}

	@Override
	protected CConnMainThread createMainThread() {
		CConnMainThread cMainThread = new CConnMainThread();
		return cMainThread;
	}

	@Override
	protected CConnPushHandler createPushHandler(NetReceiver nr) {
		CConnPushHandler cPushHandler = new CConnPushHandler(nr);
		return cPushHandler;
	}
	
	@Override
	public synchronized void onConnectionLost() {
		super.onConnectionLost();
		parent.onClientEnd(this); // Tell parent to delete me (to be gc'd)!
	}
}
