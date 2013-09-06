package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.R;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Shows the number of points the user will get if they answer the question.
 * @author william
 *
 */
public class PointsGainBar extends TimingView {
	
	private static final int PROGRESS_MAX = 1000;
	
	private NinePatchDrawable pointerBg;
	
	private Paint textPaint;
	
	private final float textWidth, textHeight;
	private final float pointerWidth, pointerHeight;

	public PointsGainBar(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.progressBarStyleHorizontal);
		setProgressDrawable(context.getResources().getDrawable(R.drawable.pointsgainbar));
		setMax(PROGRESS_MAX);
		
		pointerBg = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.points_slider);

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.rgb(0, 0, 0));
		// TODO: Get text size from system, for accessibility etc.
		textPaint.setTextSize(18 * displayMetrics.scaledDensity);
		textPaint.setTextAlign(Align.CENTER);
		
		textWidth = textPaint.measureText("00000");
		textHeight = textPaint.getTextSize();
		
		pointerWidth = pointerBg.getMinimumWidth() + textWidth;
		pointerHeight = pointerBg.getMinimumHeight() + textHeight;
	}
	
	public float time;

	@Override
	protected ObjectAnimator getPropertyAnimator(float startPosition) {
		return ObjectAnimator.ofInt(this, "progress", (int)((1f - startPosition) * PROGRESS_MAX), 0);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(
				getMeasuredWidth(),
				getMeasuredHeight() + 20);
	}
	
	private transient Rect pointerPos = new Rect();
	private transient Rect pointerPadding = new Rect();
	
	protected synchronized void onDraw(Canvas c) {
		c.translate(0, pointerPos.height());
		super.onDraw(c);
		final float pointerCentre = getWidth() * getProgress() / getMax();
		
		pointerPos.left = (int) (pointerCentre - pointerWidth / 2);
		pointerPos.right = pointerPos.left + (int)pointerWidth;
		pointerPos.top = 0;
		pointerPos.bottom = pointerPos.top + (int)pointerHeight;
		
		pointerBg.setBounds(pointerPos);
		
		pointerBg.draw(c);
		
		pointerBg.getPadding(pointerPadding);
		
		c.drawText("00000",
				(float)pointerPos.centerX(),
				(float)pointerPos.bottom - (float)pointerPadding.bottom,
				textPaint);
	}
}
