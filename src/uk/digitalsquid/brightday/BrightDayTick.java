/*
 * This file is part of Bright Day.
 * 
 * Bright Day is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Bright Day is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Bright Day.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.digitalsquid.brightday;

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
	
	private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
	// private static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
	private static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
	
	@Override
	public void onReceive(Context context, Intent arg1)
	{
		int result = getValue256(context);
		
   		if(Settings.System.getString(context.getContentResolver(), SCREEN_BRIGHTNESS_MODE) != null) { // Option available - Android 8
	   		Settings.System.putInt(context.getContentResolver(), SCREEN_BRIGHTNESS_MODE, SCREEN_BRIGHTNESS_MODE_MANUAL);
   		}
		
   		Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, result);
   		//Toast.makeText(context, "Pref set!", Toast.LENGTH_SHORT).show();
	}
	
	public static int cutMax(float num, int min, int max)
	{
		if(num <= (float)min) return min;
		if(num >= (float)max) return max;
		return (int)num;
	}
	
	public static final int getValue256(Context context) {
		return getValue(context, 256);
	}
	
	public static final int getValue(Context context, int outOf) {
		Date time = Calendar.getInstance().getTime();
		int minsTD = time.getHours() * 60 + time.getMinutes();
		
		SharedPreferences pref = context.getSharedPreferences("BrightDay", 0);
		ValCalc vc = new ValCalc(
				pref.getInt("minb", 0),
				pref.getInt("maxb", 0),
				pref.getInt("shift", 0),
				pref.getInt("gamma", 0),
				pref.getInt("stretch", 0),
				MINS_IN_DAY,
				outOf);
		Log.d("BrightDay", "BDT: Current Val: " + cutMax(outOf - vc.getPos(minsTD), 0, outOf));
		return cutMax(outOf - vc.getPos(minsTD), 0, outOf);
	}
}
