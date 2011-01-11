package pennygame.lib.queues;

import java.util.LinkedList;
import java.util.Queue;

import pennygame.lib.msg.PennyMessage;

public abstract class MessageProducer extends LoopingThread {
	private static final int MAXQUEUE = 20; // Should never get this high anyway
	
	/**
	 * The message queue. Object is used here, as this can also contain strings
	 */
	private Queue<Object> messages;

	public MessageProducer(String threadID) {
		super(threadID);
		messages = new LinkedList<Object>();
	}

	protected synchronized final void putMessage(PennyMessage msg) {
		while (messages.size() == MAXQUEUE && !stopping)
			try {
				wait(500);
			} catch (InterruptedException e) {
				System.out
						.println("MessageProducer.putMessage's wait was interrupted, should be nothing...");
				e.printStackTrace();
			}
		
		messages.add(msg);
		notify();
	}

	/**
	 * Gets a message from this queue. This will block until a message appears
	 * @return EITHER a {@link PennyMessage} or a preserialised {@link String}
	 */
	protected synchronized final Object getMessage() {
		notify();
		while (messages.size() == 0 && !stopping)
			try {
				wait(500);
			} catch (InterruptedException e) {
				System.out
						.println("MessageProducer.getMessage's wait was interrupted, should be nothing...");
				e.printStackTrace();
			}
		Object message = messages.poll();
		if(PennyMessage.class.isAssignableFrom(message.getClass()) || String.class.isAssignableFrom(message.getClass()))
		{
			notify();
			return message;
		}
		return null;
	}
	
	protected synchronized final Object getMessageNow() {
		Object message = messages.poll();
		if(message == null) return null;
		if(PennyMessage.class.isAssignableFrom(message.getClass()) || String.class.isAssignableFrom(message.getClass()))
			return message;
		return null;
	}
}
