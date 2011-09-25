package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.spaceitem.assistors.Spring;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.Bezier;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

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
	
	public Tether(SimulationContext context) {
		super(context, new Vec2(), 0, BodyType.STATIC);
		tether = new Bezier(1, 1, 1, 1);
		
		springCalc = new Spring(7, 0, 0, 0, 0, 1f, 20f);
	}

	@Override
	public void move(float millistep, float speedScale) {
		springCalc.move(millistep, speedScale);
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
		springCalc.drawMove(millistep, speedscale);
		
		tether.setBezierPoints(springCalc.getSpringPoints());
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		tether.draw(gl);
	}

	/**
	 * Gets the force on the end of the spring
	 * @param itemC NOT USED
	 */
	@Override
	public Vec2 calculateRF(Vec2 itemC) {
		if(state == State.DISABLED) return null;
		
		return springCalc.calculateEndForce();
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
		return null;
	}

	@Override public void calculateVelocityMutable(Vec2 itemC, Vec2 itemVC, float itemRadius) { }
	
	public void disable() {
		state = State.DISABLED;
	}
	
	/**
	 * Turns the spring on and puts it in place
	 * @param sx
	 * @param sy
	 * @param dx
	 * @param dy
	 */
	public void activate(float sx, float sy, float dx, float dy) {
		state = State.TETHERED;
		springCalc.setPosition(sx, sy, dx, dy);
	}
	
	/**
	 * Smoothly updates the spring's ends
	 * @param sx
	 * @param sy
	 * @param dx
	 * @param dy
	 */
	public void update(float sx, float sy, float dx, float dy) {
		springCalc.setEnds(sx, sy, dx, dy);
	}
}
