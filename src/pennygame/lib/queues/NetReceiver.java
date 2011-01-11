package pennygame.lib.queues;

import java.io.IOException;
import java.io.Reader;

import pennygame.lib.ext.Serialiser;
import pennygame.lib.queues.handlers.OnConnectionListener;

public class NetReceiver extends MessageProducer {
	protected final Reader inStream;
	protected final OnConnectionListener connectionLostListener;
	
	public NetReceiver(Reader inStream, OnConnectionListener connectionLostListener, String threadID)
	{
		super(threadID);
		this.inStream = inStream;
		this.connectionLostListener = connectionLostListener;
	}
	
	@Override
	protected void loop() {
		try {
			putMessage(Serialiser.decode(inStream, serialiserStopper));
		} catch (IOException e) {
			System.out.println("ERROR: Could not receive message!");
			e.printStackTrace();
			if(!stopping)
				connectionLostListener.onConnectionLost();
		}
	}

	@Override
	public synchronized void beginStopping() {
		super.beginStopping();
		serialiserStopper.stopping = true;
	}
	
	NetReceiverStopper serialiserStopper = new NetReceiverStopper();
	
	public static final class NetReceiverStopper
	{
		public boolean stopping = false;
	}
}
