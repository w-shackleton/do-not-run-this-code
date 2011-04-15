package uk.digitalsquid.spacegame;

import java.io.InputStream;

import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegame.views.GameViewLayout;
import uk.digitalsquid.spacegame.views.LevelSelectLayout;
import uk.digitalsquid.spacegame.views.LevelSetSelectLayout;
import uk.digitalsquid.spacegame.views.MainMenuLayout;
import uk.digitalsquid.spacegame.views.SplashScreen;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Spacegame extends Activity
{
	protected GameViewLayout sview = null;
	protected SplashScreen splash = null;
	protected MainMenuLayout mainmenu = null;
	protected LevelSetSelectLayout levelsetselect = null;
	protected LevelSelectLayout levelselect = null;
	
	protected LinearLayout linearlayout;
	
	protected InputStream levelToLoad;
	
	protected LevelManager lmanager;
	
	protected String currentLevelset;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        lmanager = new LevelManager(getBaseContext());
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        linearlayout = new LinearLayout(this);
        
        setContentView(linearlayout);
        if(savedInstanceState != null)
        {
        	if(savedInstanceState.containsKey("currView"))
        		activateView((Views) savedInstanceState.getSerializable("currView"));
        	else
        		activateView(Views.LOADING); // Start of game
        }
        else
        	activateView(Views.LOADING);
        
        if(sview != null)
        	sview.restoreState(savedInstanceState);
        
        Log.v("SpaceGame", "Started");
    }

	public static final int MESSAGE_END_LEVEL = 1;
	public static final int MESSAGE_FINISHED_LOADING = 2;
	public static final int MESSAGE_QUIT = 3;
	public static final int MESSAGE_MAIN_MENU_FINISHED = 4;
	public static final int MESSAGE_RETURN_TO_MAIN_SCREEN = 5;
	public static final int MESSAGE_RETURN_TO_LEVELSET_SELECT = 6;
	public static final int MESSAGE_OPEN_LEVELSET = 7;
	public static final int MESSAGE_START_LEVEL = 8;
	public static final int MESSAGE_RESET_GAME = 9;
	
	protected Handler msgHandler = new Handler()
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
			case MESSAGE_MAIN_MENU_FINISHED:
				Log.i("SpaceGame", "Starting game...");
//				levelToLoad = getResources().openRawResource(R.raw.level1);
				activateView(Views.MENU_LEVELSET_SELECT);
				break;
			case MESSAGE_QUIT:
				finish();
				break;
			case MESSAGE_END_LEVEL:
				activateView(Views.MENU_LEVEL_SELECT); // Needs to point somewhere else...
				break;
			case MESSAGE_RETURN_TO_MAIN_SCREEN:
				activateView(Views.MAIN_MENU);
				break;
			case MESSAGE_RETURN_TO_LEVELSET_SELECT:
				activateView(Views.MENU_LEVELSET_SELECT);
				break;
			case MESSAGE_OPEN_LEVELSET:
				currentLevelset = (String) m.obj;
				activateView(Views.MENU_LEVEL_SELECT);
				break;
			case MESSAGE_START_LEVEL:
				levelToLoad = (InputStream) m.obj;
				activateView(Views.GAME);
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
		MENU_LEVELSET_SELECT,
		MENU_LEVEL_SELECT,
		GAME
	}
	
	Views currView;
	
	protected void activateView(Views view)
	{
		currView = view;
		
		linearlayout.removeAllViews();
		splash = null; // GC
		sview = null; // GC
		mainmenu = null; // GC
		levelsetselect = null; // GC
		levelselect = null;
		switch(view)
		{
		case LOADING:
			splash = new SplashScreen(getApplicationContext(), null, msgHandler, lmanager);
			linearlayout.addView(splash);
			break;
		case MAIN_MENU:
			mainmenu = new MainMenuLayout(getApplicationContext(), null, msgHandler, lmanager);
			linearlayout.addView(mainmenu);
			break;
		case GAME:
			sview = new GameViewLayout(getApplicationContext(), null, levelToLoad, msgHandler);
			linearlayout.addView(sview);
			break;
		case MENU_LEVELSET_SELECT:
			levelsetselect = new LevelSetSelectLayout(getApplicationContext(), null, msgHandler);
			linearlayout.addView(levelsetselect);
			break;
		case MENU_LEVEL_SELECT:
			levelselect = new LevelSelectLayout(getApplicationContext(), null, msgHandler, lmanager, currentLevelset);
			linearlayout.addView(levelselect);
			break;
		}
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event)
	{
		if(sview != null)
			sview.onTrackballEvent(event);
//        if(mainmenu != null)
//        	mainmenu.onTrackballEvent(event);
		return super.onTrackballEvent(event);
	}

    /**
     * Notification that something is about to happen, to give the Activity a
     * chance to save state.
     * 
     * @param outState a Bundle into which this Activity should save its state
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if(sview != null)
        	sview.saveState(outState);
        outState.putSerializable("currView", currView);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if(keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		if(sview != null)
    			sview.onBackPress();
            if(mainmenu != null)
            	mainmenu.onBackPress();
            if(levelsetselect != null)
            	levelsetselect.onBackPress();
            if(levelselect != null)
            	levelselect.onBackPress();
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
}