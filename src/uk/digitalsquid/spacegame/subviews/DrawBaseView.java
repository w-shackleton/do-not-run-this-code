package uk.digitalsquid.spacegame.subviews;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public abstract class DrawBaseView<VT extends DrawBaseView.Renderer> extends GLSurfaceView
{
	protected VT thread = null;
	
	/**
	 * Constructs a new {@link DrawBaseView}. Non-abstract extended classes must initialise a renderer
	 * with a subclass of {@link Renderer}. 
	 */
	public DrawBaseView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public static abstract class Renderer implements GLSurfaceView.Renderer
	{
		protected Context context;
		protected SurfaceHolder surface;
		
		protected static final int MAX_FPS = 30; // 40 is goodish.
		protected static final int MAX_MILLIS = (int) (1000f / (float)MAX_FPS);

		protected long currTime, prevTime, millistep;
		
		protected float WORLD_ZOOM_UNSCALED;
		protected static final int REQ_SIZE_X = 480; // & 320 - scale to middle-screen size.
		
		public Renderer(Context context, SurfaceHolder surface)
		{
			super();
			this.context = context;
			this.surface = surface;
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			initialiseOnThread();
		}
		
		@Override
		public void onDrawFrame(GL10 gl) {
			drawGL(gl);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			this.width = width;
			this.height = height;
		}

		protected abstract void initialiseOnThread();
		
		protected boolean running = true;
		
		public synchronized void setRunning(boolean run) {
			running = run;
		}
		
		/**
		 * Called on this thread when the thread is about to end, so that any final calls can be made etc.
		 */
		protected abstract void onThreadEnd();
		
		protected int width, height;
		
		private void drawGL(GL10 gl)
		{
			prevTime = System.currentTimeMillis();
			
			precalculate();
			calculate();
			postcalculate();
			
			scaleCalculate();
			
			predraw(gl);
			
			scale(gl);
			
			draw(gl);
			
			gl.glLoadIdentity();
			
			postdrawscale(gl);
			postdraw(gl);
			gl.glLoadIdentity();
			
			// SLEEPY TIME!!
			currTime = System.currentTimeMillis();

			// Update millistep for next draw loop
			millistep = System.currentTimeMillis() - prevTime;
		}
		
		protected void scaleCalculate(){}
		protected void precalculate(){}
		protected void calculate(){}
		protected void postcalculate()
		{
			WORLD_ZOOM_UNSCALED = (float)width / REQ_SIZE_X;
		}
		
		protected void predraw(GL10 c){}
		protected void draw(GL10 c){}
		protected abstract void postdrawscale(GL10 c);
		protected void postdraw(GL10 c){}
		
		protected void afterdraw(){}
		
		protected abstract void scale(GL10 c);
		
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
