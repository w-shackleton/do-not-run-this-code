package uk.digitalsquid.brightday;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

/**
 * Widget showing the status of Bright Day etc.
 * @author william
 *
 */
public class StatusWidgetProvider extends AppWidgetProvider {
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
        		new Intent(context, BrightDay.class), 0);
		for(final int id : appWidgetIds) {
			// Create an Intent to launch ExampleActivity
	        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
            views.setOnClickPendingIntent(R.id.widgetBg, pendingIntent);
            
            int percent = BrightDayTick.getValue(context, 100);
            views.setTextViewText(R.id.widgetText, "" + percent + "%");
            
            // Set day/night
			SharedPreferences pref = context.getSharedPreferences("BrightDay", 0);
            int current = BrightDayTick.getValue256(context);
			int min = pref.getInt("minb", 0);
			int max = pref.getInt("maxb", 0);
			boolean day = current > (min+max) / 2; // Further than 1/2 way
			if(day) {
				int col = context.getResources().getColor(R.color.widgetText);
				views.setTextColor(R.id.widgetText, col);
				views.setInt(R.id.widgetBg, "setBackgroundResource", R.drawable.widget_daytime);
			} else {
				int col = context.getResources().getColor(R.color.widgetTextNight);
				views.setTextColor(R.id.widgetText, col);
				views.setInt(R.id.widgetBg, "setBackgroundResource", R.drawable.widget_nighttime);
			}
            
            appWidgetManager.updateAppWidget(id, views);
		}
	}
}
