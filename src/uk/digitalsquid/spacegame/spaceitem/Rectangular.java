package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.interfaces.IsClickable;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;

public abstract class Rectangular extends SpaceItem implements IsClickable
{
	/**
	 * The size of this rectangle
	 */
	protected Coord size;
	/**
	 * The rotation, in DEGREES of this rectangle
	 */
	protected float rotation;
	
	/**
	 * @param coord		Center position of the rectangle
	 * @param size		Size of the rectangle
	 * @param rotation	Rotation of rectangle, in DEGREES
	 */
	public Rectangular(Context context, Coord coord, Coord size, float rotation)
	{
		super(context, coord);
		this.size = size.scale(ITEM_SCALE);
		this.rotation = rotation;
	}
	
	/**
	 * Get coordinates of the four corners of this rectangle
	 * @return An array of four coordinates
	 */
	protected Coord[] getRectPos()
	{
		Coord[] ret = new Coord[4];
		ret[0] = new Coord(
				pos.x + CompuFuncs.RotateX(-size.x / 2, -size.y / 2, rotation * DEG_TO_RAD),
				pos.y + CompuFuncs.RotateY(-size.x / 2, -size.y / 2, rotation * DEG_TO_RAD));
		ret[1] = new Coord(
				pos.x + CompuFuncs.RotateX(+size.x / 2, -size.y / 2, rotation * DEG_TO_RAD),
				pos.y + CompuFuncs.RotateY(+size.x / 2, -size.y / 2, rotation * DEG_TO_RAD));
		ret[2] = new Coord(
				pos.x + CompuFuncs.RotateX(+size.x / 2, +size.y / 2, rotation * DEG_TO_RAD),
				pos.y + CompuFuncs.RotateY(+size.x / 2, +size.y / 2, rotation * DEG_TO_RAD));
		ret[3] = new Coord(
				pos.x + CompuFuncs.RotateX(-size.x / 2, +size.y / 2, rotation * DEG_TO_RAD),
				pos.y + CompuFuncs.RotateY(-size.x / 2, +size.y / 2, rotation * DEG_TO_RAD));
		/*Log.v("SpaceGame", "0: " + ret[0]);
		Log.v("SpaceGame", "1: " + ret[1]);
		Log.v("SpaceGame", "2: " + ret[2]);
		Log.v("SpaceGame", "3: " + ret[3]);*/
		return ret;
	}
	
	/**
	 * Returns a {@link Rect} describing this {@link Rectangular}
	 */
	protected Rect getRect()
	{
		return new Rect(
				(int)(pos.x - (size.x / 2)),
				(int)(pos.y - (size.y / 2)),
				(int)(pos.x + (size.x / 2)),
				(int)(pos.y + (size.y / 2)));
	}

	/**
	 * Returns a {@link RectF} describing this {@link Rectangular}
	 */
	protected RectF getRectF()
	{
		return new RectF(
				(float)(pos.x - (size.x / 2)),
				(float)(pos.y - (size.y / 2)),
				(float)(pos.x + (size.x / 2)),
				(float)(pos.y + (size.y / 2)));
	}
	
	@Override
	public boolean isClicked(Coord point)
	{
		return CompuFuncs.PointInPolygon(getRectPos(), point);
	}
}
