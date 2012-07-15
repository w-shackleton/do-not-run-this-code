package uk.digitalsquid.internetrestore.manager;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Class for managing Android's internal Wifi manager (mainly stopping it)
 * @author william
 *
 */
public class AndroidWifi {
	
	private App app;
	private WifiManager wifiManager;
	
	private boolean wasRunningBefore;
	
	public AndroidWifi(App app) {
		this.app = app;
		wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
	}
	
	public boolean isWifiRunning() {
		return wifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLED;
	}
	
	/**
	 * Stops Android's {@link WifiManager} synchronously from any thread.
	 */
	public void stopWifiSync() {
		wasRunningBefore = isWifiRunning();
		wifiManager.setWifiEnabled(false);
		while(isWifiRunning()) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) { }
		}
		app.registerReceiver(wifiReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	/**
	 * Restarts Android's wifi if it was started before.
	 */
	public void startWifiIfNecessary() {
		app.unregisterReceiver(wifiReceiver);
		if(!wasRunningBefore) return;
		Logg.i("Wifi was running previously, restarting");
		wifiManager.setWifiEnabled(true);
	}
	
	private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		   NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		   if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			   if(isWifiRunning()) {
				   Toast.makeText(app,
						   "WARNING: Android has tried to start Wifi while Internet Restore is running!",
						   Toast.LENGTH_LONG).show();
			   }
		   }
		}
	};
}
