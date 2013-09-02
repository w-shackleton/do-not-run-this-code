package uk.digitalsquid.contactrecall.ingame;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class TimerView extends View {
	
	private float totalTime = 1;
	private float visualProgress = 0;
	
	/**
	 * Space around the circle
	 */
	private final float margin;
	/**
	 * Space within the circle around the text
	 */
	private final float padding;
	
	private String text = "";
	
	protected CountDownTimer timer;
	
	protected final DisplayMetrics displayMetrics;
	
	public static interface OnFinishedListener {
		void onTimerFinished(TimerView view);
		static final OnFinishedListener DEFAULT = new OnFinishedListener() {
			@Override public void onTimerFinished(TimerView view) { }
		};
	}
	
	private final float textSize;
	
	private OnFinishedListener onFinishedListener = OnFinishedListener.DEFAULT;
	
	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		displayMetrics = context.getResources().getDisplayMetrics();
		
		margin = displayMetrics.density * 5;
		padding = displayMetrics.density * 10;
		
		
		outerPaint = new Paint();
		outerPaint.setAntiAlias(true);
		outerPaint.setStyle(Style.STROKE);
		outerPaint.setStrokeCap(Cap.BUTT);
		outerPaint.setStrokeJoin(Join.ROUND);
		outerPaint.setStrokeWidth(displayMetrics.density * 2);
		outerPaint.setColor(Color.rgb(0, 0, 0));
		
		innerPaint = new Paint();
		innerPaint.setAntiAlias(false);
		innerPaint.setStyle(Style.FILL);
		innerPaint.setColor(Color.rgb(200, 200, 200));
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.rgb(0, 0, 0));
		// TODO: Get text size from system, for accessibility etc.
		textPaint.setTextSize(18 * displayMetrics.scaledDensity);
		textPaint.setTextAlign(Align.CENTER);

		float textWidth = textPaint.measureText("000"); // Measure three digits of time
		float textHeight = textPaint.getTextSize();
		textSize = Math.max(textWidth, textHeight);
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
				onFinishedListener.onTimerFinished(TimerView.this);
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
		anim = ObjectAnimator.ofFloat(this, "visualProgress", startPosition, 1f);
		anim.setDuration((int)(totalTime * 1000));
		anim.start();
	}
	
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

	public float getVisualProgress() {
		return visualProgress;
	}

	public void setVisualProgress(float visualProgress) {
		this.visualProgress = visualProgress;
		invalidate();
	}
	
	private RectF box = new RectF();
	private RectF innerBox = new RectF();
	private Paint outerPaint, innerPaint;
	private Paint textPaint;
	
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int measuredSize = (int) (textSize + margin * 2 + padding * 2); // Margin and padding
		setMeasuredDimension(measuredSize, measuredSize);
	}
	
	Path shape = new Path();
	
	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		float x = margin, y = margin;
		float width = c.getWidth() - margin, height = c.getHeight() - margin;
		if(width > height) {
			width = height;
		} else {
			height = width;
		}
		
		box.left = x; box.top = y;
		box.right = width; box.bottom = height;
		
		innerBox.left = box.centerX() - textSize / 2;
		innerBox.right = box.centerX() + textSize / 2;
		innerBox.top = box.centerY() - textSize / 2;
		innerBox.bottom = box.centerY() + textSize / 2;
		
		String centreText = text.replace("%r", String.valueOf((int)Math.floor(timeRemaining)));
		
		shape.reset();
		shape.addArc(box, -90, 360f * (1f-visualProgress));
		shape.arcTo(innerBox, 360f * (1f-visualProgress) - 90f, 360f * visualProgress);
		shape.close();
		c.drawPath(shape, innerPaint);
		c.drawPath(shape, outerPaint);
		
		c.drawText(
				centreText,
				c.getWidth() / 2,
				c.getHeight() / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
				textPaint);
	}

	public String getText() {
		return text;
	}

	/**
	 * Sets the text displayed at the centre of this timer.
	 * The text %r will be replaced with the number of seconds left on the timer.
	 * @param text
	 */
	public void setText(String text) {
		if(text == null) this.text = "";
		this.text = text;
		invalidate();
	}
	
	public void setTextAsCountdown() {
		setText("%r");
	}
}
