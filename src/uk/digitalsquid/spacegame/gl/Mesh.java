package uk.digitalsquid.spacegame.gl;

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
	private float x = 0;

	private float y = 0;

	// Rotate params.
	private float rz = 0;
	
	/**
	 * Constructs a {@link Mesh} with a colour
	 * @param vertices
	 * @param indices
	 * @param textureCoords
	 * @param resId
	 */
	public Mesh(float x, float y, float[] vertices, short[] indices, float r, float g, float b, float a) {
		this.x = x;
		this.y = y;
		if(vertices == null) throw new IllegalArgumentException("vertices is null");
		if(indices == null) throw new IllegalArgumentException("indices is null");
		setVertices(vertices);
		setIndices(indices);
		setColour(r, g, b, a);
	}

	/**
	 * Constructs a {@link Mesh} with a bitmap
	 * @param vertices
	 * @param indices
	 * @param textureCoords
	 * @param resId
	 */
	public Mesh(float x, float y, float[] vertices, short[] indices, float[]textureCoords, int resId) {
		this.x = x;
		this.y = y;
		if(vertices == null) throw new IllegalArgumentException("vertices is null");
		if(indices == null) throw new IllegalArgumentException("indices is null");
		if(textureCoords == null) throw new IllegalArgumentException("texturecoords is null");
		setVertices(vertices);
		setIndices(indices);
		setTextureCoordinates(textureCoords);
		this.resId = resId;
	}
	private int resId = -1;

	/**
	 * Render the mesh.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	public final void draw(GL10 gl) {
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
		gl.glEnable(GL10.GL_BLEND); // Only if alpha present?
	    gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	    
		gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
		
		if(resId != -1 && textureId == -1) textureId = TextureManager.getTexture(gl, resId);

		if (textureId != -1 && textureBuffer != null) {
			
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		}

		gl.glTranslatef(x, y, 0);
		gl.glRotatef(rz, 0, 0, 1);

		// Point out the where the color buffer is.
		gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices,
				GL10.GL_UNSIGNED_SHORT, indicesBuffer);
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		if (textureId != -1 && textureBuffer != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
		
		internalDraw(gl);
		
		gl.glDisable(GL10.GL_BLEND);

		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
		
		gl.glRotatef(-rz, 0, 0, 1);
		gl.glTranslatef(-x, -y, 0);
	}
	
	/**
	 * Does any required drawing within the settings, rotation and transformation. Designed to be overridden.
	 * @param gl
	 */
	protected void internalDraw(GL10 gl) {}

	/**
	 * Set the vertices.
	 * 
	 * @param vertices
	 */
	protected final void setVertices(float[] vertices) {
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
	protected final void setIndices(short[] indices) {
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
	protected final void setTextureCoordinates(float[] textureCoords) {
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
	protected final void setColour(float red, float green, float blue, float alpha) {
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
	
	public final float getRotation() {
		return rz;
	}
	
	public final void setXY(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets a custom texture ID (not one through textureManager)
	 * @param texId
	 */
	protected final void setTextureId(int texId) {
		textureId = texId;
	}
}
