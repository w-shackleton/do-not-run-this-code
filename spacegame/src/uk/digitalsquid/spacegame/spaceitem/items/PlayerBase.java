package uk.digitalsquid.spacegame.spaceitem.items;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.Spherical;

public abstract class PlayerBase extends Spherical {
	public static final float BALL_RADIUS = .14f;
	
	public final Vec2 itemC, itemRF = new Vec2(); // For portability to old code
	/**
	 * The force as it appears to the user
	 */
	public final Vec2 apparentRF = new Vec2();

	public PlayerBase(SimulationContext context, Vec2 coord, float radius) {
		super(context, coord, 1, BALL_RADIUS, BodyType.DYNAMIC);
		fixture.setRestitution(0.9f);
		itemC = getPos();
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_PLAYER;
		fixture.getFilterData().maskBits = COLLISION_GROUP_PLAYER;
	}

	/** 
	 * Returns the body, to allow the simulator to apply force to it.
	 * @return
	 */
	public Body getBody() {
		return body;
	}
	
	public Vec2 getVelocity() {
		return body.getLinearVelocity();
	}
	
	public void setVelocity(Vec2 v) {
		body.setLinearVelocity(v);
	}
}
