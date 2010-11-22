package uk.digitalsquid.spacegame;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StaticInfo
{
	public static boolean Antialiasing;
	public static boolean Starfield;
	
	public static final void initialise(Context context)
	{
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Antialiasing = p.getBoolean("antialiasing", true);
		Starfield = p.getBoolean("starfield", true);
	}
}
