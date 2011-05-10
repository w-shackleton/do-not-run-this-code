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
	
	private Coord[] tmpRectPos;
	/**
	 * Get coordinates of the four corners of this rectangle
	 * @return An array of four coordinates
	 */
	protected Coord[] getRectPos()
	{
		if(tmpRectPos == null) {
			tmpRectPos = new Coord[4];
			tmpRectPos[0] = new Coord();
			tmpRectPos[1] = new Coord();
			tmpRectPos[2] = new Coord();
			tmpRectPos[3] = new Coord();
		}
		tmpRectPos[0].x = pos.x + CompuFuncs.RotateX(-size.x / 2, -size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[0].y = pos.y + CompuFuncs.RotateY(-size.x / 2, -size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[1].x = pos.x + CompuFuncs.RotateX(+size.x / 2, -size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[1].y = pos.y + CompuFuncs.RotateY(+size.x / 2, -size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[2].x = pos.x + CompuFuncs.RotateX(+size.x / 2, +size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[2].y = pos.y + CompuFuncs.RotateY(+size.x / 2, +size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[3].x = pos.x + CompuFuncs.RotateX(-size.x / 2, +size.y / 2, rotation * DEG_TO_RAD);
		tmpRectPos[3].y = pos.y + CompuFuncs.RotateY(-size.x / 2, +size.y / 2, rotation * DEG_TO_RAD);
		/*Log.v("SpaceGame", "0: " + ret[0]);
		Log.v("SpaceGame", "1: " + ret[1]);
		Log.v("SpaceGame", "2: " + ret[2]);
		Log.v("SpaceGame", "3: " + ret[3]);*/
		return tmpRectPos;
	}
	
	private Rect tmpRect;
	/**
	 * Returns a {@link Rect} describing this {@link Rectangular}
	 */
	protected Rect getRect()
	{
		if(tmpRect == null) {
			tmpRect = new Rect(
					(int)(pos.x - (size.x / 2)),
					(int)(pos.y - (size.y / 2)),
					(int)(pos.x + (size.x / 2)),
					(int)(pos.y + (size.y / 2)));
		}
		tmpRect.left = (int)(pos.x - (size.x / 2));
		tmpRect.top = (int)(pos.y - (size.y / 2));
		tmpRect.right = (int)(pos.x + (size.x / 2));
		tmpRect.bottom = (int)(pos.y + (size.y / 2));
		return tmpRect;
	}

	private RectF tmpRectF;
	/**
	 * Returns a {@link RectF} describing this {@link Rectangular}
	 */
	protected RectF getRectF()
	{
		if(tmpRectF == null) {
			tmpRectF = new RectF(
					(float)(pos.x - (size.x / 2)),
					(float)(pos.y - (size.y / 2)),
					(float)(pos.x + (size.x / 2)),
					(float)(pos.y + (size.y / 2)));
		}
		tmpRectF.left = (float)(pos.x - (size.x / 2));
		tmpRectF.top = (float)(pos.y - (size.y / 2));
		tmpRectF.right = (float)(pos.x + (size.x / 2));
		tmpRectF.bottom = (float)(pos.y + (size.y / 2));
		return tmpRectF;
	}
	
	@Override
	public boolean isClicked(float x, float y)
	{
		return CompuFuncs.PointInPolygon(getRectPos(), x, y);
	}
}
