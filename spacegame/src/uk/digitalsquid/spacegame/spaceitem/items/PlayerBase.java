package uk.digitalsquid.spacegame.spaceitem.items;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.Spherical;

public abstract class PlayerBase extends Spherical {
	public static final float BALL_RADIUS = 14;
	
	public final Vec2 itemC, itemVC = new Vec2(), itemRF = new Vec2(); // For portability to old code

	public PlayerBase(SimulationContext context, Vec2 coord, float radius) {
		super(context, coord, 1, BALL_RADIUS, BodyType.DYNAMIC);
		itemC = getPos();
	}

	/** 
	 * Returns the body, to allow the simulator to apply force to it.
	 * @return
	 */
	public Body getBody() {
		return body;
	}
}
