package uk.digitalsquid.internetrestore;

import android.util.Log;

/**
 * Logger which also logs to file, as well as standard android log.
 * @author william
 *
 */
public class Logg implements LogConf {
	
	/**
	 * Anything less than this level won't be logged
	 */
	private static int LEVEL = 0;
	
	public static void v(String msg) {
		if(Log.VERBOSE >= LEVEL)
			Log.v(TAG, msg);
	}
	public static void v(String msg, Throwable t) {
		if(Log.VERBOSE >= LEVEL)
			Log.v(TAG, msg, t);
	}
	public static void d(String msg) {
		if(Log.DEBUG >= LEVEL)
			Log.d(TAG, msg);
	}
	public static void d(String msg, Throwable t) {
		if(Log.DEBUG >= LEVEL)
			Log.d(TAG, msg, t);
	}
	public static void i(String msg) {
		if(Log.INFO >= LEVEL)
			Log.i(TAG, msg);
	}
	public static void i(String msg, Throwable t) {
		if(Log.INFO >= LEVEL)
			Log.i(TAG, msg, t);
	}
	public static void w(String msg) {
		if(Log.WARN >= LEVEL)
			Log.w(TAG, msg);
	}
	public static void w(String msg, Throwable t) {
		if(Log.WARN >= LEVEL)
			Log.w(TAG, msg, t);
	}
	public static void e(String msg) {
		if(Log.ERROR >= LEVEL)
			Log.e(TAG, msg);
	}
	public static void e(String msg, Throwable t) {
		if(Log.ERROR >= LEVEL)
			Log.e(TAG, msg, t);
	}
}
