package pennygame.server.admin;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;

public class AdminConn extends
		QueuePair<AdminConnMainThread, AdminConnPushHandler> {
	final AdminServer parent;

	public AdminConn(Socket sock, AdminServer parent) {
		super(sock, -1);
		this.parent = parent;
	}

	@Override
	public void onConnected() {

	}

	@Override
	protected AdminConnMainThread createMainThread(String threadIdentifier) {
		return new AdminConnMainThread(threadIdentifier);
	}

	@Override
	protected AdminConnPushHandler createPushHandler(NetReceiver nr,
			String threadIdentifier) {
		return new AdminConnPushHandler(nr, threadIdentifier);
	}

	@Override
	public synchronized void onConnectionLost() {
		super.onConnectionLost();
		parent.onSessionEnd(); // Tell parent to delete me (to be gc'd)!
	}
}
