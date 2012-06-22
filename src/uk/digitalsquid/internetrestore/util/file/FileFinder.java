package uk.digitalsquid.internetrestore.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.util.ProcessRunner;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class FileFinder {
	public FileFinder(Context context) {
		this.context = context;
		initialise(context);
	}
	private final Context context;
	
	private String su = "";
	private String busybox = "";
	
	/**
	 * The system's version of BB, if it exists.
	 */
	@SuppressWarnings("unused")
	private String systemBusybox = "";
	
	private String wpa_supplicant = "";
	
	public String getSuPath() throws FileNotFoundException {
		if(su == "")
			throw new FileNotFoundException("su");
		return su;
	}
	
	public String getBusyboxPath() throws FileNotFoundException {
		if(busybox == "")
			throw new FileNotFoundException("busybox");
		return busybox;
	}
	
	public String getWpaSupplicantPath() throws FileNotFoundException {
		if(wpa_supplicant == "")
			throw new FileNotFoundException("wpa_supplicant");
		return wpa_supplicant;
	}
	
	private static final String[] BB_PATHS = { "/system/bin/busybox", "/system/xbin/busybox", "/system/sbin/busybox", "/vendor/bin/busybox", "busybox" };
	private static final String[] SU_PATHS = { "/system/bin/su", "/system/xbin/su", "/system/sbin/su", "/vendor/bin/su", "su" };
	private static final String[] WPA_PATHS = { "/system/bin/wpa_supplicant", "/system/xbin/wpa_supplicant", "/system/sbin/wpa_supplicant", "/vendor/bin/wpa_supplicant", "wpa_supplicant" };
	
	/**
	 * Searches for the busybox executable. Uses the builtin one if user wants. This is also the default behaviour.
	 * @return
	 */
	private String findBusybox(boolean useLocal, SharedPreferences prefs) {
		if(useLocal && prefs != null) {
			if(prefs.getBoolean("builtinbusybox", true)) {
				String myBB = FileInstaller.getScriptPath(context, "busybox");
				Logg.i("Using local copy of BB");
				return myBB; // Found our copy of BB
			}
			String customPath = prefs.getString("pathToBB", "");
			if(!customPath.equals("") && new File(customPath).exists()) return customPath;
		}
		for(String bb : BB_PATHS) {
			if(new File(bb).exists()) {
				return bb;
			}
		}
		return "";
	}
	
	/**
	 * Searches for the su executable
	 * @return
	 */
	private String findSu(SharedPreferences prefs) {
		if(prefs != null) {
			String customPath = prefs.getString("pathToSu", "");
			if(!customPath.equals("") && new File(customPath).exists()) return customPath;
		}
		for(String su : SU_PATHS) {
			if(new File(su).exists()) {
				return su;
			}
		}
		return "";
	}
	
	/**
	 * Searches for the wpa_supplicant executable
	 * @return
	 */
	private String findWpa(SharedPreferences prefs) {
		if(prefs != null) {
			String customPath = prefs.getString("pathToWpa", "");
			if(!customPath.equals("") && new File(customPath).exists()) return customPath;
		}
		for(String wpa : WPA_PATHS) {
			if(new File(wpa).exists()) {
				return wpa;
			}
		}
		return "";
	}
	
	private void initialise(Context appContext) {
		loadPaths();
	}
	
	/**
	 * (re)loads the su and BB paths, perhaps after a preference change.
	 * @throws FileNotFoundException 
	 */
	public void loadPaths() {
		SharedPreferences prefs = null;
		if(context != null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		busybox = findBusybox(true, prefs);
		systemBusybox = findBusybox(false, prefs);
		su = findSu(prefs);
		wpa_supplicant = findWpa(prefs);
	}
	
	/**
	 * Returns a list of missing files.
	 * @return
	 */
	public String[] getMissingFiles() {
		ArrayList<String> list = new ArrayList<String>(2);
		if(busybox.equals("")) list.add("busybox");
		if(su.equals("")) list.add("su");
		if(wpa_supplicant.equals("")) list.add("wpa_supplicant");
		addBBMissingFunctions(list);
		return list.toArray(null);
	}
	
	/**
	 * Checks that the necessary BB commands are available
	 * @throws FileNotFoundException 
	 */
	private final void addBBMissingFunctions(ArrayList<String> out) {
		List<String> result = new LinkedList<String>();
		try {
			ProcessRunner.runProcess(null, result, busybox);
		} catch (IOException e) {
			Logg.e("Failed to check BB programs, probably as BB doesn't exist?");
			e.printStackTrace();
		}
		String requiredApplets[] = {
				"mkdir",
				"cp",
		};
		boolean foundApplets[] = new boolean[requiredApplets.length];
		for(String line : result) {
			int i = 0;
			for(String applet : requiredApplets) {
				if(line.contains(applet))
					foundApplets[i] = true;
				i++;
			}
		}
		int i = 0;
		for(boolean found : foundApplets) {
			if(!found) out.add("bb:"+requiredApplets[i]);
			i++;
		}
	}
}
