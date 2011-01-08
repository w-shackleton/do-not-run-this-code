package pennygame.lib.queues;

import java.util.Vector;

import pennygame.lib.PennyMessage;

public abstract class MessageProducer extends LoopingThread {
	private static final int MAXQUEUE = 20; // Should never get this high anyway
	private final Vector<PennyMessage> messages = new Vector<PennyMessage>();

	public MessageProducer() {

	}

	protected final synchronized void putMessage(PennyMessage msg) {
		while (messages.size() == MAXQUEUE)
			try {
				wait();
			} catch (InterruptedException e) {
				System.out
						.println("MessageProducer.putMessage's wait was interrupted, should be nothing...");
				e.printStackTrace();
			}
		messages.addElement(msg);
		notify();
	}

	protected final synchronized PennyMessage getMessage() {
		notify();
		while (messages.size() == 0)
			try {
				wait();
			} catch (InterruptedException e) {
				System.out
						.println("MessageProducer.putMessage's wait was interrupted, should be nothing...");
				e.printStackTrace();
			}
		PennyMessage message = messages.firstElement();
		messages.removeElement(message);
		notify();
		return message;
	}
}
