package uk.digitalsquid.BrightDay;

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
