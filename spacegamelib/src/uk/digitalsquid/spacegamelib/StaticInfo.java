package uk.digitalsquid.spacegamelib;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

public final class StaticInfo implements Constants {
	public static boolean Antialiasing;
	public static boolean Starfield;
	
	public static final boolean DEBUG = true;
	
	public static final void initialise(Context context)
	{
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Antialiasing = p.getBoolean("antialiasing", true);
		Starfield = p.getBoolean("starfield", true);
		
		try {
			Fonts.bangers = Typeface.createFromAsset(context.getAssets(), "fonts/bangers_custom.ttf");
		} catch (RuntimeException e) {
			Log.e(TAG, "Can't load fonts", e);
		}
	}
	
	public static final class Fonts {
		public static Typeface bangers;
	}
}
