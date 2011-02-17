package pennygame.lib.queues;

/**
 * An abstract class, conceptualising the 'consumer' part of a message queue.
 * @author william
 *
 * @param <T>
 */
public abstract class MessageConsumer<T extends MessageProducer> extends LoopingThread {
	protected final T producer;

	public MessageConsumer(T producer, String threadID) {
		super(threadID);
		this.producer = producer;
	}
}
