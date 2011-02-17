package pennygame.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import pennygame.lib.GlobalPreferences;

/**
 * Loads the server settings from a settings file and allows them to be accessed.
 * @author william
 *
 */
public final class SettingsLoader {
	
	private static String listenAddress = "0.0.0.0";
	private static int listenPort = GlobalPreferences.getPort();
	private static int adminListenPort = GlobalPreferences.getAdminport();
	private static int projectorListenPort = GlobalPreferences.getProjectorPort();
	private static String mySqlAddress;
	private static String mySqlDatabase;
	private static String mySqlUsername;
	private static String mySqlPassword;
	
	private static String adminEncryptedPassword;
	private static String projectorEncryptedPassword;
	
	public static final void loadSettings(String filename) throws IOException {
		FileReader fr = new FileReader(filename);
		BufferedReader f = new BufferedReader(fr);
		
		String s;
		while((s = f.readLine()) != null) {
			String[] params = s.split(":");
			if(params.length < 2) continue;
			
			if       (params[0].equals("listenAddress")) { // Parse each item
				setListenAddress(params[1]);
			} else if(params[0].equals("listenPort")) {
				setListenPort(Integer.valueOf(params[1]));
			} else if(params[0].equals("adminListenPort")) {
				setAdminListenPort(Integer.valueOf(params[1]));
			} else if(params[0].equals("projectorListenPort")) {
				setProjectorListenPort(Integer.valueOf(params[1]));
			} else if(params[0].equals("mySqlAddress")) {
				setMySqlAddress(params[1]);
			} else if(params[0].equals("mySqlDatabase")) {
				setMySqlDatabase(params[1]);
			} else if(params[0].equals("mySqlUsername")) {
				setMySqlUsername(params[1]);
			} else if(params[0].equals("mySqlPassword")) {
				setMySqlPassword(params[1]);
			} else if(params[0].equals("adminEncryptedPassword")) {
				setAdminEncryptedPassword(params[1]);
			} else if(params[0].equals("projectorEncryptedPassword")) {
				setProjectorEncryptedPassword(params[1]);
			}
		}
		f.close();
		fr.close();
	}

	protected static void setListenAddress(String listenAddress) {
		SettingsLoader.listenAddress = listenAddress;
	}

	public static String getListenAddress() {
		return listenAddress;
	}

	protected static void setListenPort(int listenPort) {
		SettingsLoader.listenPort = listenPort;
	}

	public static int getListenPort() {
		return listenPort;
	}

	protected static void setMySqlAddress(String mySqlAddress) {
		SettingsLoader.mySqlAddress = mySqlAddress;
	}

	public static String getMySqlAddress() {
		return mySqlAddress;
	}

	protected static void setMySqlDatabase(String mySqlDatabase) {
		SettingsLoader.mySqlDatabase = mySqlDatabase;
	}

	public static String getMySqlDatabase() {
		return mySqlDatabase;
	}

	protected static void setMySqlUsername(String mySqlUsername) {
		SettingsLoader.mySqlUsername = mySqlUsername;
	}

	public static String getMySqlUsername() {
		return mySqlUsername;
	}

	protected static void setMySqlPassword(String mySqlPassword) {
		SettingsLoader.mySqlPassword = mySqlPassword;
	}

	public static String getMySqlPassword() {
		return mySqlPassword;
	}

	protected static void setAdminEncryptedPassword(String adminEncryptedPassword) {
		SettingsLoader.adminEncryptedPassword = adminEncryptedPassword;
	}

	public static String getAdminEncryptedPassword() {
		return adminEncryptedPassword;
	}

	protected static void setAdminListenPort(int adminListenPort) {
		SettingsLoader.adminListenPort = adminListenPort;
	}

	public static int getAdminListenPort() {
		return adminListenPort;
	}

	protected static void setProjectorListenPort(int projectorListenPort) {
		SettingsLoader.projectorListenPort = projectorListenPort;
	}

	public static int getProjectorListenPort() {
		return projectorListenPort;
	}

	protected static void setProjectorEncryptedPassword(String projectorEncryptedPassword) {
		SettingsLoader.projectorEncryptedPassword = projectorEncryptedPassword;
	}

	public static String getProjectorEncryptedPassword() {
		return projectorEncryptedPassword;
	}
}
