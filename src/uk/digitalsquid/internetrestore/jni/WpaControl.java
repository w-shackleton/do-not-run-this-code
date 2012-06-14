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
	private long wpa_ctrl;
	
	public boolean isConnectionOpen() {
		return wpa_ctrl != 0;
	}

	private static native long openCtrl(String ctrlPath);
	private static native void closeCtrl(long wpa_ctrl);
	
	public void close() {
		if(isConnectionOpen())
			closeCtrl(wpa_ctrl);
	}
}
