package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.file.FileFinder;

/**
 * Class for managing the instance of wpa_supplicant
 * @author william
 *
 */
public class Wpa {
	final String wpa_supplicant;
	final App app;
	
	public Wpa(App app) {
		this.app = app;
		try {
			FileFinder.initialise(app);
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("wpa_supplicant))")) {
				MissingFeatureException exc =
						new MissingFeatureException(
								app.getString(R.string.no_wpa, app.getAppName()));
				exc.initCause(e);
				throw exc;
			}
		}
		wpa_supplicant = FileFinder.WPA_SUPPLICANT;
	}
}
