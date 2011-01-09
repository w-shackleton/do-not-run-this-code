package pennygame.server.client;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;

public class Client extends QueuePair<ClientMainThread, ClientPushHandler> {
	protected Clients parent;

	public Client(Socket sock, Clients parent) {
		super(sock);
		this.parent = parent;
	}

	@Override
	protected ClientMainThread createMainThread() {
		ClientMainThread cMainThread = new ClientMainThread();
		return cMainThread;
	}

	@Override
	protected ClientPushHandler createPushHandler(NetReceiver nr) {
		ClientPushHandler cPushHandler = new ClientPushHandler(nr);
		return cPushHandler;
	}
	
	@Override
	public synchronized void onConnectionLost() {
		super.onConnectionLost();
		parent.onClientEnd(this); // Tell parent to delete me!
	}
}
