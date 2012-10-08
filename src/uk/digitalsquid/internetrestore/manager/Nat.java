package uk.digitalsquid.internetrestore.manager;

import java.io.IOException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

public final class Nat {
	
	final App app;
	final Runner runner;
	
	public Nat(App app, Runner runner) {
		this.app = app;
		this.runner = runner;
	}
	
	/**
	 * Runs the script to set up the iptables config
	 * @throws MissingFeatureException 
	 */
	public synchronized void start() throws IOException {
		if(runner.isRunning("wpa")) return;
		
		final String natter = app.getFileInstaller().getScriptPath(FileInstaller.BIN_NATTER);
		
		final String natCmdLine = String.format("%s", natter);
		Logg.d("Running command line \"" + natCmdLine + "\"");
		
		runner.sendCommand("create natter;");
		runner.sendCommand("set args natter natCmdLine;");
	}
	
	public synchronized void stop() {
		try {
			runner.sendCommand("send natter \"\";");
			runner.sendCommand("sleep 2;");
			runner.sendCommand("stop natter;");
		} catch (IOException e) {
			Logg.e("Failed to stop natter", e);
		}
	}
	
	/**
	 * Sets the subnet to be put behind the NAT firewall.
	 * @param subnet A subnet in the form a.b.c.d/s, ie. 10.1.2.0/24
	 * @throws IOException 
	 */
	public synchronized void setMasqueradedSubnet(String subnet) throws IOException {
		Logg.i(String.format("Setting new masqueraded subnet to %s", subnet));
		runner.sendCommand(String.format("send natter %s", subnet));
	}
}
