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
	
	/**
	 * The group for things which should collide with the player
	 */
	protected static final int COLLISION_GROUP_PLAYER = 0x1;
	
	/**
	 * The group for simulations (off screen ones)
	 */
	protected static final int COLLISION_GROUP_SIMULATION = 0x2;
	
	/**
	 * The group for things which should not collide with the player (0)
	 */
	protected static final int COLLISION_GROUP_NONE = 0x0;
	
	protected SimulationContext context;
	
	protected final Body body;
	
	public SpaceItem(SimulationContext context, Vec2 pos, float angle, BodyType type) {
		this.context = context;
		BodyDef def = new BodyDef();
		def.position = pos;
		def.type = type;
		def.angle = angle * DEG_TO_RAD;
		body = context.world.createBody(def);
		
		body.setUserData(this);
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
	
	/**
	 * Rotation in DEGREES
	 * @return
	 */
	protected float getRotation() {
		return body.getAngle() * RAD_TO_DEG;
	}
	
	protected static final int INTERACT_LAYER_NORMAL = 0x1;
	protected static final int INTERACT_LAYER_PASSTHROUGH = 0x2;
	
	/**
	 * Unlinks this object from Box2D
	 */
	public void dispose() {
		context.world.destroyBody(body);
	}
}
