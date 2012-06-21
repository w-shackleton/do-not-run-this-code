package uk.digitalsquid.internetrestore.jni;

/**
 * wpa_supplicant control class
 * @author william
 *
 */
public class WpaControl {
	
	public WpaControl(String ctrlPath) {
		wpa_ctrl = openCtrl(ctrlPath);
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
