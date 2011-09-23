package uk.digitalsquid.spacegamelib.spaceitem;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegamelib.SimulationContext;

public abstract class SpaceItem
{
	/**
	 * Multiply a number of degrees by this to convert it to radians
	 */
	protected static final float DEG_TO_RAD = (float) (Math.PI / 180);
	
	/**
	 * Multiply a number of radians by this to convert it to degrees
	 */
	protected static final float RAD_TO_DEG = (float) (180 / Math.PI);
	
	protected SimulationContext context;
	
	protected final Body body;
	
	public SpaceItem(SimulationContext context, Vec2 pos, float angle, BodyType type) {
		this.context = context;
		BodyDef def = new BodyDef();
		def.position = pos;
		def.type = type;
		body = context.world.createBody(def);
	}
	
	/**
	 * Draw this object onto the screen
	 * @param gl		The GL to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void draw(GL10 gl, float worldZoom);
	
	public Vec2 getPos() {
		return body.getPosition();
	}
	
	public void setPosX(float x) {
		getPos().x = x;
	}
	
	public void setPosY(float y) {
		getPos().y = y;
	}
	
	public float getPosX() {
		return getPos().x;
	}
	
	public float getPosY() {
		return getPos().y;
	}
	
	protected float getRotation() {
		return body.getAngle();
	}
	
	protected static final int INTERACT_LAYER_NORMAL = 0x1;
	protected static final int INTERACT_LAYER_PASSTHROUGH = 0x2;
}
