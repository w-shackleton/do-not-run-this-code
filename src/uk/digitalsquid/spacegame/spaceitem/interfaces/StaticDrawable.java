package uk.digitalsquid.spacegame.spaceitem.interfaces;

import android.graphics.Canvas;
import android.graphics.Matrix;

public interface StaticDrawable
{
	/**
	 * Draw parts of this object onto the screen that are drawn statically.
	 * @param c			The canvas to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void drawStatic(Canvas c, final float worldZoom, final int width, final int height, final Matrix matrix);
}
