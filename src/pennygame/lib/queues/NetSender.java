package pennygame.lib.queues;

import java.io.IOException;
import java.io.Writer;

import pennygame.lib.PennyMessage;
import pennygame.lib.ext.Serialiser;
import pennygame.lib.queues.handlers.OnConnectionLostListener;

public class NetSender<T extends MainThread> extends MessageConsumer<T> {
	protected final Writer outStream;
	protected final OnConnectionLostListener connectionLostListener;
	
	private long previousTime;
	
	public NetSender(T producer, Writer outStream, OnConnectionLostListener connectionLostListener) {
		super(producer);
		this.outStream = outStream;
		this.connectionLostListener = connectionLostListener;
		previousTime = System.currentTimeMillis();
	}

	@Override
	protected void loop() {
		try {
			keepAlive();
		} catch (IOException e1) {
			System.out.println("ERROR: Could not send keepalive out! (probably means client has disconnected)");
			if(!stopping) // When we are stopping IO errors will probably occur
				connectionLostListener.onConnectionLost(); // Try and reconnect / safely kill.
		}
		
		PennyMessage msg = producer.getMessage();
		if (msg == null) {
			System.out.println("NetSender received a blank message...");
			return; // Somehow a blank message got in here; oh well.
		}

		try {
			Serialiser.encode(msg, outStream);
		} catch (IOException e) {
			System.out.println("ERROR: Could not send message out! (probably means client has disconnected)");
			if(!stopping) // When we are stopping IO errors will probably occur
				connectionLostListener.onConnectionLost(); // Try and reconnect / safely kill.
		}
	}
	
	private void keepAlive() throws IOException
	{
		// TODO: FIX THE KEEPALIVE PACKET!!!
		if(Math.abs(previousTime - System.currentTimeMillis()) > 2000) // Every 2 seconds at most
		{
			previousTime = System.currentTimeMillis();
			outStream.write("*,");
			outStream.flush();
		}
	}
	
	@Override
	public synchronized void beginStopping() {
		super.beginStopping();
		synchronized(outStream)
		{
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void setup() {
		// No need
	}
}
