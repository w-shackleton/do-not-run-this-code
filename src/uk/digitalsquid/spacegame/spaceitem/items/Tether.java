package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.misc.Mesh;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;

/**
 * The 'tether' which attaches to the user's finger to move the character.
 * Also calculates the physics for the tether.
 * @author william
 *
 */
public final class Tether extends SpaceItem implements Moveable, Forceful {
	
	private enum State {
		DISABLED,
		TETHERING,
		TETHERED,
		UNTETHERING,
	}
	
	private State state = State.DISABLED;

	public Tether(Context context) {
		super(context, new Coord());
	}
	
	Mesh fingerImg = new RectMesh(0, 0, 30, 30, 1, 0, 0, 1);

	@Override
	public void move(float millistep, float speedScale) {
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		fingerImg.draw(gl);
	}

	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC) {
		if(state == State.DISABLED) return null;
		double forceX = pos.x - itemC.x;
		double forceY = pos.y - itemC.y;
		
		return new Coord(forceX / 2, forceY / 2);
	}

	@Override
	public BallData calculateVelocityImmutable(Coord itemC, Coord itemVC, float itemRadius, boolean testRun) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Coord itemC, Coord itemVC, float itemRadius) {
	}
	
	public void setTetherPos(Coord pos) {
		setTetherPos((float)pos.x, (float)pos.y);
	}
	
	/**
	 * Sets the on-screen (and gravity position from) position of the tether.
	 * @param x
	 * @param y
	 */
	public void setTetherPos(double x, double y) {
		if(state != State.TETHERED) state = State.TETHERED;
		
		pos.x = x;
		pos.y = y;
		
		fingerImg.setXY((float)x, (float)y);
	}
	
	public void untether() {
		state = State.DISABLED;
	}
}
