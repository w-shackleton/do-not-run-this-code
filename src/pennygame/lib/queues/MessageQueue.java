package pennygame.lib.queues;

/**
 * A base to hold a producer and consumer.<br />
 * Some common pairs:<br />
 * <br /> {@link MessageProducer} and {@link MessageConsumer} - the base classes.<br />
 * {@link MainThread} and {@link NetSender} - this is the 'working thread' of
 * either side, which has main control, and the sender to the net.<br />
 * {@link NetReceiver} and {@link PushHandler} - These receive messages, process
 * them and do something - a 'push' event.
 * 
 * @author william
 * 
 * @param <P>
 *            A producer
 * @param <C>
 *            A consumer
 */
public class MessageQueue<P extends MessageProducer, C extends MessageConsumer<P>> {

}
