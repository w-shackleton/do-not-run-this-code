package pennygame.lib.queues;

import pennygame.lib.msg.PennyMessage;

/**
 * A {@link MessageConsumer} which pulls messages from the queue and calls {@link #processMessage(PennyMessage)} with each new message, hence processing it.
 * This class should be overridden to receive messages.
 * @author william
 *
 */
public abstract class PushHandler extends MessageConsumer<NetReceiver> {

	public PushHandler(NetReceiver producer, String threadID) {
		super(producer, threadID);
	}

	@Override
	protected void loop() {
		try
		{
			PennyMessage m = (PennyMessage) producer.getMessageNow(); // This SHOULD always return a PennyMessage
			if(m != null)
				processMessage(m);
		} catch (Exception e) {
			// System.out.println("PushHandler received non-PennyMessage message.");
		} // No need to worry if not
	}
	
	protected abstract void processMessage(PennyMessage msg);
}
