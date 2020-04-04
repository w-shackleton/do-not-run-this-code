package uk.digitalsquid.internetrestore.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * Network info manipulation tools.
 * @author william
 *
 */
public class Network {
	private Network() {}

	/**
	 * Gets the address of a subnet, given an address in that subnet.
	 * Example: <code>getSubnetAddress(10.1.2.3, 16) = 10.1.0.0</code>
	 * @param address
	 * @param subnet
	 * @throws UnknownHostException 
	 */
	public static final InetAddress getSubnetAddress(InetAddress address, short subnet) throws UnknownHostException {
		final byte[] orig = address.getAddress();
		final byte[] dest = new byte[orig.length];
		
		for(int i = 0; i <  orig.length; i++) {
			short subnetPart = (short) (subnet - i * 8);
			if(subnetPart > 8) subnetPart = 8;
			if(subnetPart < 0) subnetPart = 0;
			
			byte mask =  (byte) ((0xFF00 >> subnetPart) & 0xFF);
			
			dest[i] = (byte) (orig[i] & mask);
		}
		
		return InetAddress.getByAddress(dest);
	}
	
	/**
	 * As far as I can see, Inet4Address.equals is broken - 
	 * http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/7-b147/java/net/Inet4Address.java#Inet4Address.equals%28java.lang.Object%29
	 * @param a
	 * @param b
	 * @return
	 */
	public static final boolean isNetworkEqual(InetAddress a, InetAddress b) {
		if(a == null) return false;
		if(b == null) return false;
		if(a.getAddress() == null) return false;
		if(b.getAddress() == null) return false;
		return Arrays.equals(a.getAddress(), b.getAddress());
	}
}
