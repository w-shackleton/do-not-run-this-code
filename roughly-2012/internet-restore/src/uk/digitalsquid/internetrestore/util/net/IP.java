package uk.digitalsquid.internetrestore.util.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IP {
	private IP() { }
	
	public static final InetAddress inetFromInt(int ip) throws UnknownHostException {
		return inetFromInt((long)ip);
	}
	public static final InetAddress inetFromInt(long ip) throws UnknownHostException {
		return InetAddress.getByAddress(new byte[] {
				(byte) ((ip >> 0 ) & 0xFF),
				(byte) ((ip >> 8 ) & 0xFF),
				(byte) ((ip >> 16) & 0xFF),
				(byte) ((ip >>>24) & 0xFF),
						});
	}
	
	public static final InetAddress reverseInetFromInt(int ip) throws UnknownHostException {
		return reverseInetFromInt((long)ip);
	}
	public static final InetAddress reverseInetFromInt(long ip) throws UnknownHostException {
		return InetAddress.getByAddress(new byte[] {
				(byte) ((ip >>>24) & 0xFF),
				(byte) ((ip >> 16) & 0xFF),
				(byte) ((ip >> 8 ) & 0xFF),
				(byte) ((ip >> 0 ) & 0xFF),
						});
	}
}
