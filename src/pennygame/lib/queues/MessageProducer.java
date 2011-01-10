package pennygame.lib.queues;

import java.util.LinkedList;
import java.util.Queue;

import pennygame.lib.PennyMessage;

public abstract class MessageProducer extends LoopingThread {
	private static final int MAXQUEUE = 20; // Should never get this high anyway
	private Queue<PennyMessage> messages;

	public MessageProducer() {
		messages = new LinkedList<PennyMessage>();
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

	protected synchronized final PennyMessage getMessage() {
		notify();
		while (messages.size() == 0)
			try {
				wait(500);
			} catch (InterruptedException e) {
				System.out
						.println("MessageProducer.getMessage's wait was interrupted, should be nothing...");
				e.printStackTrace();
			}
		PennyMessage message = messages.poll();
		notify();
		return message;
	}
}
