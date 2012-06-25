package uk.digitalsquid.internetrestore;

import uk.digitalsquid.internetrestore.settings.WpaSettings;
import uk.digitalsquid.internetrestore.util.file.FileFinder;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;
import android.app.Application;

public class App extends Application {
	
	private String appName;

	public String getAppName() {
		if(appName == null) appName = getString(R.string.app_name);
		return appName;
	}
	
	private FileFinder fileFinder;
	
	public FileFinder getFileFinder() {
		if(fileFinder == null) fileFinder = new FileFinder(this);
		return fileFinder;
	}
	
	private FileInstaller fileInstaller;
	
	public FileInstaller getFileInstaller() {
		if(fileInstaller == null) fileInstaller = new FileInstaller(this);
		return fileInstaller;
	}
	
	private WpaSettings wpaSettings;
	
	public WpaSettings getWpaSettings() {
		if(wpaSettings == null) wpaSettings = new WpaSettings(this);
		return wpaSettings;
	}
}
