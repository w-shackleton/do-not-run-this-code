package uk.digitalsquid.spacegamelib.misc;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.Rectangular;

/**
 * One of the level walls, ie. an invisible wall.
 * @author william
 *
 */
public class LevelWall extends Rectangular {
	
	protected static final float WALL_RESTITUTION = 0.8f;

	public LevelWall(SimulationContext context, Vec2 coord, Vec2 size) {
		super(context, coord, size, 1, 0, WALL_RESTITUTION, BodyType.STATIC);
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
	}
}
