package pennygame.lib.queues;

/**
 * An abstract(empty) class of a main thread, which, further up the heirarchy, emits messages to be sent across the network, to whichever destination is implemented.
 * @author william
 *
 */
public abstract class MainThread extends MessageProducer {

	public MainThread(String threadID) {
		super(threadID);
	}
}
