package uk.digitalsquid.spacegamelib;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;

public final class StaticInfo {
	public static boolean Antialiasing;
	public static boolean Starfield;
	
	public static final void initialise(Context context)
	{
		SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
		Antialiasing = p.getBoolean("antialiasing", true);
		Starfield = p.getBoolean("starfield", true);
		
		Fonts.bangers = Typeface.createFromAsset(context.getAssets(), "fonts/bangers_custom.ttf");
	}
	
	public static final class Fonts {
		public static Typeface bangers;
	}
}
