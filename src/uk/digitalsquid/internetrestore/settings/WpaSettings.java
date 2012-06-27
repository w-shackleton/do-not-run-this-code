package uk.digitalsquid.internetrestore.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.settings.wpa.WpaCollection;
import uk.digitalsquid.internetrestore.util.ProcessRunner;
import uk.digitalsquid.internetrestore.util.ProcessRunner.ProcessResult;
import uk.digitalsquid.internetrestore.util.file.FileIO;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

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
	 * Gets the directory in which wpa_supplicant configurations are stored.
	 * On some (all?) devices this is <code>/data/misc/wifi</code>
	 * @return
	 * @throws FileNotFoundException 
	 */
	public File getWpaDir() throws FileNotFoundException {
		final File[] dirs = {
			new File("/data/misc/wifi"),
		};
		for(File dir : dirs) {
			if(dir.isDirectory())
				return dir;
		}
		throw new FileNotFoundException("wpa folder not found");
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
				"/data/misc/wifi/wpa_supplicant.conf",
				new File(getWpaDir(), "wpa_supplicant.conf").getAbsolutePath(),
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
	
	public void writeLocalConfig(WpaCollection config) throws IOException {
		StringBuilder out = new StringBuilder();
		config.write(out);
		
		FileIO.writeContents(getLocalConfigPath(), out.toString());
	}
	
	public WpaCollection readLocalConfig() {
		String data;
		try {
			data = FileIO.readContents(getLocalConfigPath());
			return new WpaCollection(data);
		} catch (IOException e) {
			Logg.d("Couldn't get local config contents, returning empty config descriptor", e);
			return new WpaCollection();
		}
	}
	
	public File getLocalConfigPath() {
		return app.getFileInstaller().getConfFilePath(FileInstaller.CONF_WPA_SUPPLICANT);
	}
}
