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

package uk.digitalsquid.BrightDay;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class BrightDay extends Activity implements OnSeekBarChangeListener, OnClickListener
{
	private BrightDayGraph graph;
	private Button saveButton;
	private SeekBar minSeek, maxSeek, shiftSeek, gammaSeek, stretchSeek;// stretchYSeek;
	private TextView gv1, gv2, gv3;// gv4, gv5;
	private boolean saveVisible = false;
	private SharedPreferences pref;
	private String start = "", stop = "", about = "";
	private String tutorial = "", website = "";
	private LinearLayout warningM;
	public static final int DIALOG_ABOUT = 1;
	public static final int DIALOG_FIRSTRUN = 2;
	
	AlarmManager alarmM;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        graph = new BrightDayGraph(this, dm);
        setContentView(R.layout.main);
        ((LinearLayout)findViewById(R.id.GraphLayoutHolder)).addView(graph);

        minSeek = (SeekBar)findViewById(R.id.MinBrightnessSeekBar);
        maxSeek = (SeekBar)findViewById(R.id.MaxBrightnessSeekBar);
        shiftSeek = (SeekBar)findViewById(R.id.ShiftSeekBar);
        gammaSeek = (SeekBar)findViewById(R.id.GammaSeekBar);
        stretchSeek = (SeekBar)findViewById(R.id.StretchSeekBar);
        //stretchYSeek = (SeekBar)findViewById(R.id.StretchYSeekBar);
        gv1 = (TextView)findViewById(R.id.gv1);
        gv2 = (TextView)findViewById(R.id.gv2);
        gv3 = (TextView)findViewById(R.id.gv3);
        //gv4 = (TextView)findViewById(R.id.gv4);
        //gv5 = (TextView)findViewById(R.id.gv5);
        
        ((ImageView)findViewById(R.id.BdRunningImage)).setImageResource(android.R.drawable.stat_notify_error);
        warningM = (LinearLayout)findViewById(R.id.BdRunningLayout);
        warningM.setBackgroundColor(0xFF000000);
        ((LinearLayout)findViewById(R.id.BdRunningSubLayout)).setBackgroundColor(0xFFFFFFFF);
        ((TextView)findViewById(R.id.BdRunningText)).setTextColor(0xFF000000);
        
        saveButton = (Button)findViewById(R.id.SaveButton);
        saveButton.setOnClickListener(this);
        saveButton.setVisibility(Button.INVISIBLE);
        pref = getSharedPreferences("BrightDay", 0);

        graph.minBright = pref.getInt("minb", 0);
        graph.maxBright = pref.getInt("maxb", 255);
        graph.shift = pref.getInt("shift", 50);
        graph.gamma = pref.getInt("gamma", 0);
        graph.stretch = pref.getInt("stretch", 50);
        //graph.stretchY = pref.getInt("stretchY", 50);
        graph.setToCurrent();

        gv1.setText(String.valueOf((int)((float)pref.getInt("minb", 0) / 255f * 100f)) + "%");
        gv2.setText(String.valueOf((int)((float)pref.getInt("maxb", 255) / 255f * 100f)) + "%");
        gv3.setText(String.valueOf((int)((float)pref.getInt("shift", 50) / 50f * 12f - 12)) + "h");

        minSeek.setProgress(pref.getInt("minb", 0));
        maxSeek.setProgress(pref.getInt("maxb", 255));
        shiftSeek.setProgress(pref.getInt("shift", 50));
        gammaSeek.setProgress(pref.getInt("gamma", 0));
        stretchSeek.setProgress(pref.getInt("stretch", 50));
        //stretchYSeek.setProgress(pref.getInt("stretchY", 50));
        
        minSeek.setOnSeekBarChangeListener(this);
        maxSeek.setOnSeekBarChangeListener(this);
        shiftSeek.setOnSeekBarChangeListener(this);
        gammaSeek.setOnSeekBarChangeListener(this);
        stretchSeek.setOnSeekBarChangeListener(this);
        //stretchYSeek.setOnSeekBarChangeListener(this);
        
        start = getString(R.string.start);
        stop = getString(R.string.stop);
        about = getString(R.string.about);
        tutorial = getString(R.string.tutorial);
        website = getString(R.string.website);
        
        if(pref.getInt("servStarted", 0) == 0)
        {
        	warningM.setVisibility(View.VISIBLE);
        }
        if(pref.getInt("firstRun", 1) == 1)
        {
        	pref.edit().putInt("firstRun", 0).commit();
        	showDialog(DIALOG_FIRSTRUN);
        }
        
        alarmM = (AlarmManager) getSystemService(ALARM_SERVICE);
		Log.d("BrightDay", "BD: Started");
    }
    @Override
    public void onDestroy()
    {
    	super.onDestroy();
    	savePrefs();
		Log.d("BrightDay", "BD: Stopped");
    }

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		switch (seekBar.getId())
		{
		case R.id.MinBrightnessSeekBar:
			graph.minBright = progress;
			if(maxSeek.getProgress() < progress)
			{
				maxSeek.setProgress(progress);
		        gv2.setText(String.valueOf((int)((float)progress / 255f * 100f)) + "%");
			}
	        gv1.setText(String.valueOf((int)((float)progress / 255f * 100f)) + "%");
			break;
		case R.id.MaxBrightnessSeekBar:
			graph.maxBright = progress;
			if(minSeek.getProgress() > progress)
			{
				minSeek.setProgress(progress);
	        	gv1.setText(String.valueOf((int)((float)progress / 255f * 100f)) + "%");
			}
	        gv2.setText(String.valueOf((int)((float)progress / 255f * 100f)) + "%");
			break;
		case R.id.ShiftSeekBar:
			graph.shift = progress;
	        gv3.setText(String.valueOf((int)((float)progress / 50f * 12f - 12)) + "h");
			break;
		case R.id.GammaSeekBar:
			graph.gamma = progress;
			break;
		case R.id.StretchSeekBar:
			graph.stretch = progress;
			break;
		//case R.id.StretchYSeekBar:
		//	graph.stretchY = progress;
		//	break;
		}
		graph.invalidate();
		if(!saveVisible)
		{
	        saveButton.setVisibility(Button.VISIBLE);
	        saveButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein));
	        saveVisible = true;
		}
	}

	public void onStartTrackingTouch(SeekBar seekBar){}
	public void onStopTrackingTouch(SeekBar seekBar){}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		super.onCreateOptionsMenu(menu);
		if(pref.getInt("servStarted", 0) == 0)
		{
			MenuItem item = menu.add(start);
			item.setIcon(android.R.drawable.ic_menu_rotate);
		}
		else
		{
			MenuItem item = menu.add(stop);
			item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			Log.i("BrightDay", "BD: Service already running!");
		}
		menu.add(about).setIcon(android.R.drawable.ic_menu_info_details);
		menu.add(tutorial).setIcon(android.R.drawable.ic_menu_help);
		menu.add(website).setIcon(android.R.drawable.ic_menu_help);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//Log.v("BrightDay", "BD: Menu item selected - sAS = \"" + pref.getInt("shouldAutoStart", 2) + "\"");
		//Log.v("BrightDay", "BD:                    -  sS = \"" + pref.getInt("servStarted", 2) + "\"");
		if(item.getTitle() == start)
		{
			Log.i("BrightDay", "BD: Start");
			item.setTitle(stop);
			item.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
			pref.edit().putInt("shouldAutoStart", 1).commit();
			pref.edit().putInt("servStarted", 1).commit();
        	
        	Intent intent = new Intent(this, BrightDayTick.class);
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);

        	alarmM.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, 10 * 60 * 1000, pendingIntent);
        	
        	warningM.setVisibility(View.INVISIBLE);
        	warningM.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout));
			return true;
		}
		if(item.getTitle() == stop)
		{
			Log.i("BrightDay", "BD: Stop");
			item.setTitle(start);
			item.setIcon(android.R.drawable.ic_menu_rotate);
			//Log.v("BrightDay", "BD: Menu info changed");
			Log.v("BrightDay", "BD: Service stopped");
			pref.edit().putInt("shouldAutoStart", 0).commit();
			pref.edit().putInt("servStarted", 0).commit();
			//Log.v("BrightDay", "BD: shouldAutoStart changed to 0");
        	
        	Intent intent = new Intent(this, BrightDayTick.class);
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 0, intent, 0);
        	
        	alarmM.cancel(pendingIntent);
        	
        	warningM.setVisibility(View.VISIBLE);
        	warningM.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadein));
			return true;
		}
		if(item.getTitle() == about)
		{
			Log.i("BrightDay", "BD: About");
			
			showDialog(DIALOG_ABOUT);
			
			return true;
		}
		if(item.getTitle() == website)
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://digitalsquid.co.uk/brightday/")));
		if(item.getTitle() == tutorial)
			startActivity(new Intent(BrightDay.this, BrightDayHelp.class)); 
		return true;
	}

	public void onClick(View v)
	{
		if(saveVisible)
		{
	        saveButton.setVisibility(Button.INVISIBLE);
	        saveButton.startAnimation(AnimationUtils.loadAnimation(getBaseContext(), R.anim.fadeout));
	        saveVisible = false;
	        savePrefs();
	        graph.setToCurrent();
		}
	}
	
	private void savePrefs()
	{
		Editor e = pref.edit();
		e.putInt("minb", minSeek.getProgress());
		e.putInt("maxb", maxSeek.getProgress());
		e.putInt("shift", shiftSeek.getProgress());
		e.putInt("gamma", gammaSeek.getProgress());
		e.putInt("stretch", stretchSeek.getProgress());
		//e.putInt("stretchY", stretchYSeek.getProgress());
		e.commit();
		
    	Intent intent = new Intent(this, BrightDayTick.class);
    	PendingIntent pI = PendingIntent.getBroadcast(this, 0, intent, 0);
    	try {
			pI.send();
		} catch (CanceledException e1){	}
		
		WindowManager.LayoutParams layout = getWindow().getAttributes();
		layout.screenBrightness = (float)BrightDayTick.getValue256(getApplicationContext()) / 256f;
		getWindow().setAttributes(layout);
	}
	
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
		case DIALOG_ABOUT:
		    Dialog dialog =  new Dialog(this);
			dialog.setContentView(R.layout.about);
			dialog.setTitle(R.string.aboutbd);
		    return dialog;
		case DIALOG_FIRSTRUN:
			return new AlertDialog.Builder(this)
					.setTitle(R.string.welcomeTitle)
					.setMessage(R.string.welcome)
					.setPositiveButton(R.string.ok, new
							DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
									startActivity(new Intent(BrightDay.this, BrightDayHelp.class));
								}
							})
					.setNegativeButton(R.string.no, new
							DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog, int whichButton)
								{
									
								}
							})
					.create();
		}
		return null;
	}
}