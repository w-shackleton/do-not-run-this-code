package uk.digitalsquid.internetrestore.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.StreamGobbler;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

/**
 * Class for managing the instance of wpa_supplicant
 * @author william
 *
 */
public class Wpa {
	private String wpa_supplicant, su;
	private final App app;
	
	private final String iface;
	
	private Process instance;
	
	public Wpa(App app) throws MissingFeatureException {
		this.app = app;
		try {
			wpa_supplicant = app.getFileFinder().getWpaSupplicantPath();
			su = app.getFileFinder().getSuPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("wpa_supplicant))")) {
				MissingFeatureException exc =
						new MissingFeatureException("wpa_supplicant is missing",
								R.string.no_wpa, app.getAppName());
				exc.initCause(e);
				throw exc;
			} else if(e.getMessage().equalsIgnoreCase("su))")) {
				MissingFeatureException exc =
						new MissingFeatureException("su is missing",
								R.string.no_su);
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
		if(instance != null) return;
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
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				wpaCmdLine);
		
		pb.environment().put("BB", app.getFileFinder().getBusyboxPath());
		
		instance = pb.start();
		
		new StreamGobbler(instance.getInputStream(), "wpa cout");
		new StreamGobbler(instance.getErrorStream(), "wpa cerr");
	}
	
	/**
	 * Writes a blank line to the stdin of the process. Silently fails
	 */
	private void writeLine() {
		try {
			if(instance == null) return;
			OutputStream os = instance.getOutputStream();
			if(os == null) return;
			os.write("\n".getBytes());
		} catch (IOException e) {
			Logg.w("Failed to write line to wpa stdin");
		}
	}
	
	/**
	 * Stops wpa_supplicant, if it is running.
	 * @return The exit code of wpa_supplicant
	 */
	public synchronized int stop() {
		Logg.d("Stopping wpa_supplicant");
		if(instance == null)
			return -1;
		writeLine();
		try {
			Thread.sleep(1200);
		} catch (InterruptedException e1) { }
		instance.destroy();
		int ret = -1;
		try {
			ret = instance.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		instance = null;
		return ret;
	}
	
	public boolean isRunning() {
		return instance != null;
	}
}
