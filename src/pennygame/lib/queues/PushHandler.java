package pennygame.lib.queues;

import pennygame.lib.PennyMessage;

public abstract class PushHandler extends MessageConsumer<NetReceiver> {

	public PushHandler(NetReceiver producer) {
		super(producer);
	}

	@Override
	protected void loop() {
		PennyMessage m = producer.getMessage();
		if(m != null)
			processMessage(m);
	}
	
	protected abstract void processMessage(PennyMessage msg);
}
