package pennygame.server.projector;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.queues.LoopingThread;
import pennygame.server.db.GameUtils;

public class ProjectionServer extends LoopingThread {
	ProjectorConn projector = null;
	final ServerSocket serv;
	final GameUtils gameUtils;
	final String pass;

	public ProjectionServer(GameUtils gameUtils, String listenAddress, int listenPort, String projectorPass) throws IOException {
		super("Projection Server monitor");
		GlobalPreferences.setProjectorPort(listenPort);
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
		if(projector == null)
		{
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
			projector = new ProjectorConn(sock, this, gameUtils, pass);
			projector.start();
		} else
			try {
				Thread.sleep(1000); // Wait around for a bit until it needs respawning
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	@Override
	protected void finish() {
		System.out.println("Projector Server Closed");
	}
	
	void onSessionEnd() {
		projector = null; // Respawn
	}
	
	@Override
	public void beginStopping() {
		super.beginStopping();
		
		try {
			serv.close();
		} catch (IOException e) {
		}
		
		if(projector != null)
		{
			projector.stop();
		}
	}
	
	public void refreshTradeGraph() {
		if(projector == null) return;
		projector.refreshTradeGraph();
	}
}
