package uk.digitalsquid.contactrecall.misc;

import java.util.concurrent.ConcurrentHashMap;

import android.os.AsyncTask;

/**
 * Loads elements asynchronously from a source, and allows the results to be taken from the top.
 * @author william
 *
 */
public class AsyncLoadBuffer<T> {
	
	private final Source<? extends T> src;
	
	private static final int QUEUE_LENGTH = 2;
	
	private static final int STAGE_READY = 1;
	private static final int STAGE_RUNNING = 2;
	private static final int STAGE_DONE = 3;
	private int stage = STAGE_READY;
	
	private final ConcurrentHashMap<Integer, T> queue = new ConcurrentHashMap<Integer, T>();
	
	/**
	 * Gets the next element, waiting if necessary.
	 * @return
	 */
	public synchronized T get(int position) {
		if(stage == STAGE_DONE || stage == STAGE_READY) return queue.get(position);
		// else
		while(queue.get(position) == null) {
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) { }
		}
		T result = queue.get(position);
		// Clear all entries below this one, position wise.
		for(int elem : queue.keySet()) {
			if(elem < position) queue.remove(elem);
		}
		return result;
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
		
		/**
		 * If getElement returns null, this value is used.
		 * @return
		 */
		T ifNull();
		
		boolean hasMore(int pos);
		
		void finish();
	}
	
	public void windTo(int position) {
		windToPosition = position;
		queue.clear();
	}
	
	/**
	 * -1 indicates nothing to do.
	 */
	private int windToPosition = -1;
	
	private final AsyncTask<Void, T, Void> thread = new AsyncTask<Void, T, Void>() {

		@Override
		protected Void doInBackground(Void... params) {
			int count = 0;
			while(src.hasMore(count)) {
				if(isCancelled()) return null;
				while(queue.size() >= QUEUE_LENGTH) { // Wait for queue to shrink
					if(isCancelled()) return null;
					try {
						Thread.sleep(600);
					} catch (InterruptedException e) { }
				}
				if(windToPosition != -1) {
					queue.clear();
					count = windToPosition;
					windToPosition = -1;
				}
				T elem = src.getElement(count);
				if(elem != null) queue.put(count, elem);
				else queue.put(count, elem);
				count++;
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(T... vals) {
		}
		
		@Override
		protected void onPostExecute(Void result) {
			src.finish();
			stage = STAGE_DONE;
		}
	};
}
