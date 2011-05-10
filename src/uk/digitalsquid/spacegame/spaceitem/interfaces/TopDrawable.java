package uk.digitalsquid.spacegame.spaceitem.interfaces;

import javax.microedition.khronos.opengles.GL10;

public interface TopDrawable
{
	/**
	 * Draw parts of this object onto the screen that are drawn on top of the ball
	 * @param gl		The GL to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void drawTop(GL10 gl, float worldZoom);
}
