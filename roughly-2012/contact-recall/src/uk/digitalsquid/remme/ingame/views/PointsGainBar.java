package uk.digitalsquid.remme.ingame.views;

import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.misc.Function;
import uk.digitalsquid.remme.misc.Utils;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Shows the number of points the user will get if they answer the question.
 * @author william
 *
 */
public class PointsGainBar extends TimingView {
	
	private Drawable pointerBg;
	private Drawable progressBar;
	
	private Paint textPaint;
	
	private final float textWidth, textHeight;
	private final float pointerWidth, pointerHeight;
	private final float barHeight, minBarWidth;
	
	private Function<Integer, Float> pointsGenerator = new Function<Integer, Float>() {
		@Override
		public Integer call(Float arg) {
			return (int)(arg * 1000);
		}
	};
	
	private float progress;

	public PointsGainBar(Context context, AttributeSet attrs) {
		super(context, attrs, android.R.attr.progressBarStyleHorizontal);
		progressBar = context.getResources().getDrawable(R.drawable.pointsgainbar);
		
		pointerBg = context.getResources().getDrawable(R.drawable.slider);

		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.rgb(0, 0, 0));
		// TODO: Get text size from system, for accessibility etc.
		textPaint.setTextSize(18 * displayMetrics.scaledDensity);
		textPaint.setTextAlign(Align.CENTER);
		
		textWidth = textPaint.measureText("00000");
		textHeight = textPaint.getTextSize();
		
		Rect pointerPadding = new Rect();
		pointerBg.getPadding(pointerPadding);
		pointerWidth = textWidth + pointerPadding.left + pointerPadding.right;
		pointerHeight = textHeight + pointerPadding.top + pointerPadding.bottom;
		
		barHeight = 20 * displayMetrics.density;
		minBarWidth = pointerWidth;
		
		setProgress(1);
	}
	
	@Override
	protected ObjectAnimator getPropertyAnimator(float startPosition) {
		return ObjectAnimator.ofFloat(this, "progress", 1f - startPosition, 0);
	}

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int dw = (int)minBarWidth;
        int dh = (int) (pointerHeight + barHeight);

        setMeasuredDimension(resolveSize(dw, widthMeasureSpec), dh);
    }

	private transient Rect pointerPos = new Rect();
	private transient Rect progressPos = new Rect();
	private transient Rect pointerPadding = new Rect();
	
	protected synchronized void onDraw(Canvas c) {
		super.onDraw(c);
		final float pointerCentre = Utils.minMax(getWidth() * getProgress(),
				pointerWidth / 2,
				getWidth() - pointerWidth / 2);
		
		pointerPos.left = (int) (pointerCentre - pointerWidth / 2);
		pointerPos.right = pointerPos.left + (int)pointerWidth;
		pointerPos.top = 0;
		pointerPos.bottom = pointerPos.top + (int)pointerHeight;
		
		pointerBg.setBounds(pointerPos);
		pointerBg.draw(c);
		pointerBg.getPadding(pointerPadding);
		
		c.drawText(String.valueOf(pointsGenerator.call(getProgress())),
				(float)pointerPos.centerX(),
				(float)pointerPos.bottom - (float)pointerPadding.bottom,
				textPaint);
		
		progressPos.left = 0;
		progressPos.right = getWidth();
		progressPos.top = pointerPos.bottom;
		progressPos.bottom = (int) (progressPos.top + barHeight);
		progressBar.setBounds(progressPos);
		progressBar.draw(c);
	}

	public float getProgress() {
		return progress;
	}

	public void setProgress(float progress) {
		this.progress = progress;
		
		Drawable progressDrawable = null;
		
		if(progressBar instanceof LayerDrawable)
			progressDrawable = ((LayerDrawable)progressBar)
					.findDrawableByLayerId(android.R.id.progress);
		(progressDrawable != null ? progressDrawable : progressBar)
				.setLevel((int) (progress * 10000));
		postInvalidate();
	}
	
	/**
	 * Gets the current points value being displayed
	 */
	public int getVisualPoints() {
		return pointsGenerator.call(getProgress());
	}

	public void setPointsGenerator(Function<Integer, Float> pointsGenerator) {
		this.pointsGenerator = pointsGenerator;
	}
}
