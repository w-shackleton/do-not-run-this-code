package uk.digitalsquid.spacegame.spaceview.gamemenu;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.gl.Text;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.StaticDrawable;
import android.content.Context;
import android.graphics.Matrix;

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
	
	private final RectMesh star;
	
	private final Text text;
	
	/**
	 * Causes star to 'jump' when star collected
	 */
	private float jump;
	private int jumpStatus = STAR_RESTING;
	
	private static final int STAR_RESTING = 0;
	private static final int STAR_RISING  = 1;
	private static final int STAR_FALLING = 2;
	
	private static final float STAR_JUMP_DIST = 1f;
	private static final float STAR_JUMP_SPEED = 0.1f;
	
	private final Portal portal;
	
	public StarDisplay(Context context, int starTotal, Portal portal) {
		star = new RectMesh(2.5f, -2.5f, 3.0f, 3.0f, R.drawable.star);
		this.starTotal = starTotal;
		this.portal = portal;
		
		text = new Text("0", 5.0f, -2.5f, 3.0f);
	}

	@Override
	public void drawStatic(GL10 gl, float width, float height, final Matrix matrix) {
		gl.glPushMatrix();
		gl.glTranslatef(-width / 2, +height / 2, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0, jump, 0);
		// star.setAlpha(1);
		star.draw(gl);
		gl.glPopMatrix();
		
		text.draw(gl);
		
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
		text.setText("" + displayedStarCount);
		jumpStatus = STAR_RISING;
	}

	public int getDisplayedStarCount() {
		return displayedStarCount;
	}

	@Override
	public void move(float millistep, float speedScale) { }

	@Override
	public void drawMove(float millistep, float speedscale) {
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
