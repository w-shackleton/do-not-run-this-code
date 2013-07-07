package uk.digitalsquid.contactrecall.ingame;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class TimerView extends View {
	
	private float totalTime = 1;
	private float visualProgress = 0;
	
	private final float margin;
	
	protected CountDownTimer timer;
	
	protected final DisplayMetrics displayMetrics;
	
	public static interface OnFinishedListener {
		void onTimerFinished(TimerView view);
		static final OnFinishedListener DEFAULT = new OnFinishedListener() {
			@Override public void onTimerFinished(TimerView view) { }
		};
	}
	
	private OnFinishedListener onFinishedListener = OnFinishedListener.DEFAULT;
	
	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		displayMetrics = context.getResources().getDisplayMetrics();
		
		margin = displayMetrics.density * 2;
		
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
	}

	public float getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(float totalTime) {
		this.totalTime = totalTime;
		timer = new CountDownTimer((int)(totalTime * 1000), (int)(totalTime * 1000)) {
			
			@Override
			public void onTick(long millisUntilFinished) {
			}
			
			@Override
			public void onFinish() {
				onFinishedListener.onTimerFinished(TimerView.this);
			}
		};
	}

	public void setOnFinishedListener(OnFinishedListener onFinishedListener) {
		if(onFinishedListener == null) onFinishedListener = OnFinishedListener.DEFAULT;
		this.onFinishedListener = onFinishedListener;
	}
	
	private ObjectAnimator anim;
	
	public void start() {
		if(timer == null) throw new RuntimeException("A time hasn't been set yet");
		timer.start();
		anim = ObjectAnimator.ofFloat(this, "visualProgress", 0f, 1f);
		anim.setDuration((int)(totalTime * 1000));
		anim.start();
	}
	
	public void cancel() {
		if(timer != null) timer.cancel();
		if(anim != null) anim.cancel();
	}

	public float getVisualProgress() {
		return visualProgress;
	}

	public void setVisualProgress(float visualProgress) {
		this.visualProgress = visualProgress;
		invalidate();
	}
	
	private RectF box = new RectF();
	private Paint outerPaint, innerPaint;
	
	@Override
	public void onDraw(Canvas c) {
		super.onDraw(c);
		float x = margin, y = margin;
		float width = c.getWidth() - margin * 2, height = c.getHeight() - margin * 2;
		if(width > height) {
			width = height;
		} else {
			height = width;
		}
		
		box.left = x; box.top = y;
		box.right = width; box.bottom = height;
		
		c.drawArc(box, -90, 360f * (1f-visualProgress), true, innerPaint);
		c.drawArc(box, -90, 360f * (1f-visualProgress), true, outerPaint);
	}
}
