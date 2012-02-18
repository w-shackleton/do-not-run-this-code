package uk.digitalsquid.spacegame;

import uk.digitalsquid.spacegame.levels.LevelManager.LevelExtendedInfo;
import uk.digitalsquid.spacegame.views.MainMenuLayout;
import uk.digitalsquid.spacegame.views.SplashScreen;
import uk.digitalsquid.spacegamelib.Constants;
import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Spacegame extends Activity implements Constants
{
	protected SplashScreen splash = null;
	protected MainMenuLayout mainmenu = null;
	
	protected LinearLayout linearlayout;
	
	protected LevelExtendedInfo levelToLoad;
	
	protected String currentLevelset;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // UI thread initialisy stuff
        BounceVibrate.initialise(getApplicationContext());
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
        linearlayout = new LinearLayout(this);
        
        setContentView(linearlayout);
    	activateView(Views.LOADING);
        
        Log.v(TAG, "Started");
    }

	public static final int MESSAGE_FINISHED_LOADING = 2;
	public static final int MESSAGE_QUIT = 3;
	public static final int MESSAGE_RESET_GAME = 9;
	
	private final Handler msgHandler = new Handler()
	{
		@Override
		public void handleMessage(Message m)
		{
			super.handleMessage(m);
			switch(m.what)
			{
			case MESSAGE_FINISHED_LOADING:
				activateView(Views.MAIN_MENU);
				break;
			case MESSAGE_QUIT:
				finish();
				break;
			case MESSAGE_RESET_GAME:
				activateView(Views.LOADING);
				break;
			}
		}
	};
	
	protected static enum Views
	{
		LOADING,
		MAIN_MENU,
	}
	
	Views currView;
	
	protected void activateView(Views view)
	{
		currView = view;
		
		linearlayout.removeAllViews();
		splash = null; // GC
		mainmenu = null; // GC
		switch(view)
		{
		case LOADING:
			splash = new SplashScreen(getApplicationContext(), null, msgHandler, (App)getApplication());
			linearlayout.addView(splash);
			break;
		case MAIN_MENU:
			mainmenu = new MainMenuLayout(getApplicationContext(), null, msgHandler, (App)getApplication());
			linearlayout.addView(mainmenu);
			break;
		}
	}
	
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(mainmenu != null)
            	mainmenu.onBackPress();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

}