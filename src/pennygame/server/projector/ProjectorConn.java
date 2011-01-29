package pennygame.server.projector;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;
import pennygame.server.db.GameUtils;

public class ProjectorConn extends
		QueuePair<ProjectorConnMainThread, ProjectorConnPushHandler> {
	final ProjectionServer parent;
	final GameUtils gameUtils;
	final String pass;

	public ProjectorConn(Socket sock, ProjectionServer parent, GameUtils gameUtils, String projectorPass) {
		super(sock, -1);
		this.parent = parent;
		this.gameUtils = gameUtils;
		this.pass = projectorPass;
	}

	@Override
	public void onConnected() {

	}

	@Override
	protected ProjectorConnMainThread createMainThread(String threadIdentifier) {
		return new ProjectorConnMainThread(threadIdentifier, gameUtils);
	}

	@Override
	protected ProjectorConnPushHandler createPushHandler(NetReceiver nr,
			String threadIdentifier) {
		return new ProjectorConnPushHandler(nr, threadIdentifier, mainThread.projectorMsgBacks, connectionEnder, gameUtils, pass);
	}

	@Override
	public synchronized void onConnectionLost() {
		super.onConnectionLost();
		parent.onSessionEnd(); // Tell parent to delete me (to be gc'd)!
	}
	
	public void refreshTradeGraph() {
		mainThread.refreshPastTrades = true;
	}
}
