package uk.digitalsquid.BrightDay;

import java.util.Calendar;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

public class BrightDayTick extends BroadcastReceiver
{
	public static final float MINS_IN_DAY = 1440;
	
	@Override
	public void onReceive(Context context, Intent arg1)
	{
		Date time = Calendar.getInstance().getTime();
		int minsTD = time.getHours() * 60 + time.getMinutes();
		Log.v("BrightDay", "BDT: Mins through day: " + String.valueOf(minsTD) + ", secs: " + String.valueOf(time.getSeconds()));
		
		SharedPreferences pref = context.getSharedPreferences("BrightDay", 0);
		ValCalc vc = new ValCalc(
				pref.getInt("minb", 0),
				pref.getInt("maxb", 0),
				pref.getInt("shift", 0),
				pref.getInt("gamma", 0),
				pref.getInt("stretch", 0),
				MINS_IN_DAY,
				256);
		Log.d("BrightDay", "BDT: Current Val: " + cutMax(256 - vc.getPos(minsTD), 0, 255));
   		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, cutMax(256 - vc.getPos(minsTD), 0, 255));
   		//Toast.makeText(context, "Pref set!", Toast.LENGTH_SHORT).show();
	}
	
	public static int cutMax(float num, int min, int max)
	{
		if(num <= (float)min) return min;
		if(num >= (float)max) return max;
		return (int)num;
	}
}
