package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.IsClickable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

/**
 * Block pieces fit together and are aligned to a grid.
 * @author william
 *
 */
public class Block extends SpaceItem implements Moveable, Forceful, IsClickable {
	/**
	 * The size of this block
	 */
	protected Vec2 size;
	
	protected Fixture fixture;

	public Block(SimulationContext context, Vec2 pos, Vec2 size, float angle, BlockDef def) {
		super(context, pos, angle, BodyType.DYNAMIC);
		
		setSize(size, def.getMinSize(), def.getMaxSize());
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = def.getShape();
		fixtureDef.density = 1;
		fixtureDef.friction = def.getFriction();
		fixtureDef.restitution = def.getRestitution();
		fixture = body.createFixture(fixtureDef);
		fixture.setUserData(this);
	}
	
	/**
	 * This contstructor doesn't set up the block's data. This one should probably be overridden.
	 * @param context
	 * @param pos
	 * @param size
	 * @param angle
	 */
	Block(SimulationContext context, Vec2 pos, Vec2 size, float angle) {
		super(context, pos, angle, BodyType.DYNAMIC);
		this.size = size;
	}
	
	/**
	 * Sets the size within an area
	 * @param size
	 * @param min
	 * @param max
	 */
	void setSize(Vec2 size, Vec2 min, Vec2 max) {
		this.size = size;
		this.size.x = CompuFuncs.TrimMinMax(size.x, min.x, max.x);
		this.size.y = CompuFuncs.TrimMinMax(size.y, min.y, max.y);
	}

	@Override
	public boolean isClicked(float x, float y) {
		return false;
	}

	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		return false;
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV,
			float itemRadius) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV,
			float itemRadius) {
	}

	@Override
	public void move(float millistep, float speedScale) {
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
	}
}
