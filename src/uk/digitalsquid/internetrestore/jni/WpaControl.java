package uk.digitalsquid.internetrestore.jni;

import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;

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
	
	public synchronized void close() {
		if(isConnectionOpen()) {
			closeCtrl(wpa_ctrl);
			wpa_ctrl = 0;
		}
	}
}
