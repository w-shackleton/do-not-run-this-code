package pennygame.lib;

import java.io.UnsupportedEncodingException;

/**
 * Contains and sets the preferences in the game
 * @author william
 *
 */
public final class GlobalPreferences {
	private static int port = 8852;
	private static int adminPort = 8853;
	private static int projectorPort = 8854;
	private static final int keySize = 1024;
	
	/**
	 * Only change this salt if needed, and also change it in the PHP admin password checker
	 */
	private static final String salt = "FJDNnfjdjfduf789ud8ffkdnsfklDUOfn8dhfjkdnhs fdjsfhdsfuhdyf7d8hfjidH&y7fd8sy7fudnJFNsario78w4uy38hfnejknf4jn589234-4i9230iroempqjg8r9g07ur890gum890 h";
	private static final int saltIterations = 100;
	
	private static final int quoteAcceptTimeout = 4;
	
	public static int getPort() {
		return port;
	}
	
	public static void setPort(int port) {
		GlobalPreferences.port = port;
	}

	public static int getKeysize() {
		return keySize;
	}

	public static String getSalt() {
		return salt;
	}
	
	public static byte[] getBSalt() {
		try {
			return salt.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new byte[1];
	}

	public static int getSaltiterations() {
		return saltIterations;
	}

	public static int getAdminport() {
		return adminPort;
	}
	
	public static void setAdminport(int adminPort) {
		GlobalPreferences.adminPort = adminPort;
	}

	public static int getQuoteAcceptTimeout() {
		return quoteAcceptTimeout;
	}

	public static void setProjectorPort(int projectorPort) {
		GlobalPreferences.projectorPort = projectorPort;
	}

	public static int getProjectorPort() {
		return projectorPort;
	}
}
