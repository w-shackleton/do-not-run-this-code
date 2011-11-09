package uk.digitalsquid.spacegame.spaceitem.blocks;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;

/**
 * The vortex over a block
 * @author william
 *
 */
public class BlockVortex implements Constants, Forceful {
	public static enum VortexType {
		LINEAR,
		ANGULAR
	}
	
	/**
	 * Identity transform
	 */
	private static final Transform transform = new Transform(new Vec2(), new Mat22(1, 0, 0, 1));
	
	protected final Shape catchArea;
	
	protected final Vec2 pos;
	/**
	 * The angle in RADIANS. 0 is pulling left.
	 */
	protected final float angle;
	
	protected final VortexType type;
	
	protected Vec2 linearSize;
	
	/**
	 * The size in RADIANS of an angular block
	 */
	protected float angularSize;
	/**
	 * The minimum width of the arc
	 */
	protected float angularYmin;
	/**
	 * The maximum width of the arc
	 */
	protected float angularYmax;
	
	/**
	 * Constructs a rectangular vortex
	 * @param center
	 * @param size
	 * @param angle The rotation in RADIANS
	 */
	public BlockVortex(Vec2 center, Vec2 size, float angle) {
		type = VortexType.LINEAR;
		pos = center;
		linearSize = size;
		this.angle = angle;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(size.x / 2, size.y / 2, center, angle);
		catchArea = shape;
		
		tmpDraw = new RectMesh(center.x, center.y, size.x, size.y, 1, 1, 0, 1);
		tmpDraw.setRotation(angle * RAD_TO_DEG);
	}
	
	private Vec2 force = new Vec2();
	private static final float FORCE_MAGNITUDE = 12f;
	private static final Vec2 LINEAR_FORCE = new Vec2(0, -FORCE_MAGNITUDE);
	
	@Override
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		if(catchArea.testPoint(transform, itemC)) {
			force.set(LINEAR_FORCE);
			CompuFuncs.rotateLocal(force, null, angle);
			return force;
		}
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		return false;
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
		return null;
	}
	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
	}
	
	protected RectMesh tmpDraw;
	
	public void draw(GL10 gl) {
		tmpDraw.draw(gl);
	}
}
