package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;

/**
 * Class for managing the instance of wpa_supplicant
 * @author william
 *
 */
public class Wpa {
	private String wpa_supplicant;
	private final App app;
	
	public Wpa(App app) throws MissingFeatureException {
		this.app = app;
		try {
			wpa_supplicant = app.getFileFinder().getWpaSupplicantPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("wpa_supplicant))")) {
				MissingFeatureException exc =
						new MissingFeatureException(
								app.getString(R.string.no_wpa, app.getAppName()));
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
		if(networkCount == 0) throw new MissingFeatureException("Please add at least 1 network to connect to");
	}
}
