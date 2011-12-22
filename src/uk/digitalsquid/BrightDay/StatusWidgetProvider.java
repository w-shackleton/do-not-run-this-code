package uk.digitalsquid.BrightDay;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
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
            
            appWidgetManager.updateAppWidget(id, views);
		}
	}
}
