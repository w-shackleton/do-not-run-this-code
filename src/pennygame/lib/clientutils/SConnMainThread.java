package pennygame.lib.clientutils;

import pennygame.lib.queues.MainThread;

public class SConnMainThread extends MainThread {
	
	public SConnMainThread(String username, String pass) {
		super();
	}
	
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
