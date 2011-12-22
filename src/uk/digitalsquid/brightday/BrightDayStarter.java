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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BrightDayStarter extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent pIntent)
	{
		//context.startService(new Intent(context,BrightDayService.class));
		if(context.getSharedPreferences("BrightDay", 0).getInt("shouldAutoStart", 0) == 1)
		{
        	Intent intent = new Intent(context, BrightDayTick.class);
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        	
        	AlarmManager alarmM = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        	alarmM.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10 * 60 * 1000, pendingIntent);
			Log.d("BrightDay", "BDSt: Starting BrightDay Service");
		}
		
	}
}
