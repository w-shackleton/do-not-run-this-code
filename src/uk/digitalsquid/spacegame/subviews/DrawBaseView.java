package uk.digitalsquid.spacegame.subviews;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.gl.TextureManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.AttributeSet;

public abstract class DrawBaseView<VT extends DrawBaseView.ViewWorker> extends GLSurfaceView
{
	protected final Context context;
	protected VT thread;
	
	/**
	 * Constructs a new {@link DrawBaseView}. Non-abstract extended classes must initialise a renderer
	 * with a subclass of {@link ViewWorker}. 
	 */
	public DrawBaseView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// setEGLConfigChooser(8, 8, 8, 8, 0, 0);
	    // getHolder().setFormat(PixelFormat.RGBA_8888);
	    // setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);
	    setDebugFlags(DEBUG_CHECK_GL_ERROR);
		this.context = context;
	}
	
	protected final void initP2() {
		thread = createThread();
		if(thread == null) throw new IllegalStateException("thread not initialised!");
		setRenderer(thread);
	}
	
	/**
	 * Creates the thread
	 * @return A new VT thread
	 */
	protected abstract VT createThread();
	
	public static abstract class ViewWorker implements GLSurfaceView.Renderer
	{
		protected Context context;
		
		protected long currTime, prevTime, millistep = 17;
		
		protected static final int REQ_SIZE_Y = 320; // & 480 - scale to middle-screen size.
		protected int scaledWidth = 480;
		protected int scaledHeight = REQ_SIZE_Y;
		
		public ViewWorker(Context context)
		{
			super();
			this.context = context;
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			initialiseOnThread();
		}
		
		protected boolean firstFrame = true;
		
		@Override
		public void onDrawFrame(GL10 gl) {
			if(firstFrame) {
				TextureManager.init(context, gl); // Here because derp derpderp...
			}
			
			// clear Screen and Depth Buffer
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	
			// Reset the Modelview Matrix
			gl.glLoadIdentity();
	
			// Drawing
			gl.glTranslatef(0.0f, 0.0f, -REQ_SIZE_Y / 2); // Screen is now 320x???px big, where ??? is about 480px
			// gl.glTranslatef(0.0f, 0.0f, -5f);
			
			gl.glPushMatrix(); // As to stop this matrix being affected
			
			drawGL(gl);
			
			gl.glPopMatrix();
			
			currTime = System.currentTimeMillis();
			if(firstFrame) {
				prevTime = System.currentTimeMillis() - 17; // Estimate
				firstFrame = false;
			}
			
			millistep = currTime - prevTime;
			millistep = (long) (1000f / 60);
			
			prevTime = currTime;
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			if(height == 0) { 						//Prevent A Divide By Zero By
				height = 1; 						//Making Height Equal One
			}
			this.width = width;
			this.height = height;
			
			gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
			gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
			gl.glLoadIdentity(); 					//Reset The Projection Matrix
	
			//Calculate The Aspect Ratio Of The Window
			GLU.gluPerspective(gl, 90.0f, (float)width / (float)height, 50f, 1000.0f);
			scaledWidth = (REQ_SIZE_Y * width) / height;
	
			gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
			gl.glLoadIdentity(); 					//Reset The Modelview Matrix
			
			gl.glEnable(GL10.GL_LINE_SMOOTH);
			// gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST); // no visible diff
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			
			onSizeChanged(width, height);
		}

		protected abstract void initialiseOnThread();
		
		protected boolean running = true;
		
		public synchronized void setRunning(boolean run) {
			if(running && !run) // One time stop
				onThreadEnd();
			running = run;
		}
		
		/**
		 * Called on this thread when the thread is about to end, so that any final calls can be made etc.
		 */
		protected abstract void onThreadEnd();
		
		protected int width, height;
		
		private void drawGL(GL10 gl)
		{
			precalculate();
			calculate();
			postcalculate();
			
			gl.glPushMatrix();
			
			predraw(gl);
			scale(gl);
			
			draw(gl);
			
			gl.glPopMatrix();
			
			// gl.glPushMatrix();
			postdraw(gl);
			// gl.glPopMatrix();
		}
		
		protected void precalculate(){}
		protected void calculate(){}
		protected void postcalculate(){}
		
		protected void predraw(GL10 c){}
		protected void draw(GL10 c){}
		protected void postdraw(GL10 c){}
		
		protected void afterdraw(){}
		
		protected abstract void scale(GL10 c);
		
		public abstract void saveState(Bundle bundle);
		public abstract void restoreState(Bundle bundle);
		
		protected void onSizeChanged(int w, int h) {}
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
