package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.AsyncTaskHelper;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.jni.WpaControl;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.ProcessRunner;
import uk.digitalsquid.internetrestore.util.StreamGobbler;
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
	
	private String dhcpcd, su;
	private final App app;
	
	private final String iface;
	
	private Process instance;
	
	private IpListener ipListener;
	
	public Dhcpcd(App app) throws MissingFeatureException {
		this.app = app;
		try {
			dhcpcd = app.getFileFinder().getDhcpcdPath();
			su = app.getFileFinder().getSuPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("dhcpcd))")) {
				MissingFeatureException exc =
						new MissingFeatureException("dhcpcd is missing",
								R.string.no_dhcpcd, app.getAppName());
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
	
	/**
	 * Starts the DHCPCd daemon on the Wifi interface
	 * @throws IOException 
	 */
	public synchronized void start() throws IOException {
		if(instance != null) return;
		// Note that this method of running processes only works on SDK >= 9.
		// This is fine for this app as we are using SDK 10.
		final String cmdLine = String.format("%s %s",
				dhcpcd,
				iface
				);
		Logg.d("Running command line \"" + cmdLine + "\"");
		
		ProcessBuilder pb = new ProcessBuilder(
				su,
				"-c",
				cmdLine);
		
		pb.environment().put("BB", app.getFileFinder().getBusyboxPath());
		
		instance = pb.start();
		
		new StreamGobbler(instance.getInputStream(), "dhcpcd cout");
		new StreamGobbler(instance.getErrorStream(), "dhcpcd cerr");
		
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
		
		if(instance == null) return;
		instance.destroy();
		
		String kill_dhcpcd = app.getFileInstaller().getScriptPath(FileInstaller.BIN_KILL_DHCPCD);
		
		// Process is killed if dhcpcd hadn't detached.
		// Now run a script to kill it using the pidfile
		try {
			ProcessRunner.runProcess(su, "-c", kill_dhcpcd);
		} catch (IOException e) {
			Logg.w("Dhcpcd couldn't be killed", e);
		}
	}
	
	static final class IpListener extends AsyncTask<Void, InetAddress, Void> {
		
		private final App app;
		private final String iface;
		
		public IpListener(App app, String iface) {
			this.app = app;
			this.iface = iface;
			// Start monitoring network ifaces for changes.
			// Also receive signals from WpaControl that something has happened
	    	
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
					InetAddress addr = app.getInfoCollector().getIpFromIface(iface);
					publishProgress(addr);
				} catch (SocketException e) {
					Logg.w("Failed to get address of Wifi (2)", e);
				}
			}
			Logg.i("IP checker stopped");
			app.unregisterReceiver(wpaReceiver);
			return null;
		}
		
		@Override
		protected void onProgressUpdate(InetAddress... values) {
			super.onProgressUpdate(values);
			Intent intent = new Intent(INTENT_IPSTATUS);
			intent.putExtra(INTENT_EXTRA_INET_ADDR, values[0]);
			app.sendBroadcast(intent, Manifest.permission.ACCESS_WIFI_STATE);
		}
		
		final BroadcastReceiver wpaReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean connected = intent.getBooleanExtra(WpaControl.INTENT_EXTRA_CONNECTED, false);
				if(connected) {
					try {
						InetAddress addr = app.getInfoCollector().getIpFromIface(iface);
						onProgressUpdate(addr);
					} catch (SocketException e) {
						Logg.w("Failed to get address of Wifi (1)", e);
					}
				}
			}
		};
	}
}
