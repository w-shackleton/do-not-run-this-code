package uk.digitalsquid.spacegame.views;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.Spacegame;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.levels.LevelManager;
import uk.digitalsquid.spacegame.levels.SaxInfoLoader;
import uk.digitalsquid.spacegame.levels.SaxLoader;
import uk.digitalsquid.spacegame.subviews.ThreadedView;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;

public class SplashScreen extends ThreadedView<SplashScreen.ViewThread>
{
	Handler parentHandler;
	
	public SplashScreen(Context context, AttributeSet attrib, Handler parentHandler, LevelManager lmanager)
	{
		super(context, attrib);
		
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        this.parentHandler = parentHandler;
        
        if (isInEditMode() == false)
        {
        	thread = new ViewThread(context, holder, new Handler()
        	{
        		@Override
        		public void handleMessage(Message m)
        		{
        			super.handleMessage(m);
        			if(m.what == ViewThread.MESSAGE_FINISHED)
        			{
        				Message newM = new Message();
        				newM.what = Spacegame.MESSAGE_FINISHED_LOADING;
        				SplashScreen.this.parentHandler.sendMessage(newM);
        			}
        		}
        	}, lmanager);
        }
	}
	
	static class ViewThread extends ThreadedView.ViewThread
	{
		public ViewThread(Context context, SurfaceHolder surface, Handler handler, LevelManager lmanager)
		{
			super(context, surface);
			msgHandler = handler;
			loader = new Loader(context, lmanager);
		}

		static final int MESSAGE_FINISHED = 1;
		private Handler msgHandler;

		private float WORLD_ZOOM;
		@SuppressWarnings("unused")
		private int scaledWidth, scaledHeight;

		protected static final PaintDesc bgPaint = new PaintDesc(0, 0, 0);
		protected static final PaintDesc txtPaint = new PaintDesc(255, 255, 255, 255, 1, 12);
		
		int timeThroughLoop = 0;
		
		private Drawable splashLogo;
		private static final int SPLASH_LOGO_WIDTH = 160;
		private static final int SPLASH_LOGO_HEIGHT = 120;
		private int splashLogoOpacity = 0;
		private int splashLogoWantedOpacity = 0;
		
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
				splashLogoWantedOpacity = 200;
			if(timeThroughLoop == 50)
				splashLogoWantedOpacity = 0;
			if(timeThroughLoop == 70)
				setRunning(false);

			if(splashLogoOpacity < splashLogoWantedOpacity)
				splashLogoOpacity+=14;
			if(splashLogoOpacity > splashLogoWantedOpacity)
				splashLogoOpacity-=14;
		}
		
		@Override
		protected void postcalculate()
		{
			WORLD_ZOOM = (float) width / (float) REQ_SIZE_X;

			scaledHeight = (int) (height / WORLD_ZOOM);
			scaledWidth = (int) (width / WORLD_ZOOM);
		}
		
		@Override
		protected void predraw(Canvas c)
		{
			c.drawPaint(PaintLoader.load(bgPaint));
		}

		@Override
		protected void scale(Canvas c)
		{
			c.scale(WORLD_ZOOM, WORLD_ZOOM);
		};
		
		@Override
		protected void draw(Canvas c)
		{
			splashLogo.setBounds(new Rect(
					(scaledWidth / 2) - (SPLASH_LOGO_WIDTH / 2),
					120 - (SPLASH_LOGO_HEIGHT / 2),
					(scaledWidth / 2) + (SPLASH_LOGO_WIDTH / 2),
					120  + (SPLASH_LOGO_HEIGHT / 2)
					));
			splashLogo.setAlpha(splashLogoOpacity);
			splashLogo.draw(c);
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
				synchronized(context) // Main loading point of application
				{
					Log.i("SpaceGame", "Initialising various game components...");
					BounceVibrate.initialise(context);
					StaticInfo.initialise(context);
					Log.i("SpaceGame", "Loading XML parser...");
					SaxLoader.initialise();
					SaxInfoLoader.initialise();
					Log.i("SpaceGame", "Loading Level Loaders...");
					lmanager.initialise();
					Log.i("SpaceGame", "Loaded.");
				}
			}
		}

		@Override
		protected void initialiseOnThread()
		{
			loader.start();
			splashLogo = context.getResources().getDrawable(R.drawable.splash);
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

		@Override
		protected void postdrawscale(Canvas c) {
			c.scale(1, 1);
		}
	}
}
