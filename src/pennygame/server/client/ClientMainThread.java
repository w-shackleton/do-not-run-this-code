package pennygame.server.client;

import pennygame.lib.PennyMessage;
import pennygame.lib.queues.MainThread;

public class ClientMainThread extends MainThread {
	// TODO: Implement keepalive packet of '*,'
	int id = 0;
	@Override
	protected void loop() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		PennyMessage msg = new PennyMessage();
		msg.id = id++;
		putMessage(msg);
	}
}
