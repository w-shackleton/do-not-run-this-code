package pennygame.server.projector;

import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;
import pennygame.server.db.GameUtils;

/**
 * A connection to a Projector.
 * @author william
 *
 */
public class ProjectorConn extends
		QueuePair<ProjectorConnMainThread, ProjectorConnPushHandler> {
	final ProjectorServer parent;
	final GameUtils gameUtils;
	final String pass;

	public ProjectorConn(Socket sock, ProjectorServer parent, int id, GameUtils gameUtils, String projectorPass) {
		super(sock, id);
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
		parent.onClientEnd(this);
	}
	
	public void refreshTradeGraph() {
		mainThread.refreshPastTrades = true;
	}
}
