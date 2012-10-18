package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InterfaceAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.AsyncTaskHelper;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.jni.WpaControl;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;

/**
 * Manages the dhcpcd daemon.
 * @author william
 *
 */
public class Dhcpcd {
	public static final String INTENT_IPSTATUS = "uk.digitalsquid.internetrestore.manager.Dhcpcd.InetAddress";
	public static final String INTENT_EXTRA_INET_ADDR = "uk.digitalsquid.internetrestore.manager.Dhcpcd.InetAddress.addr";
	public static final String INTENT_EXTRA_SUBNET_MASK = "uk.digitalsquid.internetrestore.manager.Dhcpcd.InetAddress.subnet";
	
	private String dhcpcd;
	private final Runner runner;
	private final App app;
	
	private final String iface;
	
	private IpListener ipListener;
	
	public Dhcpcd(App app, Runner runner) throws MissingFeatureException {
		this.app = app;
		this.runner = runner;
		try {
			dhcpcd = app.getFileFinder().getDhcpcdPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("dhcpcd))")) {
				MissingFeatureException exc =
						new MissingFeatureException("dhcpcd is missing",
								R.string.no_dhcpcd, app.getAppName());
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
	
	/**
	 * Starts the DHCPCd daemon on the Wifi interface
	 * @throws IOException 
	 */
	public synchronized void start() throws IOException {
		// Note that this method of running processes only works on SDK >= 9.
		// This is fine for this app as we are using SDK 10.
		final String cmdLine = String.format("%s %s",
				dhcpcd,
				iface
				);
		Logg.d("Running command line \"" + cmdLine + "\"");
		
		runner.sendCommand("create dhcpcd;");
		runner.sendCommand(String.format("set args dhcpcd %s;", cmdLine));
		runner.sendCommand("start dhcpcd;");
		
		ipListener = new IpListener(app, iface);
		AsyncTaskHelper.execute(ipListener);
	}
	
	/**
	 * Stops Dhcpcd. If it hasn't detached, this command kills the process.
	 * Otherwise, it looks for the PID file and kills it that way.
	 */
	public synchronized void stop() {
		if(ipListener != null) {
			ipListener.cancel(false);
			ipListener = null;
		}
		
		String kill_dhcpcd = app.getFileInstaller().getScriptPath(FileInstaller.BIN_KILL_DHCPCD);
		
		try {
			runner.sendCommand("create dhcpcdkill;");
			runner.sendCommand(String.format("set args dhcpcdkill %s;", kill_dhcpcd));
			runner.sendCommand("start dhcpcdkill;");
			runner.sendCommand("sleep 2;");
			runner.sendCommand("stop dhcpcd;");
		} catch (IOException e) {
			Logg.e("Failed to stop dhcpcd (runner error), ", e);
		}
	}
	
	static final class IpListener extends AsyncTask<Void, InterfaceAddress, Void> {
		
		private final App app;
		private final String iface;
		
		public IpListener(App app, String iface) {
			this.app = app;
			this.iface = iface;
			// Start monitoring network ifaces for changes.
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			
			// Receive signals from WpaControl that something has happened
	    	IntentFilter filter = new IntentFilter(WpaControl.INTENT_WPASTATUS);
	    	app.registerReceiver(wpaReceiver, filter);
		}

		@Override
		protected Void doInBackground(Void... params) {
			Logg.i("Starting to check for IPs...");
			while(!isCancelled()) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) { }
				try {
					InterfaceAddress addr = app.getInfoCollector().getIpFromIface(iface);
					if(addr != null) publishProgress(addr);
				} catch (SocketException e) {
					Logg.w("Failed to get address of Wifi (2)", e);
				}
			}
			Logg.i("IP checker stopped");
			return null;
		}
		
		@Override
		protected void onProgressUpdate(InterfaceAddress... values) {
			super.onProgressUpdate(values);
			Intent intent = new Intent(INTENT_IPSTATUS);
			intent.putExtra(INTENT_EXTRA_INET_ADDR, values[0].getAddress());
			intent.putExtra(INTENT_EXTRA_SUBNET_MASK, values[0].getNetworkPrefixLength());
			app.sendBroadcast(intent, Manifest.permission.ACCESS_WIFI_STATE);
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			app.unregisterReceiver(wpaReceiver);
		}
		
		final BroadcastReceiver wpaReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean connected = intent.getBooleanExtra(WpaControl.INTENT_EXTRA_CONNECTED, false);
				if(connected) {
					try {
						InterfaceAddress addr = app.getInfoCollector().getIpFromIface(iface);
						if(addr != null) onProgressUpdate(addr);
					} catch (SocketException e) {
						Logg.w("Failed to get address of Wifi (1)", e);
					}
				}
			}
		};
	}
}
