package pennygame.server.admin;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;
import pennygame.server.db.GameUtils;

public class AdminConn extends
		QueuePair<AdminConnMainThread, AdminConnPushHandler> {
	final AdminServer parent;
	final GameUtils gameUtils;

	public AdminConn(Socket sock, AdminServer parent, GameUtils gameUtils) {
		super(sock, -1);
		this.parent = parent;
		this.gameUtils = gameUtils;
	}

	@Override
	public void onConnected() {

	}

	@Override
	protected AdminConnMainThread createMainThread(String threadIdentifier) {
		return new AdminConnMainThread(threadIdentifier, gameUtils);
	}

	@Override
	protected AdminConnPushHandler createPushHandler(NetReceiver nr,
			String threadIdentifier) {
		return new AdminConnPushHandler(nr, threadIdentifier, mainThread.adminMsgBacks, connectionEnder, gameUtils);
	}

	@Override
	public synchronized void onConnectionLost() {
		super.onConnectionLost();
		parent.onSessionEnd(); // Tell parent to delete me (to be gc'd)!
	}
}
