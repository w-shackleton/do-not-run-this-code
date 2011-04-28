package uk.digitalsquid.spacegame.spaceitem.interfaces;

import javax.microedition.khronos.opengles.GL10;

public interface StaticDrawable
{
	/**
	 * Draw parts of this object onto the screen that are drawn statically.
	 * @param gl		The GL to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void drawStatic(GL10 gl, final int width, final int height);
}
