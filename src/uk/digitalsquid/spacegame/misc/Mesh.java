package uk.digitalsquid.spacegame.misc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Mesh is a base class for 3D objects making it easier to create and maintain
 * new primitives.<br />
 * 
 * Based off work by Per-Erik Bergman (per-erik.bergman@jayway.com)
 * @author william
 * 
 */
public class Mesh {
	/**
	 * The vertex buffer - points of mesh
	 */
	private FloatBuffer verticesBuffer = null;

	/**
	 * The index buffer - order to draw triangles
	 */
	private ShortBuffer indicesBuffer = null;

	// Our UV texture buffer.
	private FloatBuffer textureBuffer;

	// Our texture id.
	private int textureId = -1;

	// The number of indices.
	private int numOfIndices = -1;

	// Flat Color
	private final float[] mRGBA = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// Translate params.
	public float x = 0;

	public float y = 0;

	// Rotate params.
	public float rz = 0;
	
	/**
	 * Constructs a {@link Mesh} with a colour
	 * @param vertices
	 * @param indices
	 * @param textureCoords
	 * @param resId
	 */
	public Mesh(float x, float y, float[] vertices, short[] indices, float r, float g, float b) {
		if(vertices == null) throw new IllegalArgumentException("vertices is null");
		if(indices == null) throw new IllegalArgumentException("indices is null");
		setVertices(vertices);
		setIndices(indices);
		setColor(r, g, b, 1);
	}

	/**
	 * Constructs a {@link Mesh} with a bitmap
	 * @param vertices
	 * @param indices
	 * @param textureCoords
	 * @param resId
	 */
	public Mesh(float x, float y, float[] vertices, short[] indices, float[]textureCoords, int resId) {
		if(vertices == null) throw new IllegalArgumentException("vertices is null");
		if(indices == null) throw new IllegalArgumentException("indices is null");
		if(textureCoords == null) throw new IllegalArgumentException("texturecoords is null");
		setVertices(vertices);
		setIndices(indices);
		setTextureCoordinates(textureCoords);
		textureId = TextureManager.getTexture(resId);
		this.resId = resId;
	}
	int resId;

	/**
	 * Render the mesh.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	public void draw(GL10 gl) {
		gl.glPushMatrix();
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		// Specifies the location and data format of an array of vertex
		// coordinates to use when rendering.
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);
		// Set flat color
		gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);

		if (textureId != -1 && textureBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		}

		gl.glTranslatef(x, y, 0);
		gl.glRotatef(rz++, 0, 0, 1);

		// Point out the where the color buffer is.
		gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices,
				GL10.GL_UNSIGNED_SHORT, indicesBuffer);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		if (textureId != -1 && textureBuffer != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
		
		gl.glPopMatrix();
	}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		verticesBuffer = vbb.asFloatBuffer();
		verticesBuffer.put(vertices);
		verticesBuffer.position(0);
	}

	/**
	 * Set the indices.
	 * 
	 * @param indices
	 */
	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indicesBuffer = ibb.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numOfIndices = indices.length;
	}

	/**
	 * Set the texture coordinates.
	 * 
	 * @param textureCoords
	 */
	protected void setTextureCoordinates(float[] textureCoords) { // New
																	// function.
		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		ByteBuffer byteBuf = ByteBuffer
				.allocateDirect(textureCoords.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		textureBuffer = byteBuf.asFloatBuffer();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);
	}

	/**
	 * Set one flat color on the mesh.
	 * 
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	protected void setColor(float red, float green, float blue, float alpha) {
		mRGBA[0] = red;
		mRGBA[1] = green;
		mRGBA[2] = blue;
		mRGBA[3] = alpha;
	}
	
	public void setAlpha(float alpha) {
		mRGBA[3] = alpha;
	}
}
