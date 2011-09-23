package uk.digitalsquid.spacegame;

import java.io.IOException;

import uk.digitalsquid.spacegame.levels.LevelItem.LevelSummary;
import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegame.levels.LevelManager.LevelExtendedInfo;
import uk.digitalsquid.spacegame.views.GameViewLayout;
import uk.digitalsquid.spacegame.views.LevelSelectLayout;
import uk.digitalsquid.spacegame.views.LevelSetSelectLayout;
import uk.digitalsquid.spacegame.views.MainMenuLayout;
import uk.digitalsquid.spacegame.views.SplashScreen;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable.WarpData;
import android.app.Activity;
import android.app.Dialog;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Spacegame extends Activity
{
	protected GameViewLayout sview = null;
	protected SplashScreen splash = null;
	protected MainMenuLayout mainmenu = null;
	protected LevelSetSelectLayout levelsetselect = null;
	protected LevelSelectLayout levelselect = null;
	
	protected LinearLayout linearlayout;
	
	protected LevelExtendedInfo levelToLoad;
	
	protected LevelManager lmanager;
	
	private LevelSummary levelCompletedSummary;
	
	protected String currentLevelset;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        // UI thread initialisy stuff
        BounceVibrate.initialise(getApplicationContext());
        
        lmanager = new LevelManager(getApplicationContext());
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
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
			case MESSAGE_MAIN_MENU_FINISHED:
				Log.i("SpaceGame", "Starting game...");
				activateView(Views.MENU_LEVELSET_SELECT);
				break;
			case MESSAGE_QUIT:
				finish();
				break;
			case MESSAGE_END_LEVEL:
				switch(m.arg1) {
				case WarpData.END_FAIL:
				case WarpData.END_QUIT:
					activateView(Views.MENU_LEVEL_SELECT);
					break;
				case WarpData.END_SUCCESS:
					levelCompletedSummary = (LevelSummary) m.obj;
					lmanager.setLevelTime(levelToLoad, levelCompletedSummary.timeTaken);
					showDialog(DIALOG_LEVEL_COMPLETED);
					break;
				}
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
				levelToLoad = (LevelExtendedInfo) m.obj;
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
			try {
				sview = new GameViewLayout(getApplicationContext(), null, lmanager.getLevelIStream(levelToLoad), msgHandler);
			} catch (IOException e) {
				Toast.makeText(this, "Error loading level", Toast.LENGTH_LONG);
				activateView(Views.MENU_LEVEL_SELECT);
			}
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
    
    private static final int DIALOG_LEVEL_COMPLETED = 1;
    
    static final int LEVELCOMPLETED_CONTINUE = 1;
    static final int LEVELCOMPLETED_TOMENU = 2;
    static final int LEVELCOMPLETED_RETRY = 3;
    
    private final Handler levelCompletedDialogHandler = new Handler() {
    	
		@Override
		public void handleMessage(Message m) {
			switch(m.what) {
			case LEVELCOMPLETED_CONTINUE:
				break;
			case LEVELCOMPLETED_TOMENU:
				activateView(Views.MENU_LEVEL_SELECT);
				break;
			case LEVELCOMPLETED_RETRY:
				activateView(Views.GAME);
				break;
			}
		}
    };
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	super.onCreateDialog(id);
    	switch(id) {
    	case DIALOG_LEVEL_COMPLETED:
    		LevelCompletedDialog d = new LevelCompletedDialog(this, levelCompletedSummary, levelCompletedDialogHandler);
    		return d;
		default:
			return null;
    	}
    }
}