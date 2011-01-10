package pennygame.lib.clientutils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import pennygame.lib.queues.NetReceiver;
import pennygame.lib.queues.QueuePair;

public class SConn extends QueuePair<SConnMainThread, SConnPushHandler> {
	
	protected final String server;
	protected final int port;
	
	private final String username, password;

	public SConn(String server, int port, String username, String password) {
		super(new Socket());
		this.server = server;
		this.port = port;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public synchronized void start() {
		try {
			socket.connect(new InetSocketAddress(server, port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected SConnMainThread createMainThread() {
		return new SConnMainThread(username, password);
	}

	@Override
	protected SConnPushHandler createPushHandler(NetReceiver nr) {
		return new SConnPushHandler(nr);
	}

}
