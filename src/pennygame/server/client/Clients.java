package pennygame.server.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import pennygame.lib.GlobalPreferences;
import pennygame.server.db.GameUtils;

public final class Clients extends Thread {
	final ConcurrentHashMap<Integer, CConn> clients;
	int topId = 0;
	final ServerSocket serv;
	private boolean stopping = false;
	
	private final GameUtils gameUtils;
	
	/**
	 * Creates a new list of clients which will accept connections.
	 * @throws IOException if the new socket can't be created
	 */
	public Clients(GameUtils gameUtils) throws IOException {
		super("Connection starter");
		serv = new ServerSocket(GlobalPreferences.getPort());
		clients = new ConcurrentHashMap<Integer, CConn>();
		serv.setSoTimeout(1000); // To allow server to stop
		
		this.gameUtils = gameUtils;
	}
	
	@Override
	public void run()
	{
		while(!stopping) {
			synchronized(serv) {
				try {
					Socket sock = serv.accept();
					
					CConn client = new CConn(sock, this, topId, gameUtils);
					clients.putIfAbsent(topId++, client);
					client.start(); // Start!
					System.out.println("Client connected, starting next...");
					
				} catch (SocketTimeoutException e1) {
					// Do nothing; we just want to reloop to allow stopping to take effect
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
		System.out.println("Client connector stopped, waiting for clients to die");
	}
	
	public void beginStopping() {
		stopping = true;
		try {
			serv.close();
		} catch (IOException e) {
		}
		
		Collection<CConn> clientSet = clients.values();
		Iterator<CConn> iter = clientSet.iterator();
		while(iter.hasNext())
		{
			CConn client = iter.next();
			client.stop();
			clients.remove(client);
		}
	}

	public synchronized void onClientEnd(CConn client) {
		clients.remove(client);
		System.out.println("Removed dead client");
		Runtime.getRuntime().gc();
	}
}
