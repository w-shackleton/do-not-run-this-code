package pennygame.server.client;

import pennygame.lib.queues.MainThread;

public class CConnMainThread extends MainThread {
	// TODO: Implement keepalive packet of '*,'
	int id = 0;
	@Override
	protected void loop() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void setup() {
		// TODO: Add login processing (Wait until request received from PushHandler)
	}
}
