package uk.digitalsquid.spacegame.spaceitem.interfaces;

import android.graphics.Canvas;

public interface TopDrawable
{
	/**
	 * Draw parts of this object onto the screen that are drawn on top of the ball
	 * @param c			The canvas to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void drawTop(Canvas c, float worldZoom);
}
