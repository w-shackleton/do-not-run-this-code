package uk.digitalsquid.contactrecall.misc;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.os.AsyncTask;

/**
 * Loads elements asynchronously from a source, and allows the results to be taken from the top.
 * @author william
 *
 */
public class AsyncLoadBuffer<T> {
	
	private final Source<? extends T> src;
	
	private static final int QUEUE_LENGTH = 3;
	
	private static final int STAGE_READY = 1;
	private static final int STAGE_RUNNING = 2;
	private static final int STAGE_DONE = 3;
	private int stage = STAGE_READY;
	
	private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<T>();
	
	/**
	 * Gets the next element, waiting if necessary.
	 * @return
	 */
	public synchronized T get() {
		if(stage == STAGE_DONE || stage == STAGE_READY) return queue.poll();
		// else
		while(queue.isEmpty()) {
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) { }
		}
		return queue.poll();
	}
	
	/**
	 * Starts the processing stage. Only run this once.
	 */
	public synchronized void start() {
		if(stage == STAGE_READY) {
			thread.execute();
			stage = STAGE_RUNNING;
		}
	}
	
	public void stop() {
		stage = STAGE_DONE;
		thread.cancel(false);
	}
	
	public AsyncLoadBuffer(Source<? extends T> src) {
		this.src = src;
	}

	public static interface Source<T> {
		/**
		 * Gets the nth element to be parsed. Will be called in sequential order.
		 */
		T getElement(int pos);
		
		boolean hasMore();
		
		void finish();
	}
	
	private final AsyncTask<Void, T, Void> thread = new AsyncTask<Void, T, Void>() {

		@Override
		protected Void doInBackground(Void... params) {
			int count = 0;
			while(src.hasMore()) {
				if(isCancelled()) return null;
				while(queue.size() >= QUEUE_LENGTH) { // Wait for queue to shrink
					if(isCancelled()) return null;
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) { }
				}
				queue.offer(src.getElement(count++));
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(T... vals) {
			queue.add(vals[0]);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			src.finish();
			stage = STAGE_DONE;
		}
	};
}
