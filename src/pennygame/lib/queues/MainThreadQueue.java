package pennygame.lib.queues;

import java.io.BufferedWriter;

/**
 * This class pairs a MainThread with a NetSender to send its messages. The NetSender is automatically constructed.
 * @author william
 *
 * @param <P>
 */
public class MainThreadQueue<P extends MainThread> extends MessageQueue<P, NetSender<P>> {
	public MainThreadQueue(P producer, BufferedWriter outStream) {
		super(producer, new NetSender<P>(producer, outStream));
	}
}
