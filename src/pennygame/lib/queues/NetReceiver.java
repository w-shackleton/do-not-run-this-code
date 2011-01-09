package pennygame.lib.queues;

import java.io.IOException;
import java.io.Reader;

import pennygame.lib.ext.Serialiser;
import pennygame.lib.queues.handlers.OnConnectionLostListener;

public class NetReceiver extends MessageProducer {
	protected final Reader inStream;
	protected final OnConnectionLostListener connectionLostListener;
	
	public NetReceiver(Reader inStream, OnConnectionLostListener connectionLostListener)
	{
		super();
		this.inStream = inStream;
		this.connectionLostListener = connectionLostListener;
	}
	
	@Override
	protected void loop() {
		try {
			putMessage(Serialiser.decode(inStream));
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
	}
}
