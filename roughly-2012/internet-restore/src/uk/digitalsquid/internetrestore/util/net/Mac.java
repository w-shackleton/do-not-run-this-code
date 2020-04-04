package uk.digitalsquid.internetrestore.util.net;

/**
 * Mac address utils
 * @author william
 *
 */
public class Mac {
	private Mac() { }
	
	public static final String format(byte[] mac) {
		if(mac == null) return "";
		StringBuilder sb = new StringBuilder();
		for(byte b : mac) {
			if(sb.length() > 0)
				sb.append(':');
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
