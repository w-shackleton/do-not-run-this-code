package pennygame.lib.queues;

/**
 * This class pairs a PushHandler and a NetReceiver in a queue.
 * @author william
 *
 * @param <C>
 */
public class PushHandlerQueue<C extends PushHandler> extends MessageQueue<NetReceiver, C> {
	public PushHandlerQueue(NetReceiver producer, C consumer) {
		super(producer, consumer);
	}
}
