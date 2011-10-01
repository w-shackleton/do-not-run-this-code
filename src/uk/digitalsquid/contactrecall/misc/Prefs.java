package uk.digitalsquid.contactrecall.misc;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public final class Prefs {
	
	private final SharedPreferences pref;
	
	public Prefs(Context context) {
		pref = PreferenceManager.getDefaultSharedPreferences(context);
	}
}
