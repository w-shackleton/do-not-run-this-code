package pennygame.server.projector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.queues.LoopingThread;
import pennygame.server.db.GameUtils;

public class ProjectorServer extends LoopingThread {
	final ConcurrentHashMap<Integer, ProjectorConn> projectors;
	int topId = 0;
	final ServerSocket serv;
	final GameUtils gameUtils;
	final String pass;

	public ProjectorServer(GameUtils gameUtils, String listenAddress, int listenPort, String projectorPass) throws IOException {
		super("Projector Server monitor");
		GlobalPreferences.setProjectorPort(listenPort);
		projectors = new ConcurrentHashMap<Integer, ProjectorConn>();
		serv = new ServerSocket(GlobalPreferences.getProjectorPort(), 0, InetAddress.getByName(listenAddress));
		serv.setSoTimeout(1000);
		this.gameUtils = gameUtils;
		this.pass = projectorPass;
	}
	
	@Override
	protected void setup() {
		
	}

	@Override
	protected void loop() {
		Socket sock = null;
		while(sock == null && !stopping)
		{
			try {
				sock = serv.accept();
			} catch (SocketTimeoutException e1) {
				
			} catch (IOException e) {
				// e.printStackTrace();
			}
		}
		if(stopping) return;
		ProjectorConn projector = new ProjectorConn(sock, this, topId, gameUtils, pass);
		projectors.put(topId++, projector);
		projector.start();
	}

	@Override
	protected void finish() {
		System.out.println("Projector Server Closed");
	}
	
	synchronized void onClientEnd(ProjectorConn projector) {
		projectors.remove(projector);
		System.out.println("Removed dead projector");
		Runtime.getRuntime().gc();
	}
	
	@Override
	public void beginStopping() {
		super.beginStopping();
		
		try {
			serv.close();
		} catch (IOException e) {
		}
		
		Collection<ProjectorConn> clientSet = projectors.values();
		Iterator<ProjectorConn> iter = clientSet.iterator();
		while(iter.hasNext())
		{
			ProjectorConn admin = iter.next();
			admin.stop();
			projectors.remove(admin);
		}
	}
	
	public void refreshTradeGraph() {
		Iterator<ProjectorConn> it = projectors.values().iterator();
		
		while(it.hasNext()) {
			ProjectorConn projector = it.next();
			projector.refreshTradeGraph();
		}
	}
}
