package uk.digitalsquid.internetrestore.manager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;

import uk.digitalsquid.internetrestore.App;
import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.R;
import uk.digitalsquid.internetrestore.util.MissingFeatureException;
import uk.digitalsquid.internetrestore.util.ProcessRunner;
import uk.digitalsquid.internetrestore.util.StreamGobbler;
import uk.digitalsquid.internetrestore.util.file.FileInstaller;

/**
 * Manages the dhcpcd daemon.
 * @author william
 *
 */
public class Dhcpcd {
	private String dhcpcd, su;
	private final App app;
	
	private final String iface;
	
	private Process instance;
	
	public Dhcpcd(App app) throws MissingFeatureException {
		this.app = app;
		try {
			dhcpcd = app.getFileFinder().getDhcpcdPath();
			su = app.getFileFinder().getSuPath();
		} catch (FileNotFoundException e) {
			if(e.getMessage().equalsIgnoreCase("dhcpcd))")) {
				MissingFeatureException exc =
						new MissingFeatureException("dhcpcd is missing",
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
	}
	
	/**
	 * Stops Dhcpcd. If it hasn't detached, this command kills the process.
	 * Otherwise, it looks for the PID file and kills it that way.
	 * @throws IOException 
	 */
	public synchronized void stop() throws IOException {
		if(instance == null) return;
		instance.destroy();
		
		String kill_dhcpcd = app.getFileInstaller().getScriptPath(FileInstaller.BIN_KILL_DHCPCD);
		
		// Process is killed if dhcpcd hadn't detached.
		// Now run a script to kill it using the pidfile
		ProcessRunner.runProcess(su, "-c", kill_dhcpcd);
	}
}
