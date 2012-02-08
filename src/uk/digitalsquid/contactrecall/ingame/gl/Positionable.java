package uk.digitalsquid.contactrecall.ingame.gl;

import javax.microedition.khronos.opengles.GL10;

/**
 * Something that can be positioned and rotated onscreen.
 * @author william
 *
 */
public interface Positionable {
	public void setXYZ(float x, float y, float z);
	/**
	 * Sets the X, Y, Z rotations in DEGREES
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRXYZ(float rx, float ry, float rz);
	
	public float getWidth();
	public float getHeight();
	
	// Here for convenience
	public void draw(GL10 gl);
}
