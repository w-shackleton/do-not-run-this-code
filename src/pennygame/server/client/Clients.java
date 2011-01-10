package pennygame.server.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ConcurrentHashMap;

import pennygame.lib.GlobalPreferences;

public final class Clients extends Thread {
	final ConcurrentHashMap<Integer, CConn> clients;
	int topId = 0;
	final ServerSocket serv;
	private boolean stopping = false;
	
	/**
	 * Creates a new list of clients which will accept connections.
	 * @throws IOException if the new socket can't be created
	 */
	public Clients() throws IOException {
		serv = new ServerSocket(GlobalPreferences.getPort());
		clients = new ConcurrentHashMap<Integer, CConn>();
		serv.setSoTimeout(1000); // To allow server to stop
	}
	
	@Override
	public void run()
	{
		while(!stopping) {
			synchronized(serv) {
				try {
					Socket sock = serv.accept();
					
					CConn client = new CConn(sock, this);
					clients.putIfAbsent(topId++, client);
					client.start(); // Start!
					System.out.println("Client connected, starting next...");
					
				} catch (SocketTimeoutException e1) {
					// Do nothing; we just want to reloop to allow stopping to take effect
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void startStopping() {
		// TODO: Close and clean up properly!
		stopping = true;
	}

	public synchronized void onClientEnd(CConn client) {
		clients.remove(client);
		System.out.println("Removed dead client");
	}
}
