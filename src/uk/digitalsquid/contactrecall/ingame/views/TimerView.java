package uk.digitalsquid.contactrecall.ingame.views;

import uk.digitalsquid.contactrecall.R;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

public class TimerView extends TimingView {
	
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
	
	protected final DisplayMetrics displayMetrics;
	
	private final float textSize;
	
	public TimerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		displayMetrics = context.getResources().getDisplayMetrics();
		
		// Get attributes
		TypedArray customAttrs = context.getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.PieChart,
				0, 0);
		
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
		
		try {
			innerPaint.setColor(
					customAttrs.getColor(
							R.styleable.PieChart_timerBackground,
							Color.rgb(200, 200, 200)));
		} finally {
			customAttrs.recycle();
		}

		float textWidth = textPaint.measureText("000"); // Measure three digits of time
		float textHeight = textPaint.getTextSize();
		textSize = Math.max(textWidth, textHeight);
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
	protected synchronized void onDraw(Canvas c) {
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


	@Override
	protected ObjectAnimator getPropertyAnimator(float startPosition) {
		return ObjectAnimator.ofFloat(this, "visualProgress", startPosition, 1f);
	}
}
