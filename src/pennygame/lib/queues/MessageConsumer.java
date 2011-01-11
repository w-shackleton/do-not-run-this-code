package pennygame.lib.queues;

public abstract class MessageConsumer<T extends MessageProducer> extends LoopingThread {
	protected final T producer;

	public MessageConsumer(T producer, String threadID) {
		super(threadID);
		this.producer = producer;
	}
}
