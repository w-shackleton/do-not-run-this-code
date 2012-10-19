package uk.digitalsquid.internetrestore.manager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.NoCloseInputStream;
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
		final String cmdLine = String.format("%s", runner);
		Logg.d("Running command line \"" + cmdLine + "\"");
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				cmdLine);
		
		instance = pb.start();
		
		stdin = instance.getOutputStream();
		new StreamGobbler(instance.getErrorStream(), "runner cerr");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(new NoCloseInputStream(instance.getInputStream())));
		boolean found = false;
		for(int i = 0; i < 5; i++) {
			if("STARTED".equals(reader.readLine())) {
				Logg.v("Found line indicating runner starting");
				found = true;
				break;
			}
		}
		reader.close();
		if(!found) throw new IOException("Runner failed to start, possibly SU wasn't accepted");
		
		new StreamGobbler(instance.getInputStream(), "runner cout");
		
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
			sendCommand("quit;");
			stdin.close();
		} catch (IOException e) { }
		instance.destroy();
		instance = null;
		stdin = null;
	}
	
	private OutputStream stdin;
	
	public synchronized void sendCommand(String command) throws IOException {
		if(instance == null) start();
		Logg.v(String.format("Runner: \"%s\"", command));
		// This newline prompts yacc to parse what has been entered
		command = String.format("%s\n", command);
		stdin.write(command.getBytes());
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
