package uk.digitalsquid.spacegame.misc;

public class RectMesh extends Mesh {
	
	private static final short[] indices = {0, 1, 2, 1, 3, 2};
	private static final float[] texCoords = 
					{0.0f, 1.0f,
	                1.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f };

	/**
	 * Constructs a new rectangular mesh with a colour.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 */
	public RectMesh(float x, float y, float width, float height, float r, float g, float b, float a) {
		super(x, y,
				new float[] {
					-width * 0.5f, -height * 0.5f, 0.0f,
					 width * 0.5f, -height * 0.5f, 0.0f,
					-width * 0.5f,  height * 0.5f, 0.0f,
					 width * 0.5f,  height * 0.5f, 0.0f, },
					 indices.clone(),
                r, g, b, a);
	}
	
	/**
	 * Constructs a new rectangular mesh from an image
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param resId
	 */
	public RectMesh(float x, float y, float width, float height, int resId) {
		super(x, y,
				new float[] {
					-width * 0.5f, -height * 0.5f, 0.0f,
					 width * 0.5f, -height * 0.5f, 0.0f,
					-width * 0.5f,  height * 0.5f, 0.0f,
					 width * 0.5f,  height * 0.5f, 0.0f, },
					indices.clone(),
	                texCoords.clone(), resId);
	}
	
	/**
	 * Constructs a new rectangular mesh from an image, with texture relative width & height.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param texW
	 * @param texH
	 * @param resId
	 */
	public RectMesh(float x, float y, float width, float height, float texW, float texH, int resId) {
		super(x, y,
				new float[] {
					-width * 0.5f, -height * 0.5f, 0.0f,
					 width * 0.5f, -height * 0.5f, 0.0f,
					-width * 0.5f,  height * 0.5f, 0.0f,
					 width * 0.5f,  height * 0.5f, 0.0f, },
		        indices,
		        new float[] {
					0.0f, texH,
	                texW, texH,
                    0.0f, 0.0f,
                    texW, 0.0f }, resId);
	}
}
