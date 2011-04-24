package uk.digitalsquid.spacegame.misc;

public class RectMesh extends Mesh {

	public RectMesh(float x, float y, float width, float height, float r, float g, float b) {
		super(x, y,
				new float[] {
					-width * 0.5f, -height * 0.5f, 0.0f,
					 width * 0.5f, -height * 0.5f, 0.0f,
					-width * 0.5f,  height * 0.5f, 0.0f,
					 width * 0.5f,  height * 0.5f, 0.0f, },
		        new short[] {0, 1, 2, 1, 3, 2},
                r, g, b);
	}
	
	public RectMesh(float x, float y, float width, float height, int resId) {
		super(x, y,
				new float[] {
					-width * 0.5f, -height * 0.5f, 0.0f,
					 width * 0.5f, -height * 0.5f, 0.0f,
					-width * 0.5f,  height * 0.5f, 0.0f,
					 width * 0.5f,  height * 0.5f, 0.0f, },
		        new short[] {0, 1, 2, 1, 3, 2},
		        new float[] {
					0.0f, 1.0f,
	                1.0f, 1.0f,
                    0.0f, 0.0f,
                    1.0f, 0.0f }, resId);
	}
	
	public RectMesh(float x, float y, float width, float height, float texW, float texH, int resId) {
		super(x, y,
				new float[] {
					-width * 0.5f, -height * 0.5f, 0.0f,
					 width * 0.5f, -height * 0.5f, 0.0f,
					-width * 0.5f,  height * 0.5f, 0.0f,
					 width * 0.5f,  height * 0.5f, 0.0f, },
		        new short[] {0, 1, 2, 1, 3, 2},
		        new float[] {
					0.0f, texH,
	                texW, texH,
                    0.0f, 0.0f,
                    texW, 0.0f }, resId);
	}
}
