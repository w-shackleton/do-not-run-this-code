package uk.digitalsquid.internetrestore.settings;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.util.IP;
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
			String iface = getWifiIfaceByIp();
			return iface;
		} catch (UnknownHostException e) {
			Logg.w("Failed to get interface by IP, trying alternate method", e);
		} catch (SocketException e) {
			Logg.w("Failed to get interface by IP, trying alternate method", e);
		}
		throw new UnknownHostException("Couldn't get interface name of Wifi card");
	}
	private String getWifiIfaceByIp() throws UnknownHostException, SocketException {
		WifiInfo info = wifi.getConnectionInfo();
		InetAddress addr = IP.reverseInetFromInt(info.getIpAddress());
		Logg.i("Addr: " + addr);
		NetworkInterface iface = NetworkInterface.getByInetAddress(addr);
		return iface.getDisplayName();
	}
	
	private String getWifiIface2() throws SocketException {
		String wifiMac = wifi.getConnectionInfo().getMacAddress();
		Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
		while(ifaces.hasMoreElements()) {
			NetworkInterface iface = ifaces.nextElement();
		}
		return "";
	}
}
