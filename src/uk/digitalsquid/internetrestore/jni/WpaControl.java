package uk.digitalsquid.internetrestore.jni;

import uk.digitalsquid.internetrestore.AsyncTaskHelper;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import android.os.AsyncTask;

/**
 * wpa_supplicant control class
 * @author william
 *
 */
public class WpaControl {
	
	public WpaControl(String ctrlPath) throws MissingFeatureException {
		wpa_ctrl = openCtrl(ctrlPath);
		if(wpa_ctrl == 0)
			throw new MissingFeatureException("Failed to connect to wpa_supplicant control interface", R.string.no_wifi_iface);
	}
	
	static {
		System.loadLibrary("wpa");
	}
	
	/**
	 * Pointer to wpa_ctrl* object
	 */
	private int wpa_ctrl;
	
	public boolean isConnectionOpen() {
		return wpa_ctrl != 0;
	}

	private static native int openCtrl(String ctrlPath);
	private static native void closeCtrl(int wpa_ctrl);
	private static native boolean attach(int wpa_ctrl);
	private static native boolean detach(int wpa_ctrl);
	/**
	 * 
	 * @param wpa_ctrl
	 * @param buf
	 * @return -1 on failure, otherwise the number of bytes written.
	 */
	private static native int recv(int wpa_ctrl, byte[] buf);
	
	public synchronized void close() {
		if(isConnectionOpen()) {
			closeCtrl(wpa_ctrl);
			wpa_ctrl = 0;
		}
	}
	
	private boolean taskRunning = false;
	private AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

		@Override
		protected Void doInBackground(Void... params) {
			attach(wpa_ctrl);
			while(!isCancelled()) {
				
			}
			detach(wpa_ctrl);
			return null;
		}
		
		protected void onPostExecute(Void result) {
			taskRunning = false;
		}
	};
	
	/**
	 * Starts listening for messages
	 */
	public synchronized void start() {
		if(taskRunning) return;
		taskRunning = true;
		AsyncTaskHelper.execute(task);
	}
	
	public synchronized void stop() {
		task.cancel(false);
	}
}
