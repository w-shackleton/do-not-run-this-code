package pennygame.lib.queues;

public abstract class LoopingThread extends Thread {
	/**
	 * This defines how long this thread should pause in between loops, to stop
	 * too much CPU being used.
	 */
	protected int pauseTime = 20;
	protected boolean stopping = false;

	public LoopingThread() {
		super();
	}

	public void run() {
		while (!stopping) {
			loop();
			try {
				Thread.sleep(pauseTime);
			} catch (InterruptedException e) {
				System.out
						.println("Info: Thread was interrupted while sleeping, but it's OK...");
				e.printStackTrace();
			}
		}
	}

	public synchronized void beginStopping() {
		stopping = true;
	}
	
	/**
	 * This function is called once to allow the connection to set itself up (process logins etc)
	 */
	protected void setup() {
	}

	/**
	 * This function loops indefinitely when this loop is running.
	 */
	protected abstract void loop();
}
