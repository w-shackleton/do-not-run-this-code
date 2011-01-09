package pennygame.lib.queues;

import java.io.BufferedReader;
import java.io.IOException;

import pennygame.lib.ext.Serialiser;

public class NetReceiver extends MessageProducer {
	protected final BufferedReader inStream;
	private boolean connected = true;
	
	public NetReceiver(BufferedReader inStream)
	{
		super();
		this.inStream = inStream;
	}
	
	@Override
	protected void loop() {
		try {
			putMessage(Serialiser.decode(inStream));
		} catch (IOException e) {
			System.out.println("ERROR: Could not receive message!");
			e.printStackTrace();
			setConnected(false); // This COULD be the problem - PushHandler checks this
		}
	}

	private synchronized void setConnected(boolean connected) {
		this.connected = connected;
	}

	public synchronized boolean isConnected() {
		return connected;
	}
}
