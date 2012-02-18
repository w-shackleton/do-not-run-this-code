package uk.digitalsquid.spacegame.views;

import uk.digitalsquid.spacegame.App;
import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.LevelSetSelect;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegamelib.StaticInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

public class MainMenuLayout extends FrameLayout implements OnClickListener, OnCheckedChangeListener
{
	protected Context context;
	protected Handler parentHandler;
	protected SharedPreferences prefs;
	MainMenu menuView;
	protected LevelManager lmanager;
	
	public MainMenuLayout(Context context, AttributeSet attrs, Handler handler, App app)
	{
		super(context, attrs);
		this.lmanager = app.getLevelManager();
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		this.context = context;
		parentHandler = handler;
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.mainmenu, this);
		
		panout = AnimationUtils.loadAnimation(context, R.anim.panout);
		panin = AnimationUtils.loadAnimation(context, R.anim.panin);

		menuView = (MainMenu) findViewById(R.id.menuview);
		menuView.setLevel(context.getResources().openRawResource(R.raw.menu));
		menuView.setFocusable(false);
		menuView.setFocusableInTouchMode(false);
		menuView.create();

		findViewById(R.id.mainmenubuttonstart).setOnClickListener(this);
		findViewById(R.id.mainmenubuttonoptions).setOnClickListener(this);
		findViewById(R.id.mainmenubuttonquit).setOnClickListener(this);
		findViewById(R.id.mainmenuoptionsbuttonback).setOnClickListener(this);
		findViewById(R.id.mainmenuoptionsbuttonperformance).setOnClickListener(this);
		((ToggleButton)findViewById(R.id.mainmenuoptionsbuttonsound)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.mainmenuoptionsbuttonvibration)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.mainmenuoptionsperformancebuttonstarfield)).setOnCheckedChangeListener(this);
		((ToggleButton)findViewById(R.id.mainmenuoptionsperformancebuttonantialiasing)).setOnCheckedChangeListener(this);
		findViewById(R.id.mainmenuoptionsperformancebuttonback).setOnClickListener(this);
		findViewById(R.id.mainmenuoptionsperformancebuttonresetdb).setOnClickListener(this);
		
		// Set button start values
		((ToggleButton)findViewById(R.id.mainmenuoptionsbuttonsound)).setChecked(prefs.getBoolean("sound", true));
		((ToggleButton)findViewById(R.id.mainmenuoptionsbuttonvibration)).setChecked(prefs.getBoolean("vibrate", true));
		((ToggleButton)findViewById(R.id.mainmenuoptionsperformancebuttonantialiasing)).setChecked(prefs.getBoolean("antialiasing", true));
		((ToggleButton)findViewById(R.id.mainmenuoptionsperformancebuttonstarfield)).setChecked(prefs.getBoolean("starfield", true));
		
		Animation fadein = AnimationUtils.loadAnimation(context, R.anim.fadein);
		startAnimation(fadein);
		
		findViewById(R.id.mainmenubuttons).requestFocus();
	}
	
	Animation panout, panin;
	
	@Override
	public void onClick(View v)
	{
		Message m = Message.obtain();
		switch(v.getId())
		{
		case R.id.mainmenubuttonstart:
			// START
			menuView.stop();
			Intent intent = new Intent(context, LevelSetSelect.class);
			context.startActivity(intent);
			break;
		case R.id.mainmenubuttonoptions:
			findViewById(R.id.mainmenubuttons).startAnimation(panout);
			findViewById(R.id.mainmenubuttons).setVisibility(View.INVISIBLE);
			findViewById(R.id.mainmenuoptions).startAnimation(panin);
			findViewById(R.id.mainmenuoptions).setVisibility(View.VISIBLE);
			findViewById(R.id.mainmenuoptions).requestFocus();
			break;
		case R.id.mainmenubuttonquit:
			findViewById(R.id.mainmenubuttons).startAnimation(panout);
			findViewById(R.id.mainmenubuttons).setVisibility(View.INVISIBLE);
			m.what = Spacegame.MESSAGE_QUIT;
			parentHandler.sendMessageAtTime(m, SystemClock.uptimeMillis() + panout.getDuration());
			break;
		case R.id.mainmenuoptionsbuttonperformance:
			findViewById(R.id.mainmenuoptionsperformance).startAnimation(panin);
			findViewById(R.id.mainmenuoptionsperformance).setVisibility(View.VISIBLE);
			findViewById(R.id.mainmenuoptions).startAnimation(panout);
			findViewById(R.id.mainmenuoptions).setVisibility(View.INVISIBLE);
			findViewById(R.id.mainmenuoptionsperformance).requestFocus();
			break;
		case R.id.mainmenuoptionsbuttonback:
			findViewById(R.id.mainmenubuttons).startAnimation(panin);
			findViewById(R.id.mainmenubuttons).setVisibility(View.VISIBLE);
			findViewById(R.id.mainmenuoptions).startAnimation(panout);
			findViewById(R.id.mainmenuoptions).setVisibility(View.INVISIBLE);
			break;
		case R.id.mainmenuoptionsperformancebuttonback:
			findViewById(R.id.mainmenuoptionsperformance).startAnimation(panout);
			findViewById(R.id.mainmenuoptionsperformance).setVisibility(View.INVISIBLE);
			findViewById(R.id.mainmenuoptions).startAnimation(panin);
			findViewById(R.id.mainmenuoptions).setVisibility(View.VISIBLE);
			break;
		case R.id.mainmenuoptionsperformancebuttonresetdb:
			lmanager.resetDB();
			findViewById(R.id.mainmenuoptionsperformance).startAnimation(panout);
			findViewById(R.id.mainmenuoptionsperformance).setVisibility(View.INVISIBLE);
			menuView.stop();
			m.what = Spacegame.MESSAGE_RESET_GAME;
			parentHandler.sendMessageAtTime(m, SystemClock.uptimeMillis() + panout.getDuration());
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		switch(buttonView.getId())
		{
		case R.id.mainmenuoptionsbuttonsound:
			prefs.edit().putBoolean("sound", isChecked).commit();
			break;
		case R.id.mainmenuoptionsbuttonvibration:
			prefs.edit().putBoolean("vibrate", isChecked).commit();
			BounceVibrate.initialise(context);
			break;
		case R.id.mainmenuoptionsperformancebuttonantialiasing:
			prefs.edit().putBoolean("antialiasing", isChecked).commit();
			StaticInfo.initialise(context); // Reload value.
			break;
		case R.id.mainmenuoptionsperformancebuttonstarfield:
			prefs.edit().putBoolean("starfield", isChecked).commit();
			StaticInfo.initialise(context); // Reload value.
			break;
		}
	}
	
	public void onBackPress() {
		if(findViewById(R.id.mainmenubuttons).getVisibility() == View.VISIBLE)
		{
			Message msg = Message.obtain();
			findViewById(R.id.mainmenubuttons).startAnimation(panout);
			findViewById(R.id.mainmenubuttons).setVisibility(View.INVISIBLE);
			msg.what = Spacegame.MESSAGE_QUIT;
			parentHandler.sendMessageAtTime(msg, SystemClock.uptimeMillis() + panout.getDuration());
		}
		if(findViewById(R.id.mainmenuoptions).getVisibility() == View.VISIBLE)
		{
			findViewById(R.id.mainmenubuttons).startAnimation(panin);
			findViewById(R.id.mainmenubuttons).setVisibility(View.VISIBLE);
			findViewById(R.id.mainmenuoptions).startAnimation(panout);
			findViewById(R.id.mainmenuoptions).setVisibility(View.INVISIBLE);
		}
		if(findViewById(R.id.mainmenuoptionsperformance).getVisibility() == View.VISIBLE)
		{
			findViewById(R.id.mainmenuoptions).startAnimation(panin);
			findViewById(R.id.mainmenuoptions).setVisibility(View.VISIBLE);
			findViewById(R.id.mainmenuoptionsperformance).startAnimation(panout);
			findViewById(R.id.mainmenuoptionsperformance).setVisibility(View.INVISIBLE);
		}
	}
}
