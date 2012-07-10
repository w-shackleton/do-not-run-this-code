package uk.digitalsquid.internetrestore.settings;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.util.net.IP;
import uk.digitalsquid.internetrestore.util.net.Mac;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Gathers various system information
 * @author william
 *
 */
public class InfoCollector {
	final Context context;
	
	private WifiManager wifi;
	
	public InfoCollector(Context context) {
		this.context = context;
	}

	public String getWifiIface() throws UnknownHostException {
		if(wifi == null) wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		try {
			return getWifiIfaceByIp();
		} catch (UnknownHostException e) {
			Logg.w("Failed to get interface by IP, trying alternate method", e);
		} catch (SocketException e) {
			Logg.w("Failed to get interface by IP, trying alternate method", e);
		}
		try {
			return getWifiIface2();
		} catch (UnknownHostException e) {
			UnknownHostException exc = new UnknownHostException("Couldn't get interface name of Wifi card");
			exc.initCause(e);
			throw exc;
		} catch (SocketException e) {
			UnknownHostException exc = new UnknownHostException("Couldn't get interface name of Wifi card - " + e.getMessage());
			exc.initCause(e);
			throw exc;
		}
	}
	private String getWifiIfaceByIp() throws UnknownHostException, SocketException {
		WifiInfo info = wifi.getConnectionInfo();
		InetAddress addr = IP.inetFromInt(info.getIpAddress());
		Logg.i("Addr: " + addr);
		NetworkInterface iface = NetworkInterface.getByInetAddress(addr);
		if(iface == null) throw new UnknownHostException("Couldn't get NetworkInterface");
		return iface.getDisplayName();
	}
	
	private String getWifiIface2() throws SocketException, UnknownHostException {
		String wifiMac = wifi.getConnectionInfo().getMacAddress();
		Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
		while(ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
			String comparison = Mac.format(iface.getHardwareAddress());
			if(wifiMac != null && wifiMac.equalsIgnoreCase(comparison))
				return iface.getDisplayName();
		}
		throw new UnknownHostException("Couldn't identify iface by mac");
	}
}
