package uk.digitalsquid.internetrestore;

import android.app.Application;

public class App extends Application {
	
	private String appName;

	public String getAppName() {
		if(appName == null) appName = getString(R.string.app_name);
		return appName;
	}
}
