package uk.digitalsquid.spacegame.spaceitem.assistors;

import uk.digitalsquid.spacegame.gl.Points;

public final class BgPoints extends Points {
	private static final int VISUAL_DEPTH = 100;

	/**
	 * 
	 * @param numOfPoints Number of points to create
	 * @param insideWidth Diameter of area to place points
	 * @param insideHeight Diameter of area to place points
	 */
	public BgPoints(int numOfPoints, int insideWidth, int insideHeight) {
		super(0, 0, genNewVertices(numOfPoints, insideWidth, insideHeight), 1, 1, 1, 1);
	}

	/**
	 * Generates a random set of points for the BG
	 * @return
	 */
	private static final float[] genNewVertices(int numOfPoints, int w, int h) {
		final float[] p = new float[numOfPoints * 3];
		
		for(int i = 0; i < p.length;) {
			p[i++] = (float) (Math.random() * w - (w / 2));
			p[i++] = (float) (Math.random() * h - (h / 2));
			p[i++] = (float) (Math.random() * -VISUAL_DEPTH);
		}
		
		return p;
	}
}
