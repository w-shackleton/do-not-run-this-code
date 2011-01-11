package pennygame.lib.clientutils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import pennygame.lib.queues.QueuePair;
import pennygame.lib.queues.handlers.OnLoginHandler;

/**
 * This is a connection to the server; it should be implemented over by the client, admin interface and fullscreen viewer
 * @author william
 *
 */
public abstract class SConn<P extends SConnMainThread, C extends SConnPushHandler> extends QueuePair<P, C> implements OnLoginHandler {
	
	protected final String server;
	protected final int port;
	
	protected final String username;
	protected final String password;

	public SConn(String server, int port, String username, String password) {
		super(new Socket(), 0); // No ID here as only 1 needed
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public synchronized void start() {
		if(server == null || server.equals("") || port == 0)
		{
			onConnectionLost();
			return;
		}
		try {
			socket.connect(new InetSocketAddress(server, port), 3000);
		} catch (IOException e) {
			System.out.println("Could not connect!");
			e.printStackTrace();
			onConnectionLost();
			return;
		}
		super.start();
		
		onConnected();
	}
	
	public synchronized void asyncStart() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				start();
			}
		});
		t.start();
	}
}
