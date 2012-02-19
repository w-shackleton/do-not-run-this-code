package uk.digitalsquid.spacegamelib.gl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/*
 * Vertex order:
 * 				 5
 * 			   4		<- an arc
 * 				2  3
 * (centre)		0	1
 */

/**
 * An arc in a circle that maps a texture around the curve
 * @author william
 *
 */
public final class Arc extends Mesh {
	
	public static final int SECTIONS = 10;
	/**
	 * Nr of sections in the mesh + 1 - this copes with there being lines on both sides
	 */
	public static final int EDGES = SECTIONS + 1;
	public static final int VERTICES = EDGES * 2;

	/**
	 * After constructing you should call the setSize method to set the actual size and position of this object
	 * @param x The origin of the arc
	 * @param y The origin of the arc
	 * @param resId
	 */
	public Arc(float x, float y, int resId) {
		super(x, y, new float[VERTICES * 3], genIndices(SECTIONS), genTextureCoords(VERTICES), resId);
	}
	/**
	 * After constructing you should call the setSize method to set the actual size and position of this object
	 * @param x The origin of the arc
	 * @param y The origin of the arc
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public Arc(float x, float y, float r, float g, float b, float a) {
		super(x, y, new float[VERTICES * 3], genIndices(SECTIONS), r, g, b, a);
	}
	
	private static final float[] genTextureCoords(final int vertices) {
		final float[] ret = new float[vertices * 2];
		for(int i = 0; i < ret.length; i += 4) {
			ret[i+0] = (float)i / (float)(vertices - 1) / 2;
			ret[i+2] = (float)i / (float)(vertices - 1) / 2;
			
			ret[i+1] = 1;
			ret[i+3] = 0;
		}
		return ret;
	}
	
	private static final short[] genIndices(final int sections) {
		final short[] ret = new short[sections * 2 * 3];
		/*
		 * Order is 0 1 2, 1 3 2,
		 * 			2 3 4, 3 5 4
		 */
		int startPos = 0;
		for(int i = 0; i < ret.length; i += 6) {
			ret[i+0] = (short) (startPos + 0);
			ret[i+1] = (short) (startPos + 1);
			ret[i+2] = (short) (startPos + 2);
			
			ret[i+3] = (short) (startPos + 1);
			ret[i+4] = (short) (startPos + 3);
			ret[i+5] = (short) (startPos + 2);
			
			startPos += 2; // Next stage
		}
		return ret;
	}
	
	/**
	 * Sets the {@link Arc}'s size and angles
	 * @param minRadius
	 * @param maxRadius
	 * @param startAngle The start angle in RADIANS
	 * @param endAngle The end angle in RADIANS
	 */
	public void setSize(final float minRadius, final float maxRadius, final float startAngle, final float endAngle) {
		if(endAngle > startAngle) setWinding(GL10.GL_CCW); // Normal
		else setWinding(GL10.GL_CW); // Reversed
		
		FloatBuffer vert = getVertices();
		
		for(int i = 0; i < EDGES; i++) {
			final float angle = (float)i / (float)(EDGES-1) * (endAngle - startAngle) + startAngle;
			vert.put(i*6+0, minRadius * (float)Math.cos(angle));
			vert.put(i*6+1, minRadius * (float)Math.sin(angle));
			vert.put(i*6+2, 0); // Is this needed?
			
			vert.put(i*6+3, maxRadius * (float)Math.cos(angle));
			vert.put(i*6+4, maxRadius * (float)Math.sin(angle));
			vert.put(i*6+5, 0);
		}
	}
}
