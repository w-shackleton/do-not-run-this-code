package uk.digitalsquid.spacegame;

import java.io.IOException;
import java.io.InputStream;

import uk.digitalsquid.spacegame.levels.LevelItem.LevelSummary;
import uk.digitalsquid.spacegame.levels.LevelManager.LevelInfo;
import uk.digitalsquid.spacegame.views.GameView;
import uk.digitalsquid.spacegamelib.Constants;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * Main gameplay activity.
 * @author william
 *
 */
public class Game extends Activity implements Constants, OnClickListener, SensorEventListener
{
	GameView gameView;
	
	Animation panout, panin;
	
	static final int DIALOG_LEVELCOMPLETE = 1;

	public static final int GVL_MSG_INFOBOX = 1;
	public static final int GVL_MSG_PAUSE = 2;
	public static final int GVL_MSG_ENDLEVEL = 3;
	
	public static final int LEVELCOMPLETED_RETRY = 4;
	public static final int LEVELCOMPLETED_CONTINUE = 5;
	public static final int LEVELCOMPLETED_TOMENU = 6;
	
	protected Handler gvHandler = new Handler()
	{
		@Override
		public void handleMessage(Message m)
		{
			switch(m.what)
			{
			case GVL_MSG_INFOBOX:
				gameView.setPaused(true);
				infoBoxTextPointer = 0;
				infoBoxText = (String[]) m.obj;
				nextInfoMessage();
				findViewById(R.id.gameviewinfobox).setVisibility(View.VISIBLE);
				findViewById(R.id.gameviewinfobox).startAnimation(panin);
				break;
			case GVL_MSG_PAUSE:
				onBackPress();
				break;
			case GVL_MSG_ENDLEVEL:
				Bundle extras = new Bundle();
				extras.putInt("reason", m.arg1);
				extras.putParcelable("summary", (LevelSummary)m.obj);
				showDialog(DIALOG_LEVELCOMPLETE, extras);
				break;
				
			case LEVELCOMPLETED_CONTINUE:
				throw new UnsupportedOperationException("Not implemented yet");
			case LEVELCOMPLETED_RETRY:
				throw new UnsupportedOperationException("Not implemented yet");
			case LEVELCOMPLETED_TOMENU:
				gameView.stop();
				finish();
				break;
			}
		}
	};
	
	protected String[] infoBoxText;
	protected int infoBoxTextPointer;
	
	protected void nextInfoMessage() {
		if(infoBoxTextPointer < infoBoxText.length)
		{
			((TextView)findViewById(R.id.gameviewinfoboxtext)).setText(infoBoxText[infoBoxTextPointer++]);
		}
		else
		{
			findViewById(R.id.gameviewinfobox).setVisibility(View.INVISIBLE);
			findViewById(R.id.gameviewinfobox).startAnimation(panout);
			gameView.setPaused(false);
		}
	}
	
	public static final String LEVELINFO_EXTRA = "uk.digitalsquid.spacegame.Game.LevelInfoExtra";
	
	App app;
	
	SensorManager sensorManager;
	Sensor accel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gameview);
		app = (App)getApplication();
		
		panout = AnimationUtils.loadAnimation(this, R.anim.panout);
		panin = AnimationUtils.loadAnimation(this, R.anim.panin);
		
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		LevelInfo info = (LevelInfo) getIntent().getExtras().getSerializable(LEVELINFO_EXTRA);
		if(info == null) {
			Log.e(TAG, "No level info given!");
			finish();
			return;
		}

		InputStream level;
		try {
			level = app.getLevelManager().getLevelIStream(info);
		} catch (IOException e) {
			Log.e(TAG, "Failed to open level", e);
			finish();
			return;
		}
		gameView = (GameView) findViewById(R.id.gameview);
		gameView.setLevel(level);
		gameView.setGameHandler(gvHandler);
		gameView.setFocusable(false);
		gameView.setFocusableInTouchMode(false);
		gameView.create();

		findViewById(R.id.gameviewbuttonresume).setOnClickListener(this);
		findViewById(R.id.gameviewbuttonquit).setOnClickListener(this);

		findViewById(R.id.gameviewinfoboxpic).setOnClickListener(this);
		findViewById(R.id.gameviewinfoboxtext).setOnClickListener(this);
		
		findViewById(R.id.gameviewbuttons).setVisibility(View.GONE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
		switch(keyCode) {
		case KeyEvent.KEYCODE_BACK:
			onBackPress();
			return true;
		}
		return false;
	}

	public void onBackPress() {
		if(findViewById(R.id.gameviewinfobox).getVisibility() == View.INVISIBLE)
		{
			if(findViewById(R.id.gameviewbuttons).getVisibility() == View.GONE)
			{
				findViewById(R.id.gameviewbuttons).setVisibility(View.VISIBLE);
				findViewById(R.id.gameviewbuttons).startAnimation(panin);
				gameView.setPaused(true);	
			}
			else
			{
				findViewById(R.id.gameviewbuttons).startAnimation(panout);
				findViewById(R.id.gameviewbuttons).setVisibility(View.GONE);
				gameView.setPaused(false);	
			}
		}
		else
			nextInfoMessage();
	}
	
	@Override
	public void onRestoreInstanceState(Bundle bundle) {
		super.onRestoreInstanceState(bundle);
		if(bundle != null) {
			findViewById(R.id.gameviewbuttons).setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		sensorManager.unregisterListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_GAME);
	}
	
	@Override
	public void onClick(View arg0)
	{
		switch(arg0.getId())
		{
		case R.id.gameviewbuttonresume:
			gameView.setPaused(false);
			findViewById(R.id.gameviewbuttons).startAnimation(panout);
			findViewById(R.id.gameviewbuttons).setVisibility(View.INVISIBLE);
			break;
		case R.id.gameviewbuttonquit:
			findViewById(R.id.gameviewbuttons).startAnimation(panout);
			findViewById(R.id.gameviewbuttons).setVisibility(View.INVISIBLE);
			
			gameView.stop();
			finish();
			break;
		case R.id.gameviewinfoboxpic:
			nextInfoMessage();
			break;
		case R.id.gameviewinfoboxtext:
			nextInfoMessage();
			break;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		gameView.onAccuracyChanged(sensor, accuracy);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		gameView.onSensorChanged(event);
	}
	
	@Override
	public Dialog onCreateDialog(int id, Bundle args) {
		switch(id) {
		case DIALOG_LEVELCOMPLETE:
			return new LevelCompletedDialog(this, (LevelSummary) args.getParcelable("summary"), gvHandler);
		}
		return null;
	}
}
