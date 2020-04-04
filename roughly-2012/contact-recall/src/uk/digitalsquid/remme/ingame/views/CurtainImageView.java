package uk.digitalsquid.remme.ingame.views;

import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.misc.Config;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

/**
 * A view that displays a shutter / curtains in the form of an image
 * @author william
 *
 */
public class CurtainImageView extends SurfaceView implements SurfaceHolder.Callback, Config {

	private final Drawable drawable;
	
	private float progress;
	
	private SurfaceHolder holder;

	public CurtainImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Get attributes
		TypedArray customAttrs = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.CurtainImageView,
				0, 0);
		drawable = customAttrs.getDrawable(R.styleable.CurtainImageView_src);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setFormat(PixelFormat.TRANSPARENT);
		if(!isInEditMode()) setZOrderOnTop(true);
	}

	public float getProgress() {
		return progress;
	}
	
	@Override
	protected void onDraw(Canvas c) {
		// To make this transparent in the editor
		if(!isInEditMode()) super.onDraw(c);
	}

	/**
	 * Set the animation progress, from 0 to 1
	 * @param progress
	 */
	public void setProgress(float progress) {
		this.progress = progress;
	}
	
	private Rect test = new Rect();
	private Rect bounds = new Rect();
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	
	AnimationTask task;

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		task = new AnimationTask(holder, getContext(), drawable);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		task.cancel(true);
	}
	
	private class AnimationTask extends AsyncTask<Void, Void, Boolean> {
		private final Drawable drawable;
		private final SurfaceHolder holder;
		
		public AnimationTask(SurfaceHolder holder, Context context, Drawable src) {
			this.drawable = src;
			this.holder = holder;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			while(!isCancelled()) {
				Canvas canvas = holder.lockCanvas();
				if(canvas != null) {
					doDraw(canvas);
					holder.unlockCanvasAndPost(canvas);
					if(displacement >= 1) return true;
				}
			}
			Log.d(TAG, "Loading screen render thread closing");
			return false;
		}
		
		private static final float k = 4;
		private static final float damping = 5f;
		private static final float mass = 0.1f;
		
		private float displacement = 0, velocity = 0;
		
		private long previousNanoTime;
		
		private void doDraw(Canvas c) {
			if(drawable == null) return;
			c.drawColor(0, Mode.CLEAR);
			c.save();
			final int height = c.getHeight(), width = c.getWidth();
			bounds.top = 0; bounds.left = 0;
			bounds.right = width; bounds.bottom = height;
			
			final long currentNanoTime = System.nanoTime();
			if(previousNanoTime == 0) previousNanoTime = currentNanoTime;
			
			final float dt = (float)(currentNanoTime - previousNanoTime) / (float)1000000000L;
			
			// Using a simple heavily damped spring model for the position.
			final float F = k * (progress - displacement) - mass * damping * velocity;
			final float acc = F / mass;
			velocity += acc * dt;
			displacement += velocity * dt;
			
			test.top = (int) (height / 2 * (1-displacement));
			test.bottom = height - test.top;
			test.left = (int) (width / 2 * (1-displacement));
			test.right = width - test.left;
			
			c.clipRect(bounds);
			c.clipRect(test, Op.DIFFERENCE);
			drawable.setBounds(bounds);
			drawable.draw(c);
				
			c.restore();
			
			previousNanoTime = System.nanoTime();
		}
		
		protected void onPostExecute(Boolean springFinished) {
			super.onPostExecute(springFinished);
			if(springFinished) CurtainImageView.this.setVisibility(View.INVISIBLE);
		}
	}
}
