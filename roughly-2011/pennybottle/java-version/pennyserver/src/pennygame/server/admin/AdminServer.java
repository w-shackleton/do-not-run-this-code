package pennygame.server.admin;

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

/**
 * Manages connections to admins, and listens for new ones
 * @author william
 *
 */
public class AdminServer extends LoopingThread {
	final ConcurrentHashMap<Integer, AdminConn> admins;
	int topId = 0;
	final ServerSocket serv;
	final GameUtils gameUtils;
	final String adminPass;

	// TODO: Now that multiple admins can connect at once, add messages to make sure they stay in sync!
	public AdminServer(GameUtils gameUtils, String listenAddress, int listenPort, String adminPass) throws IOException {
		super("Admin Server monitor");
		GlobalPreferences.setAdminport(listenPort);
		admins = new ConcurrentHashMap<Integer, AdminConn>();
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
		AdminConn admin = new AdminConn(sock, this, topId, gameUtils, adminPass);
		admins.put(topId++, admin);
		admin.start();
	}

	@Override
	protected void finish() {
		System.out.println("Admin Server Closed");
	}
	
	synchronized void onClientEnd(AdminConn admin) {
		admins.remove(admin);
		System.out.println("Removed dead admin");
		Runtime.getRuntime().gc();
	}
	
	@Override
	public void beginStopping() {
		super.beginStopping();
		
		try {
			serv.close();
		} catch (IOException e) {
		}
		
		Collection<AdminConn> clientSet = admins.values();
		Iterator<AdminConn> iter = clientSet.iterator();
		while(iter.hasNext())
		{
			AdminConn admin = iter.next();
			admin.stop();
			admins.remove(admin);
		}
	}
}
