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
	private String wpa_supplicant, su;
	private final App app;
	
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
	}
	
	/**
	 * Starts the WPA daemon using the saved config
	 * @throws MissingFeatureException 
	 */
	public synchronized void start() throws MissingFeatureException {
		// First check that wpa_supplicant.conf contains at least one network.
		int networkCount = app.getWpaSettings().readLocalConfig().getNetworkCount();
		if(networkCount == 0) throw new MissingFeatureException("No networks are available in config", R.string.no_networks);
		
		// Note that this method of running processes only works on SDK >= 9.
		// This is fine for this app as we are using SDK 10.
		final String wpa_supplicant_conf = app.getFileInstaller().getScriptPath(FileInstaller.CONF_WPA_SUPPLICANT);
		final String entropy_bin = app.getFileInstaller().getScriptPath(FileInstaller.CONF_ENTROPY_BIN);
		final String iface;
		try {
			iface = app.getInfoCollector().getWifiIface();
		} catch (UnknownHostException e) {
			MissingFeatureException exc = new MissingFeatureException("Couldn't getWifiIface()", R.string.no_wifi_iface);
			exc.initCause(e);
			throw exc;
		}
		
		final String wpaCmdLine = String.format("%s -i %s -c %s -e %s",
				wpa_supplicant,
				iface,
				wpa_supplicant_conf,
				entropy_bin
				);
		Logg.d("Running command line \"" + wpaCmdLine + "\"");
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				wpaCmdLine);
		
		File cwd;
		try {
			cwd = app.getWpaSettings().getWpaDir();
		} catch (FileNotFoundException e1) {
			cwd = null;
		}
		pb.directory(cwd);
		
		try {
			instance = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops wpa_supplicant, if it is running.
	 * @return The exit code of wpa_supplicant
	 */
	public synchronized int stop() {
		if(instance == null)
			return -1;
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
