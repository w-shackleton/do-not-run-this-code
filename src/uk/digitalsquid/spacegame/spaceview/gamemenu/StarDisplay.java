package uk.digitalsquid.spacegame.spaceview.gamemenu;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class StarDisplay implements StaticDrawable, Moveable {
	
	/**
	 * Actual number of stars.
	 */
	private int starCount = 0;
	
	private final int starTotal;
	
	/**
	 * Number of displayed stars. This is different as it is updated slightly later (when the star reaches the count thing)
	 */
	private int displayedStarCount = 0;
	
	private Drawable star;
	
	private static final Paint txtPaint = new Paint();
	static {
		txtPaint.setAntiAlias(true);
		txtPaint.setTextSize(40);
		txtPaint.setTextAlign(Paint.Align.LEFT);
		txtPaint.setColor(0xFFFFFFFF);
	}
	
	/**
	 * Causes star to 'jump' when star collected
	 */
	private int jump;
	private int jumpStatus = STAR_RESTING;
	
	private static final int STAR_RESTING = 0;
	private static final int STAR_RISING  = 1;
	private static final int STAR_FALLING = 2;
	private static final int STAR_JUMP_DIST = 10;
	private static final int STAR_JUMP_SPEED = 3;
	
	public StarDisplay(Context context, int starTotal) {
		txtPaint.setTypeface(StaticInfo.Fonts.bangers);
		star = (BitmapDrawable) context.getResources().getDrawable(R.drawable.star);
		this.starTotal = starTotal;
	}

	@Override
	public void drawStatic(Canvas c, float worldZoom, int width, int height, Matrix matrix) {
		star.setAlpha(255);
		star.setBounds(10, 10 - jump, 50, 50 - jump);
		star.draw(c);
		
		c.drawText("" + displayedStarCount + " / " + starTotal, 60, 50, txtPaint);
	}

	public void incStarCount() {
		starCount++;
	}

	public int getStarCount() {
		return starCount;
	}

	public void incDisplayedStarCount() {
		displayedStarCount++;
		jumpStatus = STAR_RISING;
	}

	public int getDisplayedStarCount() {
		return displayedStarCount;
	}

	@Override
	public void move(float millistep, float speedScale) {
		switch(jumpStatus) {
		case STAR_RESTING:
		default:
			break;
		case STAR_RISING:
			jump += STAR_JUMP_SPEED;
			if(jump > STAR_JUMP_DIST) jumpStatus = STAR_FALLING;
			break;
		case STAR_FALLING:
			jump -= STAR_JUMP_SPEED;
			if(jump <= 0) jumpStatus = STAR_RESTING;
			break;
		}
	}

}
