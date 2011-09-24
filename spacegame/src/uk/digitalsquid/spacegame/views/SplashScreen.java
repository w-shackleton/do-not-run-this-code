package uk.digitalsquid.spacegame.views;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegame.levels.SaxInfoLoader;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.spaceitem.assistors.SoundManager;
import uk.digitalsquid.spacegame.subviews.DrawBaseView;
import uk.digitalsquid.spacegamelib.StaticInfo;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

public class SplashScreen extends DrawBaseView<SplashScreen.ViewWorker>
{
	Handler parentHandler;
	
	private final LevelManager lmanager;
	
	public SplashScreen(Context context, AttributeSet attrib, Handler parentHandler, LevelManager lmanager)
	{
		super(context, attrib);
		
		this.lmanager = lmanager;
        this.parentHandler = parentHandler;
        
        initP2();
	}
	
	@Override
	protected ViewWorker createThread() {
    	return new ViewWorker(context, new Handler()
    	{
    		@Override
    		public void handleMessage(Message m)
    		{
    			super.handleMessage(m);
    			if(m.what == ViewWorker.MESSAGE_FINISHED)
    			{
    				Message newM = new Message();
    				newM.what = Spacegame.MESSAGE_FINISHED_LOADING;
    				SplashScreen.this.parentHandler.sendMessage(newM);
    			}
    		}
    	}, lmanager);
	}
	
	static class ViewWorker extends DrawBaseView.ViewWorker
	{
		public ViewWorker(Context context, Handler handler, LevelManager lmanager)
		{
			super(context);
			msgHandler = handler;
			loader = new Loader(context, lmanager);
		}

		static final int MESSAGE_FINISHED = 1;
		private Handler msgHandler;

		int timeThroughLoop = 0;
		
		private RectMesh splashLogo;
		private static final int SPLASH_LOGO_WIDTH = 20;
		private static final int SPLASH_LOGO_HEIGHT = 10;
		private float splashLogoOpacity = 0;
		private float splashLogoWantedOpacity = 0;
		
		@Override
		protected void precalculate()
		{
			super.precalculate();
			timeThroughLoop++;
		}
		
		@Override
		protected void calculate()
		{
			// Set fade in&out and exit points
			if(timeThroughLoop == 10)
				splashLogoWantedOpacity = 1;
			if(timeThroughLoop == 50)
				splashLogoWantedOpacity = 0;
			if(timeThroughLoop == 70)
				setRunning(false);

			if(splashLogoOpacity < splashLogoWantedOpacity)
				splashLogoOpacity+=14f / 256;
			if(splashLogoOpacity > splashLogoWantedOpacity)
				splashLogoOpacity-=14f / 256;
			
			splashLogo.setAlpha(splashLogoOpacity);
		}
		
		@Override
		protected void predraw(GL10 gl)
		{
		}

		@Override
		protected void scale(GL10 gl) {}
		
		@Override
		protected void draw(GL10 gl)
		{
			splashLogo.draw(gl);
		}
		
		private Loader loader;
		
		private class Loader extends Thread
		{
			private Context context;
			private LevelManager lmanager;
			
			protected Loader(Context c, LevelManager lmanager)
			{
				this.context = c;
				this.lmanager = lmanager;
			}
			
			@Override
			public void run()
			{
				Log.i("SpaceGame", "Initialising various game components...");
				// BounceVibrate.initialise(context);
				StaticInfo.initialise(context);
				Log.i("SpaceGame", "Loading XML parser...");
				SaxLoader.initialise();
				SaxInfoLoader.initialise();
				Log.i("SpaceGame", "Loading Level Loaders...");
				lmanager.initialise();
				Log.i("SpaceGame", "Loading Sounds...");
				SoundManager.initialise(context);
				Log.i("SpaceGame", "Loaded.");
			}
		}

		@Override
		protected void initialiseOnThread()
		{
			loader.start();
			// splashLogo = new RectMesh(0, 0, SPLASH_LOGO_WIDTH, SPLASH_LOGO_HEIGHT, 1, 1, 0, 0);
			splashLogo = new RectMesh(0, 0, SPLASH_LOGO_WIDTH, SPLASH_LOGO_HEIGHT, R.drawable.splash);
		}

		@Override
		protected void onThreadEnd()
		{
			while(loader.isAlive()) // Wait until thread dies
			{
				try
				{
					Thread.sleep(50);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
			Message m = Message.obtain();
			m.what = MESSAGE_FINISHED;
			msgHandler.sendMessage(m);
		}

		@Override
		public void restoreState(Bundle bundle)
		{
			
		}

		@Override
		public void saveState(Bundle bundle)
		{
			
		}
	}
}
