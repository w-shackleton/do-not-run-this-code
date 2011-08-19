package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.gl.Bezier;
import uk.digitalsquid.spacegame.gl.Mesh;
import uk.digitalsquid.spacegame.gl.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.assistors.Spring;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;

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
	
	private final Bezier tether;
	private final Spring springCalc;
	
	private State state = State.DISABLED;
	
	private final Player player;

	public Tether(Context context, Player player) {
		super(context, new Coord());
		tether = new Bezier(1, 1, 1, 1);
		this.player = player;
		
		springCalc = new Spring(7, 0, 0, 0, 0, 1f);
	}
	
	Mesh fingerImg = new RectMesh(0, 0, 30, 30, 1, 0, 0, 1);

	@Override
	public void move(float millistep, float speedScale) {
		springCalc.move(millistep, speedScale);
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
		if(state == State.TETHERED)
			springCalc.setEnds(pos.x, pos.y, player.itemC.x, player.itemC.y);
		else
			springCalc.setEnd(player.itemC.x, player.itemC.y); // Set same positions to 'hide' tether.
		
		springCalc.drawMove(millistep, speedscale);
		
		tether.setBezierPoints(springCalc.getSpringPoints());
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		if(state == State.TETHERED) fingerImg.draw(gl);
		tether.draw(gl);
	}

	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC) {
		if(state == State.DISABLED) return null;
		/*double forceX = pos.x - itemC.x;
		double forceY = pos.y - itemC.y;
		
		return new Coord(forceX / 2, forceY / 2);*/
		
		return springCalc.calculateEndForce();
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
