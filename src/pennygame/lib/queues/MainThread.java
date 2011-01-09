package pennygame.lib.queues;

public abstract class MainThread extends MessageProducer {
	protected void onConnectionLost() {
		// TODO: Implement some code to reconnect here - exit but mark a boolean
		// that we have lost connection so that it gets dealt with in the loop.
	}
}
