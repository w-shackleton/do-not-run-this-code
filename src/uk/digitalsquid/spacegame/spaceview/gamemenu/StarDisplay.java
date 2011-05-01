package uk.digitalsquid.spacegame.spaceview.gamemenu;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Paint;

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
	
	private RectMesh star;
	
	private static final Paint txtPaint = new Paint();
	static {
		txtPaint.setAntiAlias(true);
		txtPaint.setTextSize(30);
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
	private static final int STAR_JUMP_SPEED = 1;
	
	private final Portal portal;
	
	public StarDisplay(Context context, int starTotal, Portal portal) {
		txtPaint.setTypeface(StaticInfo.Fonts.bangers);
		star = new RectMesh(25, -25, 30, 30, R.drawable.star);
		this.starTotal = starTotal;
		this.portal = portal;
	}

	@Override
	public void drawStatic(GL10 gl, int width, int height, final Matrix matrix) {
		gl.glPushMatrix();
		gl.glTranslatef(-width / 2, +height / 2, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0, jump, 0);
		// star.setAlpha(1);
		star.draw(gl);
		gl.glPopMatrix();
		
		// c.drawText("" + displayedStarCount + " / " + starTotal, 50, 40, txtPaint);
		// TODO: DRAW TEXT
		gl.glPopMatrix();
	}

	public void incStarCount() {
		starCount++;
		if(starCount == starTotal)
			portal.activate();
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
