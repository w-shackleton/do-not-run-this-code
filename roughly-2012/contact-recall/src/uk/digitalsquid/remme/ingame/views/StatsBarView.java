package uk.digitalsquid.remme.ingame.views;

import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.misc.Utils;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class StatsBarView extends View {
	
	private final Paint correctPaint, incorrectPaint, discardPaint, basePaint;

	public StatsBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		final float density = context.getResources().getDisplayMetrics().density;

		correctPaint = new Paint();
		correctPaint.setStyle(Style.FILL);
		correctPaint.setColor(context.getResources().getColor(R.color.correct));
		incorrectPaint = new Paint();
		incorrectPaint.setStyle(Style.FILL);
		incorrectPaint.setColor(context.getResources().getColor(R.color.incorrect));
		discardPaint = new Paint();
		discardPaint.setStyle(Style.FILL);
		discardPaint.setColor(context.getResources().getColor(R.color.discard));

		basePaint = new Paint();
		basePaint.setStyle(Style.STROKE);
		basePaint.setColor(context.getResources().getColor(android.R.color.white));
		basePaint.setStrokeWidth(2 * density);
	}

	private float correct;
	private float incorrect;
	private float discard;

	public float getCorrect() {
		return correct;
	}

	public void setCorrect(float correct) {
		this.correct = Utils.minMax(correct, 0, 1);
		postInvalidate();
	}

	public float getIncorrect() {
		return incorrect;
	}

	public void setIncorrect(float incorrect) {
		this.incorrect = Utils.minMax(incorrect, 0, 1);
		postInvalidate();
	}

	public float getDiscard() {
		return discard;
	}

	public void setDiscard(float discard) {
		this.discard = Utils.minMax(discard, 0, 1);
		postInvalidate();
	}
	
	@Override
	protected void onDraw(Canvas c) {
		super.onDraw(c);
		
		final int width = getWidth(), height = getHeight();

		final int split1 = width / 3;
		final int split2 = width * 2 / 3;
		
		final int barTop1 = (int) (height * (1f - correct));
		final int barTop2 = (int) (height * (1f - discard));
		final int barTop3 = (int) (height * (1f - incorrect));
		
		c.drawRect(0, barTop1, split1, height, correctPaint);
		c.drawRect(split1, barTop2, split2, height, discardPaint);
		c.drawRect(split2, barTop3, width, height, incorrectPaint);
		
		c.drawLine(0, height - 1, width, height - 1, basePaint);
	}
}
