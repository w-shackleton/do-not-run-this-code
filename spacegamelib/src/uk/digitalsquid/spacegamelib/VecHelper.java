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
		return (float) (Math.atan2(vec.y - from.y, vec.x - from.x) * 180 / Math.PI); 
	}
	
	/**
	 * Returns the angle of this vector from the other in RADIANS
	 * @param vec
	 * @return
	 */
	public static final float angleFromRad(Vec2 vec, Vec2 from) {
		return (float)Math.atan2(vec.y - from.y, vec.x - from.x); 
	}
	
	/**
	 * Returns the angle of this vector in DEGREES
	 * @param vec
	 * @return
	 */
	public static final float angleDeg(Vec2 vec) {
		return (float) (Math.atan2(vec.y, vec.x) * 180 / Math.PI); 
	}
	
	/**
	 * Returns the positive angle of this vector in DEGREES
	 * @param vec
	 * @return
	 */
	public static final float anglePositiveDeg(Vec2 vec) {
		return CompuFuncs.mod(angleDeg(vec), 360);
	}
	
	/**
	 * Returns the angle of this vector in RADIANS
	 * @param vec
	 * @return
	 */
	public static final float angleRad(Vec2 vec) {
		return (float) Math.atan2(vec.y, vec.x); 
	}
	
	/**
	 * Sets a {@link Vec2} from polar coords.
	 * @param out
	 * @param angle The angle in RADIANS
	 * @param magnitude
	 */
	public static final void vecFromPolar(Vec2 out, float angle, float magnitude) {
		out.x = (float)Math.cos(angle) * magnitude;
		out.y = (float)Math.sin(angle) * magnitude;
	}

	/**
	 * Rotate this {@link Vec2} around the specified {@link Vec2} {@code rot}, by the amount of radians
	 * @param orig The origin around which to rotate (can be {@code null}, in which case the origin is {@code (0,0)})
	 * @param rot The amount to rotate, in RADIANS
	 * @return A new {@link Vec2}, which has been rotated
	 */
	public static final void rotateLocal(Vec2 point, Vec2 orig, float rot)
	{
		if(orig != null) {
			float tx = orig.x + CompuFuncs.rotateX(point.x - orig.x, point.y - orig.y, rot);
			point.y = orig.y + CompuFuncs.rotateY(point.x - orig.x, point.y - orig.y, rot);
			point.x = tx;
		} else {
			float tx = CompuFuncs.rotateX(point.x, point.y, rot);
			point.y = CompuFuncs.rotateY(point.x, point.y, rot);
			point.x = tx;
		}
	}

	/**
	 * Rotate this {@link Coord} around the specified {@link Coord} {@code rot}, by the amount of radians
	 * @param orig The origin around which to rotate (can be {@code null}, in which case the origin is {@code (0,0)})
	 * @param rot The amount to rotate, in RADIANS
	 * @return A new {@link Coord}, which has been rotated
	 */
	public static final Vec2 rotateCoord(Vec2 point, Vec2 orig, float rot)
	{
		if(orig != null)
			return new Vec2(
					orig.x + CompuFuncs.rotateX(point.x - orig.x, point.y - orig.y, rot),
					orig.y + CompuFuncs.rotateY(point.x - orig.x, point.y - orig.y, rot));
		return new Vec2(
				CompuFuncs.rotateX(point.x, point.y, rot),
				CompuFuncs.rotateY(point.x, point.y, rot));
	}

	public static final Vec2[] rotateCoords(Vec2[] old, Vec2 orig, float rot)
	{
		Vec2[] ret = new Vec2[old.length];
		for(int i = 0; i < old.length; i++)
			ret[i] = rotateCoord(old[i], orig, rot);
		return ret;
	}
}
