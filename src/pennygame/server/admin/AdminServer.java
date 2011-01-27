package pennygame.server.admin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.queues.LoopingThread;
import pennygame.server.db.GameUtils;

public class AdminServer extends LoopingThread {
	AdminConn admin = null;
	final ServerSocket serv;
	final GameUtils gameUtils;
	final String adminPass;

	public AdminServer(GameUtils gameUtils, String listenAddress, int listenPort, String adminPass) throws IOException {
		super("Admin Server monitor");
		GlobalPreferences.setAdminport(listenPort);
		serv = new ServerSocket(GlobalPreferences.getAdminport(), 0, InetAddress.getByName(listenAddress));
		serv.setSoTimeout(1000);
		this.gameUtils = gameUtils;
		this.adminPass = adminPass;
	}
	
	@Override
	protected void setup() {
		
	}

	@Override
	protected void loop() {
		if(admin == null)
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
			admin = new AdminConn(sock, this, gameUtils, adminPass);
			admin.start();
		} else
			try {
				Thread.sleep(1000); // Wait around for a bit until it needs respawning
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	@Override
	protected void finish() {
		System.out.println("Admin Server Closed");
	}
	
	void onSessionEnd() {
		admin = null; // Respawn
	}
	
	@Override
	public void beginStopping() {
		super.beginStopping();
		
		try {
			serv.close();
		} catch (IOException e) {
		}
		
		if(admin != null)
		{
			admin.stop();
		}
	}
}
