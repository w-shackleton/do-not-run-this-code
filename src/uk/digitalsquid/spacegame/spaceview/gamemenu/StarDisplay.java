package uk.digitalsquid.spacegame.spaceview.gamemenu;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import codehead.cbfg.TexFont;

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
	
	private TexFont text = null;
	
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
	
	private final Context context;
	
	public StarDisplay(Context context, int starTotal, Portal portal) {
		this.context = context;
		
		star = new RectMesh(25, -25, 30, 30, R.drawable.star);
		this.starTotal = starTotal;
		this.portal = portal;
	}

	@Override
	public void drawStatic(GL10 gl, int width, int height, final Matrix matrix) {
		if(text == null) {
			text = new TexFont(context, gl);
			try {
				text.LoadFont("fonts/bangers.bff", gl);
			} catch (IOException e) {
				Log.v("SpaceGame", "Couldn't load font!", e);
			}
		}
		
		gl.glPushMatrix();
		gl.glTranslatef(-width / 2, +height / 2, 0);
		gl.glPushMatrix();
		gl.glTranslatef(0, jump, 0);
		// star.setAlpha(1);
		star.draw(gl);
		gl.glPopMatrix();
		
		// For some reason this doesn't obey matrices, so setting absolute position
		gl.glEnable(GL10.GL_BLEND); // Only if alpha present?
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		text.PrintAt(gl, "543896894", 50, 480-64);
		
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
