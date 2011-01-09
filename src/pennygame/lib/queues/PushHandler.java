package pennygame.lib.queues;

import pennygame.lib.PennyMessage;

public abstract class PushHandler extends MessageConsumer<NetReceiver> {

	public PushHandler(NetReceiver producer) {
		super(producer);
	}

	@Override
	protected void loop() {
		processMessage(producer.getMessage());
	}
	
	protected abstract void processMessage(PennyMessage msg);
}
