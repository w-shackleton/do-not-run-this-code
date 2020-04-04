package uk.digitalsquid.spacegame;

import android.content.Context;
import android.os.Vibrator;
import android.preference.PreferenceManager;

public class BounceVibrate
{
	private static Vibrator vibrator;
	
	public static void initialise(Context context)
	{
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("vibrate", true))
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		else
			vibrator = null;
	}
	
	public static void Nullify()
	{
		vibrator = null;
	}
	
	protected static final float VIBRATE_MULTIPLIER = 0.5f;
	
	public static void Vibrate(long millis)
	{
		if(millis < 5)
			return;
		if(vibrator != null)
			vibrator.vibrate((long) (millis * VIBRATE_MULTIPLIER));
	}
}
