package uk.digitalsquid.spacegame;

import java.io.Serializable;

import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Represents a coordinate made of x and y points
 * @author william
 *
 */
public final class Coord implements Serializable
{
	/**
	 * Serial Version UID (Required by Serializable)
	 */
	private static final long serialVersionUID = -5866929819832631068L;
	public double x, y;
	
	public Coord (double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	public Coord()
	{
		x = 0;
		y = 0;
	}
	public Coord(Coord old)
	{
		x = old.x;
		y = old.y;
	}
	
	public final void copyFrom(Coord old) {
		x = old.x;
		y = old.y;
	}
	
	private static final Matrix mtrix = new Matrix();
	private static final float[] mtrixNums = new float[2];
	
	public Coord(double x, double y, Matrix matrix)
	{
		synchronized(mtrix)
		{
			mtrixNums[0] = (float) x;
			mtrixNums[1] = (float) y;
			matrix.invert(mtrix);
			mtrix.mapPoints(mtrixNums);
			this.x = mtrixNums[0];
			this.y = mtrixNums[1];
		}
	}
	
	public void reset() {
		x = 0;
		y = 0;
	}
	
	public Coord scale(float scaleFactor)
	{
		return new Coord(x * scaleFactor, y * scaleFactor);
	}
	
	public void scaleThis(float scaleFactor)
	{
		x *= scaleFactor;
		y *= scaleFactor;
	}
	
	public Coord add(Coord orig)
	{
		if(orig == null) return this;
		return new Coord(x + orig.x, y + orig.y);
	}
	//TODO: Add addInto function
	
	public Coord minus(Coord orig)
	{
		if(orig == null) return new Coord(this);
		return new Coord(x - orig.x, y - orig.y);
	}
	
	public void minusThis(Coord orig)
	{
		if(orig == null) return;
		x -= orig.x;
		y -= orig.y;
	}
	
	public void addThis(Coord orig)
	{
		if(orig == null) return;
		x += orig.x;
		y += orig.y;
	}
	
	public static final double getLength(Coord a, Coord b) {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}
	
	/**
	 * Performs the calculation into coordInto, as to save the GC a bit
	 * @param orig
	 * @param coordInto
	 */
	public void addInto(Coord orig, Coord coordInto)
	{
		if(orig == null)
		{
			coordInto.x = x;
			coordInto.y = y;
		}
		coordInto.x = x + orig.x;
		coordInto.y = y + orig.y;
	}
	
	/**
	 * Performs the calculation into coordInto, as to save the GC a bit
	 * @param orig
	 * @param coordInto
	 */
	public void minusInto(Coord orig, Coord coordInto)
	{
		if(orig == null)
		{
			coordInto.x = x;
			coordInto.y = y;
		}
		coordInto.x = x - orig.x;
		coordInto.y = y - orig.y;
	}
	
	/**
	 * Get the length of this {@link Coord} from the origin.<br>
	 * To find the distance between two points:<br>
	 * {@code a.minus(b).getLength();}
	 */
	public double getLength()
	{
		return Math.sqrt((x*x) + (y*y));
	}
	
	/**
	 * Get the rotation of this {@link Coord} in degrees
	 * @return The rotation of this Coord about the origin
	 */
	public float getRotation()
	{
		return (float) (Math.atan2(y, x) * 180 / Math.PI);
	}
	
	@Override
	public String toString()
	{
		return "(" + (int)x + "," + (int)y + ")";
	}
	
	/**
	 * Rotate this {@link Coord} around the specified {@link Coord} {@code rot}, by the amount of radians
	 * @param orig The origin around which to rotate (can be {@code null}, in which case the origin is {@code (0,0)})
	 * @param rot The amount to rotate, in RADIANS
	 * @return A new {@link Coord}, which has been rotated
	 */
	public Coord rotate(Coord orig, float rot)
	{
		if(orig != null)
			return new Coord(
					orig.x + CompuFuncs.RotateX(x - orig.x, y - orig.y, rot),
					orig.y + CompuFuncs.RotateY(x - orig.x, y - orig.y, rot));
		return new Coord(
				CompuFuncs.RotateX(x, y, rot),
				CompuFuncs.RotateY(x, y, rot));
	}
	
	/**
	 * Rotate this {@link Coord} around the specified {@link Coord} {@code rot}, by the amount of radians
	 * @param orig The origin around which to rotate (can be {@code null}, in which case the origin is {@code (0,0)})
	 * @param rot The amount to rotate, in RADIANS
	 * @return A new {@link Coord}, which has been rotated
	 */
	public void rotateThis(Coord orig, float rot)
	{
		if(orig != null) {
			x = orig.x + CompuFuncs.RotateX(x - orig.x, y - orig.y, rot);
			y = orig.y + CompuFuncs.RotateY(x - orig.x, y - orig.y, rot);
		} else {
			x = CompuFuncs.RotateX(x, y, rot);
			y = CompuFuncs.RotateY(x, y, rot);
		}
	}
	
	private Rect tmpRect;
	public Rect toRect()
	{
		tmpRect = new Rect((int)-x / 2, (int)-y / 2, (int)x / 2, (int)y / 2);
		return tmpRect;
	}
	public Rect toRectCache()
	{
		if(tmpRect == null) tmpRect = new Rect((int)-x / 2, (int)-y / 2, (int)x / 2, (int)y / 2);
		return tmpRect;
	}
	
	private RectF tmpRectF;
	public RectF toRectF()
	{
		tmpRectF = new RectF((float)-x / 2, (float)-y / 2, (float)x / 2, (float)y / 2);
		return tmpRectF;
	}
	public RectF toRectFCache()
	{
		if(tmpRectF == null) tmpRectF = new RectF((float)-x / 2, (float)-y / 2, (float)x / 2, (float)y / 2);
		return tmpRectF;
	}
}