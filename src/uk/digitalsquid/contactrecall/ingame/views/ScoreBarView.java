package uk.digitalsquid.contactrecall.ingame.views;

import uk.digitalsquid.contactrecall.misc.Utils;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ScoreBarView extends View {

	private int expectedScore;
	private int score;
	private int currentScore;
	
	protected final int preferredWidth, preferredHeight;
	
	private final Paint linePaint;
	private final Paint fillPaint;

	public ScoreBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		preferredHeight = (int) (100 * displayMetrics.density);
		preferredWidth = (int) (20 * displayMetrics.density);
		
		linePaint = new Paint();
		linePaint.setColor(Color.rgb(20, 20, 20));
		linePaint.setStrokeCap(Cap.BUTT);
		linePaint.setStrokeJoin(Join.BEVEL);
		linePaint.setStrokeWidth(2 * displayMetrics.density);
		linePaint.setStyle(Style.STROKE);

		fillPaint = new Paint();
		fillPaint.setStyle(Style.FILL);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

	    final int desiredWidth = preferredWidth;
	    final int desiredHeight = preferredHeight;

	    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

	    int width;
	    int height;

	    //Measure Width
	    if (widthMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        width = widthSize;
	    } else if (widthMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        width = Math.min(desiredWidth, widthSize);
	    } else {
	        //Be whatever you want
	        width = desiredWidth;
	    }

	    //Measure Height
	    if (heightMode == MeasureSpec.EXACTLY) {
	        //Must be this size
	        height = heightSize;
	    } else if (heightMode == MeasureSpec.AT_MOST) {
	        //Can't be bigger than...
	        height = Math.min(desiredHeight, heightSize);
	    } else {
	        //Be whatever you want
	        height = desiredHeight;
	    }

	    //MUST CALL THIS
	    setMeasuredDimension(width, height);
	}
	
	public int getExpectedScore() {
		return expectedScore;
	}

	public void setExpectedScore(int expectedScore) {
		this.expectedScore = expectedScore;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getCurrentScore() {
		return currentScore;
	}

	public void setCurrentScore(int currentScore) {
		this.currentScore = currentScore;
		postInvalidate();
	}
	
	/**
	 * Starts the animation up to the top score
	 */
	public void start(int millis) {
		ObjectAnimator anim = ObjectAnimator.ofInt(this, "currentScore", 0, score);
		anim.setInterpolator(new AccelerateDecelerateInterpolator());
		anim.setDuration(millis);
		anim.start();
	}
	
	/**
	 * Sets the score position immediately
	 */
	public void showImmediateScore() {
		setCurrentScore(score);
		postInvalidate();
	}
	
	/**
	 * The position from the top at which to display the expected score
	 */
	private static final float POS_MAX = 0.2f;
	/**
	 * The position from the top at which to display the zero mark
	 */
	private static final float POS_ZERO = 0.8f;
	
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		
		final int width = getWidth();
		final int height = getHeight();
		
		int barStart = (int) (POS_ZERO * height);
		int barEnd = (int) ((((float)getCurrentScore() / (float)expectedScore) *
				(POS_MAX - POS_ZERO) + POS_ZERO) * height);
		if(barEnd < barStart) {
			int tmp = barEnd; barEnd = barStart; barStart = tmp;
		}

		fillPaint.setColor(generateColour((float)getCurrentScore() / (float)expectedScore));
		c.drawRect(0, barStart, width, barEnd, fillPaint);
		
		c.drawLine(0, POS_ZERO * height, width, POS_ZERO * height, linePaint);
		c.drawLine(0, POS_MAX * height, width, POS_MAX * height, linePaint);
	}
	
	/**
	 * Generates the appropriate colour for the bar, given a score, usually bounded by
	 * [0,1]
	 * @param amount
	 */
	private static final int generateColour(float amount) {
		amount = Utils.minMax(amount, -0.5f, 1);
		final float bottomMul = 1 - (amount + 0.5f) / 1.5f;
		final float topMul = (amount + 0.5f) / 1.5f;
		final int bottomR = 0xEF;
		final int bottomG = 0x75;
		final int bottomB = 0x2F;
		final int topR = 0x66;
		final int topG = 0xED;
		final int topB = 0x4A;
		return Color.rgb(
				(int)(bottomR * bottomMul + topR * topMul),
				(int)(bottomG * bottomMul + topG * topMul),
				(int)(bottomB * bottomMul + topB * topMul));
	}
}
