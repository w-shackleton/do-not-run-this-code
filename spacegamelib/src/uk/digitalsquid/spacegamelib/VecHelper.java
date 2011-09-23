package uk.digitalsquid.spacegamelib;

import org.jbox2d.common.Vec2;

public final class VecHelper {
	public static final float dist(Vec2 one, Vec2 two) {
		float dx = one.x - two.x;
		float dy = one.y - two.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * Returns the angle of this vector from the other in DEGREES
	 * @param vec
	 * @return
	 */
	public static final float angleFromDeg(Vec2 vec, Vec2 from) {
		return (float) (Math.atan2(vec.x - from.x, vec.y - from.y) * 180 / Math.PI); 
	}
	
	/**
	 * Returns the angle of this vector in DEGREES
	 * @param vec
	 * @return
	 */
	public static final float angleDeg(Vec2 vec) {
		return (float) (Math.atan2(vec.x, vec.y) * 180 / Math.PI); 
	}
	
	/**
	 * Returns the angle of this vector in RADIANS
	 * @param vec
	 * @return
	 */
	public static final float angleRad(Vec2 vec) {
		return (float) Math.atan2(vec.x, vec.y); 
	}
}
