package uk.digitalsquid.internetrestore.manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.StreamGobbler;
import uk.digitalsquid.internetrestore.util.StreamGobbler.Callback;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

/**
 * Manages the runner program, which runs everything else.
 * @author william
 *
 */
public class Runner {
	private String runner, su, xtables;
	private final App app;
	
	private Process instance;
	
	public Runner(App app) throws MissingFeatureException {
		this.app = app;
		try {
			runner = app.getFileInstaller().getScriptPath(FileInstaller.BIN_RUNNER);
			su = app.getFileFinder().getSuPath();
			xtables = app.getFileInstaller().getScriptPath(FileInstaller.BIN_XTABLES);
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("runner))")) {
				
			} else if(e.getMessage().equalsIgnoreCase("su))")) {
				MissingFeatureException exc =
						new MissingFeatureException("su is missing",
								R.string.no_su);
				exc.initCause(e);
				throw exc;
			}
		}
	}
	
	/**
	 * Starts the runner application, which runs everything else.
	 * @throws IOException 
	 */
	public synchronized void start() throws IOException {
		if(instance != null) return;
		// Note that this method of running processes only works on SDK >= 9.
		// This is fine for this app as we are using SDK 10.
		final String cmdLine = runner;
		Logg.d("Running command line \"" + cmdLine + "\"");
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				cmdLine);
		
		instance = pb.start();
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(instance.getInputStream()));
		boolean found = false;
		for(int i = 0; i < 5; i++) {
			if(reader.readLine().equals("STARTED")) {
				found = true;
				break;
			}
		}
		if(!found) throw new IOException("Runner failed to start, possibly SU wasn't accepted");
		
		new StreamGobbler(instance.getInputStream(), "runner cout");
		new StreamGobbler(instance.getErrorStream(), "runner cerr");
		stdin = new OutputStreamWriter(instance.getOutputStream());
		
		// Add environment
		sendCommand(String.format("env BB=\"%s\";", app.getFileFinder().getBusyboxPath()));
		sendCommand(String.format("env XTABLES=\"%s\";", xtables));
	}
	
	/**
	 * Stops Dhcpcd. If it hasn't detached, this command kills the process.
	 * Otherwise, it looks for the PID file and kills it that way.
	 */
	public synchronized void stop() {
		if(instance == null) return;
		try {
			stdin.close();
		} catch (IOException e) { }
		instance.destroy();
		instance = null;
		stdin = null;
	}
	
	private OutputStreamWriter stdin;
	
	public synchronized void sendCommand(String command) throws IOException {
		if(instance == null) start();
		stdin.write(command);
		stdin.flush();
	}
	
	private String statusLine = null;
	
	private final Callback callback = new Callback() {
		@Override
		public void onOutput(String line) {
			statusLine = line;
			synchronized(callback) {
				callback.notify();
			}
		}
	};
	
	public synchronized boolean isRunning(String task) {
		try {
			sendCommand(String.format("running %s", task));
		} catch (IOException e) {
			Logg.e("Failed to sendCommand in isRunning", e);
		}
		
		try {
			synchronized(callback) {
				callback.wait(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if(statusLine != null) {
			if(statusLine.equals("RUNNING")) {
				statusLine = null;
				return true;
			} else {
				statusLine = null;
				return false;
			}
		}
		return false;
	}
}
