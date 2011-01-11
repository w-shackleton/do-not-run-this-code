package pennygame.server.admin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.queues.LoopingThread;

public class AdminServer extends LoopingThread {
	AdminConn admin = null;
	final ServerSocket serv;

	public AdminServer() throws IOException {
		super("Admin Server monitor");
		serv = new ServerSocket(GlobalPreferences.getAdminport());
		serv.setSoTimeout(1000);
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
			admin = new AdminConn(sock, this);
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
