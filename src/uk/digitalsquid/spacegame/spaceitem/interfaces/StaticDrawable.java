package uk.digitalsquid.spacegame.spaceitem.interfaces;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Matrix;

public interface StaticDrawable
{
	/**
	 * Draw parts of this object onto the screen that are drawn statically.
	 * @param gl		The GL to draw to
	 * @param inverseMatrix	The inverse matrix to use to translate points
	 */
	public abstract void drawStatic(GL10 gl, final int width, final int height, final Matrix matrix);
}
