/*
 * This file is part of Network Spoofer for Android.
 * Network Spoofer lets you change websites on other people’s computers
 * from an Android phone.
 * Copyright (C) 2011 Will Shackleton
 *
 * Network Spoofer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Network Spoofer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Network Spoofer, in the file COPYING.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.internetrestore.util.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.internetrestore.Logg;
import uk.digitalsquid.internetrestore.util.ProcessRunner;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class FileFinder {
	private FileFinder() { }
	private static boolean initialised = false;
	
	private static Context context;
	
	public static String SU = "";
	public static String BUSYBOX = "";
	
	public static String WPA_SUPPLICANT = "";
	
	/**
	 * The system's version of BB, if it exists.
	 */
	public static String SYSTEM_BUSYBOX = "";
	
	private static final String[] BB_PATHS = { "/system/bin/busybox", "/system/xbin/busybox", "/system/sbin/busybox", "/vendor/bin/busybox", "busybox" };
	private static final String[] SU_PATHS = { "/system/bin/su", "/system/xbin/su", "/system/sbin/su", "/vendor/bin/su", "su" };
	private static final String[] WPA_PATHS = { "/system/bin/wpa_supplicant", "/system/xbin/wpa_supplicant", "/system/sbin/wpa_supplicant", "/vendor/bin/wpa_supplicant", "wpa_supplicant" };
	
	/**
	 * Searches for the busybox executable. Uses the builtin one if user wants. This is also the default behaviour.
	 * @return
	 */
	private static final String findBusybox(boolean useLocal, SharedPreferences prefs) {
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
	private static final String findSu(SharedPreferences prefs) {
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
	private static final String findWpa(SharedPreferences prefs) {
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
	
	public static final void initialise(Context appContext) throws FileNotFoundException {
		FileFinder.context = appContext;
		if(initialised) {
			return;
		}
		initialised = true;
		loadPaths();
	}
	
	/**
	 * (re)loads the SU and BB paths, perhaps after a preference change.
	 * @throws FileNotFoundException 
	 */
	public static final void loadPaths() throws FileNotFoundException {
		SharedPreferences prefs = null;
		if(context != null) {
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
		}
		BUSYBOX = findBusybox(true, prefs);
		SYSTEM_BUSYBOX = findBusybox(false, prefs);
		if(BUSYBOX.equals("")) {
			throw new FileNotFoundException("busybox");
		}
		SU = findSu(prefs);
		if(SU.equals("")) {
			throw new FileNotFoundException("su");
		}
		WPA_SUPPLICANT = findWpa(prefs);
		if(WPA_SUPPLICANT.equals(""))
			throw new FileNotFoundException("wpa_supplicant");
		try {
			checkBBInstalledFunctions();
		} catch (FileNotFoundException e) { // If fails with this BB, try system BB.
			BUSYBOX = SYSTEM_BUSYBOX;
			checkBBInstalledFunctions(); // Let this one throw error
		}
	}
	
	/**
	 * Checks that the necessary BB commands are available
	 * @throws FileNotFoundException 
	 */
	static final void checkBBInstalledFunctions() throws FileNotFoundException {
		List<String> result = new LinkedList<String>();
		try {
			ProcessRunner.runProcess(null, result, BUSYBOX);
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
			if(!found) throw new FileNotFoundException("bb:"+requiredApplets[i]);
			i++;
		}
	}
	
	/**
	 * Initialise that doesn't search custom paths. Not recommended.
	 * @throws FileNotFoundException
	 */
	public static final void initialise() throws FileNotFoundException {
		if(initialised) {
			return;
		}
		initialised = true;
		BUSYBOX = findBusybox(true, null);
		if(BUSYBOX.equals("")) {
			throw new FileNotFoundException("busybox");
		}
		SU = findSu(null);
		if(SU.equals("")) {
			throw new FileNotFoundException("su");
		}
	}
}
