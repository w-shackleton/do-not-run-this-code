package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.Coord;
import android.content.Context;
import android.graphics.Canvas;

public abstract class SpaceItem
{
	public static final float ITEM_SCALE = 1f;
	
	/**
	 * Multiply a number of degrees by this to convert it to radians
	 */
	protected static final float DEG_TO_RAD = (float) (Math.PI / 180);
	
	/**
	 * Multiply a number of radians by this to convert it to degrees
	 */
	protected static final float RAD_TO_DEG = (float) (180 / Math.PI);
	
	protected Context context;
	
	/**
	 * The position of this item
	 */
	protected Coord pos;
	
	public SpaceItem(Context context, Coord coord)
	{
		this.context = context;
		this.pos = coord.scale(ITEM_SCALE);
	}
	
	/**
	 * Draw this object onto the screen
	 * @param c			The canvas to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void draw(Canvas c, float worldZoom);
}
