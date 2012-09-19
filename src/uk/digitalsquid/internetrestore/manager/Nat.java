package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.StreamGobbler;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

public final class Nat {
	
	final App app;
	private String xtables, su, bb;
	
	public Nat(App app) throws MissingFeatureException {
		this.app = app;
		xtables = app.getFileInstaller().getScriptPath(FileInstaller.BIN_XTABLES);
		try {
			su = app.getFileFinder().getSuPath();
			bb = app.getFileFinder().getBusyboxPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("su")) {
				MissingFeatureException exc =
						new MissingFeatureException("su is missing",
								R.string.no_su);
				exc.initCause(e);
				throw exc;
			} else if(e.getMessage().equalsIgnoreCase("busybox")) {
				MissingFeatureException exc =
						new MissingFeatureException("busybox is missing",
								R.string.no_busybox);
				exc.initCause(e);
				throw exc;
			}
		}
	}
	
	private Process instance;
	
	private OutputStreamWriter stdin;
	
	/**
	 * Runs the script to set up the iptables config
	 * @throws MissingFeatureException 
	 */
	public synchronized void start() throws IOException {
		if(instance != null) return;
		
		final String natter = app.getFileInstaller().getScriptPath(FileInstaller.BIN_NATTER);
		
		final String natCmdLine = String.format("%s", natter);
		Logg.d("Running command line \"" + natCmdLine + "\"");
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				natCmdLine);
		
		pb.environment().put("BB", bb);
		pb.environment().put("XTABLES", xtables);
		
		instance = pb.start();
		
		stdin = new OutputStreamWriter(instance.getOutputStream());
		new StreamGobbler(instance.getInputStream(), "nat cout");
		new StreamGobbler(instance.getErrorStream(), "nat cerr");
	}
	
	public synchronized void stop() {
		try {
			stdin.write("\n");
			stdin.flush();
		} catch (IOException e1) {
			Logg.w("Couldn't stop natter", e1);
		}
		
		try { Thread.sleep(1500); } catch (InterruptedException e) { }
		
		instance.destroy();
		instance = null;
	}
	
	/**
	 * Sets the subnet to be put behind the NAT firewall.
	 * @param subnet A subnet in the form a.b.c.d/s, ie. 10.1.2.0/24
	 * @throws IOException 
	 */
	public synchronized void setMasqueradedSubnet(String subnet) throws IOException {
		if(instance == null) {
			Logg.w("Attempt was made to set new subnet while natter isn't running");
			return;
		}
		Logg.i(String.format("Setting new masqueraded subnet to %s", subnet));
		stdin.write(String.format("%s\n", subnet));
		stdin.flush();
	}
}
