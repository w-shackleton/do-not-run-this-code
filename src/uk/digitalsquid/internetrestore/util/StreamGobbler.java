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
	
	/**
	 * 
	 * @param is
	 * @param logPrefix If <code>null</code>, no output will be written.
	 * Otherwise, this will be prefixed to each log entry.
	 */
	public StreamGobbler(InputStream is, String logPrefix) {
		super("StreamGobbler");
		reader = new BufferedReader(new InputStreamReader(is));
		this.logPrefix = logPrefix;
		setDaemon(true);
		start();
	}
	
	@Override
	public void run() {
		String line;
		try {
			while((line = reader.readLine()) != null) {
				if(logPrefix != null) {
					Logg.d(String.format("%s: %s", logPrefix, line));
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
