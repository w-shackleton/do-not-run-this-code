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
public class Coord implements Serializable
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
	
	private static Matrix mtrix = new Matrix();
	
	public Coord(double x, double y, Matrix matrix)
	{
		float[] nums = new float[]{(float) x, (float) y};
		synchronized(mtrix)
		{
			matrix.invert(mtrix);
			mtrix.mapPoints(nums);
			this.x = nums[0];
			this.y = nums[1];
		}
	}
	
	public Coord scale(float scaleFactor)
	{
		return new Coord(x * scaleFactor, y * scaleFactor);
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
		if(orig == null) orig = new Coord();
		return new Coord(
				orig.x + CompuFuncs.RotateX(x - orig.x, y - orig.y, rot),
				orig.y + CompuFuncs.RotateY(x - orig.x, y - orig.y, rot));
	}
	
	public Rect toRect()
	{
		return new Rect((int)-x, (int)-y, (int)x, (int)y);
	}
	
	public RectF toRectF()
	{
		return new RectF((float)-x, (float)-y, (float)x, (float)y);
	}
}