package uk.digitalsquid.spacegamelib.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Mesh is a base class for 3D objects making it easier to create and maintain
 * new primitives.<br />
 * 
 * Based off work by Per-Erik Bergman (per-erik.bergman@jayway.com)
 * @author william
 * 
 */
public class Lines {
	/**
	 * The vertex buffer - points of mesh
	 */
	private FloatBuffer verticesBuffer = null;

	// Flat Color
	private final float[] mRGBA = new float[] { 1.0f, 1.0f, 1.0f, 0.0f };

	// Translate params.
	private float x = 0;

	private float y = 0;

	// Rotate params.
	private float rz = 0;
	
	private final int type;
	
	private int numOfVertices = -1;
	
	/**
	 * Constructs a set of lines from vertices
	 * @param x
	 * @param y
	 * @param vertices The vertices to use
	 * @param type Either {@link GL10}.GL_LINE_STRIP, {@link GL10}.GL_LINE_LOOP or {@link GL10}.GL_LINES.
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public Lines(float x, float y, float[] vertices, int type, float r, float g, float b, float a) {
		this.x = x;
		this.y = y;
		if(vertices == null) throw new IllegalArgumentException("vertices is null");
		
		switch(type) {
		case GL10.GL_LINE_LOOP:
		case GL10.GL_LINE_STRIP:
		case GL10.GL_LINES:
			break;
		default:
			throw new IllegalArgumentException("invalid type");
		}
		this.type = type;
		setVertices(vertices);
		setColour(r, g, b, a);
	}
	
	/**
	 * Constructs a set of lines from vertices
	 * @param x
	 * @param y
	 * @param vertices The vertices to use
	 * @param type Either {@link GL10}.GL_LINE_STRIP, {@link GL10}.GL_LINE_LOOP or {@link GL10}.GL_LINES.
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public Lines(float x, float y, int numVertices, int type, float r, float g, float b, float a) {
		this.x = x;
		this.y = y;
		
		switch(type) {
		case GL10.GL_LINE_LOOP:
		case GL10.GL_LINE_STRIP:
		case GL10.GL_LINES:
			break;
		default:
			throw new IllegalArgumentException("invalid type");
		}
		this.type = type;
		setVertices(numVertices);
		setColour(r, g, b, a);
	}

	/**
	 * Render the lines.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	public void draw(GL10 gl) {
		gl.glEnable(GL10.GL_POINT_SMOOTH);
		gl.glEnable(GL10.GL_LINE_SMOOTH);
		gl.glPointSize(2);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		// Set flat color
		// gl.glEnable(GL10.GL_BLEND); // Only if alpha present?
	    // gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
		gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
		
		gl.glTranslatef(x, y, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Point out the where the color buffer is.
		gl.glDrawArrays(type, 0, numOfVertices);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		
		gl.glDisable(GL10.GL_POINT_SMOOTH);
		gl.glDisable(GL10.GL_LINE_SMOOTH);

		// gl.glDisable(GL10.GL_BLEND);

		gl.glRotatef(-rz, 0, 0, 1);
		gl.glTranslatef(-x, -y, 0);
	}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	public final void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		numOfVertices = vertices.length / 3;
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}
	
	/**
	 * Create a blank set of vertices
	 * 
	 * @param vertices
	 */
	public final void setVertices(int numVertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		numOfVertices = numVertices;
		ByteBuffer vbb = ByteBuffer.allocateDirect(numVertices * 3 * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.position(0);
	}
	
	public final FloatBuffer getVertices() {
		return verticesBuffer;
	}

	/**
	 * Set one flat colour on the mesh.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public final void setColour(float red, float green, float blue, float alpha) {
		mRGBA[0] = red;
		mRGBA[1] = green;
		mRGBA[2] = blue;
		mRGBA[3] = alpha;
	}
	
	/**
	 * Sets the alpha, between 0 and 1
	 * @param alpha
	 */
	public final void setAlpha(float alpha) {
		mRGBA[3] = alpha;
	}
	
	public final float getAlpha() {
		return mRGBA[3];
	}
	
	public final void setRotation(float rotation) {
		rz = rotation;
	}
}
