package uk.digitalsquid.internetrestore.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.digitalsquid.internetrestore.Logg;


/**
 * Reads from a stream and optionally spews it out to the log.
 * @author william
 *
 */
public class StreamGobbler extends Thread {
	
	BufferedReader reader;
	
	String logPrefix;
	
	public static interface Callback {
		public void onOutput(String line);
	}
	
	/**
	 * 
	 * @param is
	 * @param logPrefix If <code>null</code>, no output will be written.
	 * Otherwise, this will be prefixed to each log entry.
	 */
	public StreamGobbler(InputStream is, String logPrefix) {
		super("StreamGobbler - " + logPrefix);
		reader = new BufferedReader(new InputStreamReader(is));
		this.logPrefix = logPrefix;
		setDaemon(true);
		start();
	}
	
	public StreamGobbler(BufferedReader is, String logPrefix) {
		super("StreamGobbler - " + logPrefix);
		reader = is;
		this.logPrefix = logPrefix;
		setDaemon(true);
		start();
	}
	
	private Callback callback;
	
	public StreamGobbler(InputStream is, String logPrefix, Callback callback) {
		this(is, logPrefix);
		this.callback = callback;
	}
	public StreamGobbler(BufferedReader is, String logPrefix, Callback callback) {
		this(is, logPrefix);
		this.callback = callback;
	}
	
	@Override
	public void run() {
		String line;
		try {
			while((line = reader.readLine()) != null) {
				if(logPrefix != null) {
					Logg.d(String.format("%s: %s", logPrefix, line));
					if(callback != null) callback.onOutput(line);
				}
			}
		} catch (IOException e) {
			Logg.e("StreamGobbler failed to read from input stream", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
