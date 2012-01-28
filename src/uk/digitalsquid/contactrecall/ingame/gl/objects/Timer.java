package uk.digitalsquid.contactrecall.ingame.gl.objects;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.gl.Mesh;
import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.Const;

/**
 * Shows a timer onscreen that shrinks round in a circle
 * @author william
 *
 */
public class Timer extends Mesh implements Config {
	
	static final int POINTS_ON_CIRCLE = 24;
	/**
	 * Indices for a circular triangle strip
	 */
	private static final short[] indices;
	static {
		// Initialise triangle indices
		indices = new short[(POINTS_ON_CIRCLE-1) * 3];
		for(int i = 0; i < POINTS_ON_CIRCLE - 1; i++) {
			indices[i*3+0] = 0;
			indices[i*3+1] = (short) (i + 1);
			indices[i*3+2] = (short) (i + 2);
		}
	}
	static final int VERTICES_COUNT = (POINTS_ON_CIRCLE + 1) * 3; // +1 for centre point
	static final int TEXCOORDS_COUNT = (POINTS_ON_CIRCLE + 1) * 2; // +1 for centre point
	
	private final float sx, sy;
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param sx The half-width
	 * @param sy The half-height
	 */
	public Timer(float x, float y, float sx, float sy) {
		super(x, y, new float[VERTICES_COUNT], indices, new float[TEXCOORDS_COUNT], R.drawable.timer);
		// super(x, y, new float[VERTICES_COUNT], indices, 1, 0, 0, 1);
		this.sx = sx;
		this.sy = sy;
		setDrawMode(GL10.GL_TRIANGLE_STRIP);
		setProgress(0.0f);
		setRotation(90);
	}
	
	/**
	 * Set the progress of the timer, where 0 is a full circle and 1 is an empty one.
	 * @param progress
	 */
	public void setProgress(float progress) {
		/**
		 * The amount of circle to show
		 */
		float show = 1-progress;
		FloatBuffer vertices = getVertices();
		FloatBuffer texCoords = getTextureCoordinates();
		if(texCoords != null) {
			texCoords.put(0, 0.5f);
			texCoords.put(1, 0.5f);
		}
		for(int i = 1; i < POINTS_ON_CIRCLE + 1; i++) { // Set each except centre which is always (0, 0, 0)
			float wayRound = (float)(i-1) / (float)(POINTS_ON_CIRCLE - 1); // -1 to fill in to 360
			vertices.put(i*3+0, (float)Math.cos(wayRound * show * Const.TAU) * sx);
			vertices.put(i*3+1, (float)Math.sin(wayRound * show * Const.TAU) * sy);
			vertices.put(i*3+2, 0);
			if(texCoords != null) {
				texCoords.put(i*2+0, (float)Math.cos(wayRound * show * Const.TAU) / 2f + 0.5f);
				texCoords.put(i*2+1, (float)Math.sin(wayRound * show * Const.TAU) / 2f + 0.5f);
			}
		}
	}
}
