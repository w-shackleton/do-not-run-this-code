package pennygame.lib.queues;

public abstract class MessageConsumer<P extends MessageProducer> extends LoopingThread {
	protected final P producer;

	public MessageConsumer(P producer) {
		this.producer = producer;
	}
}
