package uk.digitalsquid.internetrestore.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

/**
 * Class for managing the instance of wpa_supplicant
 * @author william
 *
 */
public class Wpa {
	private String wpa_supplicant;
	private final App app;
	private final Runner runner;
	
	private final String iface;
	
	public Wpa(App app, Runner runner) throws MissingFeatureException {
		this.app = app;
		this.runner = runner;
		try {
			wpa_supplicant = app.getFileFinder().getWpaSupplicantPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("wpa_supplicant")) {
				MissingFeatureException exc =
						new MissingFeatureException("wpa_supplicant is missing",
								R.string.no_wpa, app.getAppName());
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
	
	public String getWifiIface() {
		return iface;
	}
	
	/**
	 * Starts the WPA daemon using the saved config
	 * @throws MissingFeatureException 
	 */
	public synchronized void start() throws IOException {
		if(runner.isRunning("wpa")) return;
		// First check that wpa_supplicant.conf contains at least one network.
		int networkCount = app.getWpaSettings().readLocalConfig().getNetworkCount();
		if(networkCount == 0) throw new MissingFeatureException("No networks are available in config", R.string.no_networks);
		
		// Note that this method of running processes only works on SDK >= 9.
		// This is fine for this app as we are using SDK 10.
		final String wpa_supplicant_conf = app.getFileInstaller().getConfFilePath(FileInstaller.CONF_WPA_SUPPLICANT).getAbsolutePath();
		final String entropy_bin = app.getFileInstaller().getConfFilePath(FileInstaller.CONF_ENTROPY_BIN).getAbsolutePath();
		final String run_wpa_supplicant = app.getFileInstaller().getScriptPath(FileInstaller.BIN_RUN_WPA_SUPPLICANT);
		// wpa_supplicant socket directory
		final String socket_dir = app.getFileInstaller().getSockPath(FileInstaller.SOCK_CTRL).getAbsolutePath();
		
		// We have to use this helper script to call everything to cd after becoming root.
		
		String cwd;
		try {
			cwd = new File(app.getWpaSettings().getWpaDir().getParentFile(), "wifi.inetrestore").getAbsolutePath();
		} catch (FileNotFoundException e1) {
			cwd = ".";
		}
		
		Logg.d("CWD is " + cwd);
		
		final String wpaCmdLine = String.format("%s %s %s %s %s %s %s",
				run_wpa_supplicant,
				cwd,
				wpa_supplicant,
				iface,
				wpa_supplicant_conf,
				entropy_bin,
				socket_dir
				);
		Logg.d("Running command line \"" + wpaCmdLine + "\"");
		
		runner.sendCommand("create wpa;");
		runner.sendCommand(String.format("set args wpa %s;", wpaCmdLine));
	}
	
	/**
	 * Stops wpa_supplicant, if it is running.
	 * @return The exit code of wpa_supplicant
	 * @throws IOException 
	 */
	public synchronized void stop() {
		Logg.d("Stopping wpa_supplicant");
		try {
			runner.sendCommand("send wpa \"stop\";");
			runner.sendCommand("sleep 1;");
			runner.sendCommand("stop wpa;");
		} catch (IOException e) {
			Logg.e("Failed to stop WPA", e);
		}
	}
	
	public boolean isRunning() {
		return runner.isRunning("wpa");
	}
}
