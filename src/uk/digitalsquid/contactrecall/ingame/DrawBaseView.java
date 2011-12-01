package uk.digitalsquid.contactrecall.ingame;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.ingame.gl.TextureManager;
import uk.digitalsquid.contactrecall.misc.Config;
import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public abstract class DrawBaseView<VT extends DrawBaseView.ViewWorker> extends GLSurfaceView implements OnTouchListener, Config
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
	    if(DEBUG) setDebugFlags(DEBUG_CHECK_GL_ERROR);
		this.context = context;
		
		setOnTouchListener(this);
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
		
		protected static final float REQ_SIZE_Y = 48f;
		protected static final float REQ_SIZE_X = 32f;
		protected float scaledWidth;
		protected float scaledHeight;
		
		protected boolean landscape = false;
		
		public ViewWorker(Context context)
		{
			super();
			this.context = context;
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			initialiseOnThread();
			TextureManager.init(context, gl);
		}
		
		final Matrix matrix2d = new Matrix();
		final Matrix matrixInverse = new Matrix();
		
		protected boolean firstFrame = true;
		
		@Override
		public void onDrawFrame(GL10 gl) {
			
			// clear Screen and Depth Buffer
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	
			// Reset the Modelview Matrix
			gl.glLoadIdentity();
	
			// Drawing
			gl.glTranslatef(0.0f, 0.0f, -scaledHeight / 2 / (float)Math.tan(Math.PI / 8)); // Screen is now 320x???px big, where ??? is about 480px
			// gl.glTranslatef(0.0f, 0.0f, -5f);
			
			gl.glPushMatrix(); // As to stop this matrix being affected
			
			drawGL(gl);
			
			gl.glPopMatrix();
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			if(height == 0) {
				height = 1;
			}
			this.width = width;
			this.height = height;
			
			gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
			gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
			gl.glLoadIdentity(); 					//Reset The Projection Matrix
	
			landscape = width > height;
			GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 5f, 200.0f);
			if(landscape) {
				scaledWidth = REQ_SIZE_Y;
				scaledHeight = (REQ_SIZE_Y * height) / width;
			} else {
				scaledHeight = REQ_SIZE_Y;
				scaledWidth = (REQ_SIZE_Y * width) / height;
			}
	
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			
			gl.glEnable(GL10.GL_LINE_SMOOTH);
			// gl.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			
			onSizeChanged(scaledWidth, scaledHeight);
		}

		protected abstract void initialiseOnThread();
		
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
		
		protected abstract void scale(GL10 c);
		
		public abstract void saveState(Bundle bundle);
		public abstract void restoreState(Bundle bundle);
		
		/**
		 * When the size changes.
		 * @param width Width & height in scaled units.
		 * @param height
		 */
		protected void onSizeChanged(float width, float height) {}
		
		protected abstract void onTouchDown(float x, float y);
		protected abstract void onTouchMove(float x, float y);
		protected abstract void onTouchUp(float x, float y);
	}
	
	public void saveState(Bundle bundle) {
		if(thread != null) thread.saveState(bundle);
	}
	
	public void restoreState(Bundle bundle) {
		if(thread != null && bundle != null) {
			thread.restoreState(bundle);
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch(event.getActionIndex()) {
		case 0: // Currently only supports 1 pointer
			if(thread == null) break;
			final float[] touchPoints = new float[2];
			touchPoints[0] = event.getX();
			touchPoints[1] = event.getY();
			thread.matrix2d.mapPoints(touchPoints);
			switch(event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				queueEvent(new Runnable() {
					@Override public void run() {
						thread.onTouchDown(touchPoints[0], touchPoints[1]);
					}
				});
				break;
			case MotionEvent.ACTION_MOVE:
				queueEvent(new Runnable() {
					@Override public void run() {
						thread.onTouchMove(touchPoints[0], touchPoints[1]);
					}
				});
				break;
			case MotionEvent.ACTION_UP:
				queueEvent(new Runnable() {
					@Override public void run() {
						thread.onTouchUp(touchPoints[0], touchPoints[1]);
					}
				});
				break;
			}
			break;
		}
		return true;
	}
}
