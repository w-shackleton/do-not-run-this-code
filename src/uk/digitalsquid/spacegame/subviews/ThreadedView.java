package uk.digitalsquid.spacegame.subviews;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public abstract class ThreadedView<VT extends ThreadedView.ViewThread> extends SurfaceView implements SurfaceHolder.Callback
{
	protected VT thread = null;
	protected SurfaceHolder holder;
	
	/**
	 * Constructs a new ThreadedView. Non-abstract extended classes must initialise {@link #thread}
	 * with a subclass of {@link ViewThread}. 
	 */
	public ThreadedView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
        holder = getHolder();
        holder.addCallback(this);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
        boolean retry = true;
        thread.setRunning(false);
        while (retry) {
            try
            {
                thread.join();
                retry = false;
            }
            catch(InterruptedException e)
            {
            	
            }
        }
	}
	
	public static abstract class ViewThread extends Thread
	{
		protected Context context;
		protected SurfaceHolder surface;
		
		protected static final int MAX_FPS = 30; // 40 is goodish.
		protected static final int MAX_MILLIS = (int) (1000f / (float)MAX_FPS);

		protected long currTime, prevTime, millistep;
		
		protected float WORLD_ZOOM_UNSCALED;
		protected static final int REQ_SIZE_X = 480; // & 320 - scale to middle-screen size.
		
		public ViewThread(Context context, SurfaceHolder surface)
		{
			super();
			this.context = context;
			this.surface = surface;
		}
		
		protected abstract void initialiseOnThread();
		
		@Override
		public void run()
		{
			super.run();
			// synchronized(context)
			{
				initialiseOnThread();
			}
			
			while(running)
			{
				Canvas c = null;
				try
				{
					c = surface.lockCanvas(null);
					synchronized(surface)
					{
						drawCanvas(c);
					}
				} catch (Exception e) {
					// Toast.makeText(context, "An error occurred somewhere, please check log for details!", Toast.LENGTH_LONG);
					Log.e("SpaceGame", "An error occurred in the main game loop: " + e.getMessage() + ".", e);
					break;
				}
				finally
				{
					if(c != null)
						surface.unlockCanvasAndPost(c);
				}
			}
			onThreadEnd();
		}
		
		protected boolean running = true;
		
		public synchronized void setRunning(boolean run)
		{
			running = run;
		}
		
		/**
		 * Called on this thread when the thread is about to end, so that any final calls can be made etc.
		 */
		protected abstract void onThreadEnd();
		
		protected int width, height;
		
		private void drawCanvas(Canvas c)
		{
			prevTime = System.currentTimeMillis();
			width = c.getWidth();
			height = c.getHeight();
			
			precalculate();
			calculate();
			postcalculate();
			
			scaleCalculate();
			
			predraw(c);
			
			scale(c);
			
			draw(c);
			
			c.restore();
			
			postdraw(c);
			
			// SLEEPY TIME!!
			currTime = System.currentTimeMillis();

			if(MAX_MILLIS - currTime + prevTime > 0)
			{
				//Log.v("SpaceGame", "Spare time, sleeping for " + (MAX_MILLIS - currTime + prevTime));
				try {
					//Log.d("SpaceGame", "MSleep: " + (MAX_MILLIS - currTime + prevTime) + ", Millis: " + (currTime - prevTime));
				Thread.sleep(MAX_MILLIS - currTime + prevTime);
				} catch (InterruptedException e) {
					Log.v("SpaceGame", "Sleep Interrupted! (Probably closing...)");
				}
			}
			// Update millistep for next draw loop
			//millistep = System.currentTimeMillis() - prevTime;
			millistep = MAX_MILLIS; // Set here to force a frame-rate
		}
		
		protected void scaleCalculate(){}
		protected void precalculate(){}
		protected void calculate(){}
		protected void postcalculate()
		{
			WORLD_ZOOM_UNSCALED = (float)width / REQ_SIZE_X;
		}
		
		protected void predraw(Canvas c){}
		protected void draw(Canvas c){}
		protected void postdraw(Canvas c){}
		
		protected abstract void scale(Canvas c);
		
		public abstract void saveState(Bundle bundle);
		public abstract void restoreState(Bundle bundle);
	}
	
	public void saveState(Bundle bundle)
	{
		thread.saveState(bundle);
	}
	
	public void restoreState(Bundle bundle)
	{
		if(bundle != null)
		{
			thread.restoreState(bundle);
		}
	}
}
