package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.StreamGobbler;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

public final class Nat {
	
	final App app;
	private String xtables, su, bb;
	private String iface;
	
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
		try {
			iface = app.getInfoCollector().getWifiIface();
		} catch (UnknownHostException e) {
			MissingFeatureException exc = new MissingFeatureException("Couldn't getWifiIface()", R.string.no_wifi_iface);
			exc.initCause(e);
			throw exc;
		}
	}
	
	private Process instance;
	
	/**
	 * Runs the script to set up the iptables config
	 * @throws MissingFeatureException 
	 */
	public synchronized void start() throws IOException {
		if(instance != null) return;
		
		// We have to use this helper script to call everything to cd after becoming root.
		
		final String natCmdLine = String.format("");
		Logg.d("Running command line \"" + natCmdLine + "\"");
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				natCmdLine);
		
		pb.environment().put("BB", app.getFileFinder().getBusyboxPath());
		pb.environment().put("XTABLES", app.getFileFinder().getBusyboxPath());
		
		instance = pb.start();
		
		new StreamGobbler(instance.getInputStream(), "nat cout");
		new StreamGobbler(instance.getErrorStream(), "nat cerr");
	}
	
}
