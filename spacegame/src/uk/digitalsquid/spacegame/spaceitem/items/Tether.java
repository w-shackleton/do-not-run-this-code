package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.spaceitem.assistors.Spring;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.Bezier;
import uk.digitalsquid.spacegamelib.gl.Mesh;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
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
	
	private final Player player;

	public Tether(SimulationContext context, Player player) {
		super(context, new Vec2(), 0, BodyType.STATIC); // TODO: CHECK WHAT TO DO ETC>>>>>
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
			springCalc.setEnds(getPosX(), getPosY(), player.itemC.x, player.itemC.y);
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
	public Vec2 calculateRF(Vec2 itemC) {
		if(state == State.DISABLED) return null;
		/*double forceX = pos.x - itemC.x;
		double forceY = pos.y - itemC.y;
		
		return new Vec2(forceX / 2, forceY / 2);*/
		
		return springCalc.calculateEndForce();
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
	}
	
	public void setTetherPos(Vec2 pos) {
		setTetherPos(pos.x, pos.y);
	}
	
	/**
	 * Sets the on-screen (and gravity position from) position of the tether.
	 * @param x
	 * @param y
	 */
	public void setTetherPos(float x, float y) {
		if(state != State.TETHERED) state = State.TETHERED;
		
		setPosX(x);
		setPosY(y);
		
		fingerImg.setXY(x, y);
	}
	
	public void untether() {
		state = State.DISABLED;
	}
}
