package uk.digitalsquid.internetrestore.jni;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import uk.digitalsquid.internetrestore.AsyncTaskHelper;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.NumberParser;
import android.os.AsyncTask;
import android.util.SparseArray;

/**
 * wpa_supplicant control class
 * @author william
 *
 */
public class WpaControl {
	
	public WpaControl(String ctrlPath, String localPath) throws MissingFeatureException {
		wpa_ctrl = openCtrl(ctrlPath, localPath);
		if(wpa_ctrl == 0)
			throw new MissingFeatureException("Failed to connect to wpa_supplicant control interface", R.string.wpa_no_ctrl);
		wpa_ctrl_msg = openCtrl(ctrlPath, localPath);
	}
	
	static {
		System.loadLibrary("wpa");
	}
	
	/**
	 * Pointer to wpa_ctrl* object
	 */
	private int wpa_ctrl;
	/**
	 * wpa_ctrl for sending messages on.
	 */
	private int wpa_ctrl_msg;
	
	public boolean isConnectionOpen() {
		return wpa_ctrl != 0;
	}
	
	// Native stuff
	
	/**
	 *  Event messages with fixed prefix
	 */
	public static enum WpaMessageCode {
		/** Authentication completed successfully and data connection enabled */
		WPA_EVENT_CONNECTED("CTRL-EVENT-CONNECTED "),
		/** Disconnected, data connection is not available */
		WPA_EVENT_DISCONNECTED("CTRL-EVENT-DISCONNECTED "),
		/** wpa_supplicant is exiting */
		WPA_EVENT_TERMINATING("CTRL-EVENT-TERMINATING "),
		/** Password change was completed successfully */
		WPA_EVENT_PASSWORD_CHANGED("CTRL-EVENT-PASSWORD-CHANGED "),
		/** EAP-Request/Notification received */
		WPA_EVENT_EAP_NOTIFICATION("CTRL-EVENT-EAP-NOTIFICATION "),
		/** EAP authentication started (EAP-Request/Identity received) */
		WPA_EVENT_EAP_STARTED("CTRL-EVENT-EAP-STARTED "),
		/** EAP method selected */
		WPA_EVENT_EAP_METHOD("CTRL-EVENT-EAP-METHOD "),
		/** EAP authentication completed successfully */
		WPA_EVENT_EAP_SUCCESS("CTRL-EVENT-EAP-SUCCESS "),
		/** EAP authentication failed (EAP-Failure received) */
		WPA_EVENT_EAP_FAILURE("CTRL-EVENT-EAP-FAILURE "),
		/** Scan results are ready */
		WPA_EVENT_SCAN_RESULTS("CTRL-EVENT-SCAN-RESULTS "),
		/** wpa_supplicant state change */
		WPA_EVENT_STATE_CHANGE("CTRL-EVENT-STATE-CHANGE "),
		/** AP to STA speed */
		WPA_EVENT_LINK_SPEED("CTRL-EVENT-LINK-SPEED "),
		/** Driver state change */
		WPA_EVENT_DRIVER_STATE("CTRL-EVENT-DRIVER-STATE ");
		
		private final String code;
		
		public String getCode() {
			return code;
		}
		
		private WpaMessageCode(String code) {
			this.code = code;
		}
	}

	private static native int openCtrl(String ctrlPath, String localPath);
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
	
	private static native int request(int wpa_ctrl, byte[] msg, byte[] result);
	private static int request(int wpa_ctrl, String msg, byte[] result) {
		return request(wpa_ctrl, msg.getBytes(), result);
	}
	
	public synchronized void close() {
		if(isConnectionOpen()) {
			closeCtrl(wpa_ctrl);
			wpa_ctrl = 0;
			closeCtrl(wpa_ctrl_msg);
			wpa_ctrl_msg = 0;
		}
	}
	
	public static final class WpaMessage {
		private final int level;
		
		private final WpaMessageCode code;
		private final String message;
		
		WpaMessage(int level, WpaMessageCode code, String message) {
			this.level = level;
			this.code = code;
			this.message = message;
		}

		public int getLevel() {
			return level;
		}

		public WpaMessageCode getCode() {
			return code;
		}

		public String getMessage() {
			return message;
		}
	}
	
	private boolean taskRunning = false;
	private AsyncTask<Void, WpaMessage, Void> task = new AsyncTask<Void, WpaMessage, Void>() {

		@Override
		protected Void doInBackground(Void... params) {
			attach(wpa_ctrl);
			byte[] buf = new byte[2048];
			request(wpa_ctrl, "LEVEL 2", buf);
			while(!isCancelled()) {
				int len = recv(wpa_ctrl, buf);
				if(len < 0) break;
				String msg = new String(buf, 0, len);
				Logg.v(String.format("wpa_ctrl message: %s", msg));
				WpaMessage parsedMessage = parseStatusMessage(msg);
				if(parsedMessage == null) continue;
				publishProgress(parsedMessage);
			}
			detach(wpa_ctrl);
			return null;
		}
		
		protected WpaMessage parseStatusMessage(String msg) {
			Pattern pattern = Pattern.compile("<(\\d)>([^ ]* )(.*)");
			Matcher matcher = pattern.matcher(msg);
			if(matcher.matches()) {
				int level = NumberParser.parseIntSafe(matcher.group(1));
				String code = matcher.group(2);
				WpaMessageCode parsedCode = null;
				String message = matcher.group(3);
				for(WpaMessageCode testCode : WpaMessageCode.values()) {
					if(testCode.getCode().equals(code))
						parsedCode = testCode;
				}
				if(parsedCode == null) {
					Logg.i(String.format("Message received with unknown code %s", code));
					return null;
				}
				return new WpaMessage(level, parsedCode, message);
			}
			Logg.i(String.format("Invalid message received: %s", msg));
			return null;
		}
		
		@Override
		protected void onProgressUpdate(WpaMessage... values) {
			super.onProgressUpdate(values);
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
		if(!taskRunning) return;
		task.cancel(false);
	}
	
	private SparseArray<String> networkIDs;
	
	/**
	 * Returns an ID -> SSID map of configured networks
	 */
	private synchronized void reloadConfiguredNetworks() {
		if(networkIDs != null) return;
		byte[] result = new byte[0x1000];
		int len = request(wpa_ctrl_msg, "LIST_NETWORKS", result);
		if(len <= 0) return; // No info available yet.
		StringTokenizer lines = new StringTokenizer(new String(result, 0, len), "\n");
		networkIDs = new SparseArray<String>();
		while(lines.hasMoreTokens()) {
			String line = lines.nextToken();
			if(line.contains("network id")) continue; // Header
			StringTokenizer elements = new StringTokenizer(line, "\t");
			if(elements.countTokens() != 4) {
				Logg.d(String.format("Line with incorrect token count found: %s", line));
				continue;
			}
			
			int id = NumberParser.parseIntSafe(elements.nextToken());
			String ssid = elements.nextToken();
			networkIDs.append(id, ssid);
		}
	}
	
	public String getNetworkName(int id) {
		reloadConfiguredNetworks();
		if(networkIDs == null) return String.format("Network %d", id);
		return networkIDs.get(id, String.format("Network %d", id));
	}
}
