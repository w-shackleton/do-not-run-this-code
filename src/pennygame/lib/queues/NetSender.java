package pennygame.lib.queues;

import java.io.BufferedWriter;
import java.io.IOException;

import pennygame.lib.PennyMessage;
import pennygame.lib.ext.Serialiser;

public class NetSender extends MessageConsumer<MainThread> {
	protected final BufferedWriter outStream;

	public NetSender(MainThread producer, BufferedWriter outStream) {
		super(producer);
		this.outStream = outStream;
	}

	@Override
	protected void loop() {
		PennyMessage msg = producer.getMessage();
		if (msg == null) {
			System.out.println("NetSender received a blank message...");
			return; // Somehow a blank message got in here; oh well.
		}

		try {
			Serialiser.encode(msg, outStream);
		} catch (IOException e) {
			System.out.println("ERROR: Could not send message out!");
			e.printStackTrace();
			producer.onConnectionLost(); // Try and reconnect
		}
	}
}
