package uk.digitalsquid.internetrestore.settings;

import java.io.FileNotFoundException;
import java.io.IOException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.settings.wpa.WpaCollection;
import uk.digitalsquid.internetrestore.util.ProcessRunner;
import uk.digitalsquid.internetrestore.util.ProcessRunner.ProcessResult;

/**
 * Class for manipulating our copy of wpa_supplicant.conf
 * @author william
 *
 */
public class WpaSettings {
	private final App app;
	
	public WpaSettings(App app) {
		this.app = app;
	}
	
	/**
	 * Reads the system config as root, then parses it.
	 * @return The system's wpa_supplicant config
	 * @throws IOException If reading the system's wpa_supplicant.conf failed
	 * @throws FileNotFoundException If finding the system's wpa_supplicant.conf failed
	 */
	public WpaCollection readSystemConfig() throws IOException {
		// Find system wpa_supplicant.conf
		String[] potentialPaths = {
				"/data/misc/wifi/wpa_supplicant.conf"
		};
		for(String potential : potentialPaths) {
			// Read system conf as root
			// su -c "busybox cat wpa_supplicant.conf"
			ProcessResult result = ProcessRunner.runProcessWithOutput(null, ProcessRunner.GET_STDOUT,
					app.getFileFinder().getSuPath(),
					"-c",
					app.getFileFinder().getBusyboxPath() + " " + 
					"cat" + " " + 
					potential);
			if(result.returnCode != 0 && result.output.isEmpty()) continue; // Try next
			return new WpaCollection(result.output);
		}
		throw new FileNotFoundException("Couldn't find system wpa_supplicant.conf, or superuser request wasn't accepted");
	}
}
