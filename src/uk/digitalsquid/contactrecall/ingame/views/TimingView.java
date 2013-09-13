package uk.digitalsquid.contactrecall.ingame.views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

/**
 * A view that has functionality to time something and show a progress
 * to the user
 * @author william
 *
 */
public abstract class TimingView extends View {

	private float totalTime = 1;

	protected CountDownTimer timer;

	private OnFinishedListener onFinishedListener = OnFinishedListener.DEFAULT;
	
	public static interface OnFinishedListener {
		void onTimerFinished(TimingView view);
		static final OnFinishedListener DEFAULT = new OnFinishedListener() {
			@Override public void onTimerFinished(TimingView view) { }
		};
	}

	public TimingView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public TimingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public float getTotalTime() {
		return totalTime;
	}
	
	/**
	 * Set by timer whilst running. (In seconds)
	 */
	float timeRemaining;
	
	public void setTotalTime(float totalTime) {
		this.totalTime = totalTime;
		timer = new CountDownTimer((int)(totalTime * 1000), 200) {
			@Override
			public void onTick(long millisUntilFinished) {
				timeRemaining = ((float) millisUntilFinished) / 1000f;
			}
			
			@Override
			public void onFinish() {
				if(onFinishedListener != null)
					onFinishedListener.onTimerFinished(TimingView.this);
				timeRemaining = 0;
				invalidate();
			}
		};
	}

	public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
		if(onFinishedListener == null) onFinishedListener = OnFinishedListener.DEFAULT;
		this.onFinishedListener = onFinishedListener;
	}
	
	private ObjectAnimator anim;
	
	private transient long startTimeMillis;
	
	/**
	 * Starts the timer.
	 * @param startPosition The amount of the animation that has already
	 * been completed - in the range [0,1]
	 */
	protected void start(float startPosition) {
		startTimeMillis = System.currentTimeMillis();
		if(timer == null) throw new RuntimeException("A time hasn't been set yet");
		timer.start();
		anim = getPropertyAnimator(startPosition);
		anim.setDuration((int)(totalTime * 1000));
		anim.start();
	}
	
	/**
	 * Returns the {@link ObjectAnimator} to animate.
	 * @param startPosition A value between 0 and 1 indicating where the animation
	 * should start.
	 * @return
	 */
	protected abstract ObjectAnimator getPropertyAnimator(float startPosition);
	
	/**
	 * Starts the timer
	 */
	public void start() {
		start(0);
	}
	
	public void cancel() {
		if(timer != null) timer.cancel();
		if(anim != null) anim.cancel();
	}
	
	private float pausedRemainingTime;
	private float pausedStartPosition;
	
	/**
	 * Pauses the timer.
	 */
	public void pause() {
		final long progressSoFar = System.currentTimeMillis() - startTimeMillis;
		pausedRemainingTime = totalTime - ((float)progressSoFar / 1000f);
		pausedStartPosition = (float)progressSoFar / 1000f / totalTime;
		cancel();
		anim = null;
		timer = null;
	}
	
	/**
	 * Resumes the timer.
	 */
	public void resume() {
		setTotalTime(pausedRemainingTime);
		start(pausedStartPosition);
	}
	
	public void setPaused(boolean pause) {
		if(pause) pause();
		else resume();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		cancel();
		anim = null;
		timer = null;
		onFinishedListener = null;
	}
}
