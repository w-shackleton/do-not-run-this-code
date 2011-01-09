package pennygame.lib.queues;

import pennygame.lib.PennyMessage;

public abstract class PushHandler extends MessageConsumer<NetReceiver> {

	public PushHandler(NetReceiver producer) {
		super(producer);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void loop() {
		if (!producer.isConnected()) {
			// Do something to check if we have disconnected, and do something
			// about it!
		}
	}
	
	protected abstract void processMessage(PennyMessage msg);
}
