package uk.digitalsquid.contactrecall.ingame.gl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Positions and draws a set of answers to the screen.
 * TODO: Needs improved layout logic
 * @author william
 *
 * @param <T> The type of the answer to be shown
 */
public class AnswerCollection<T extends Positionable> extends ArrayList<T> {

	private static final long serialVersionUID = 1237933580898961905L;
	
	/**
	 * Amount of padding on each answer.
	 */
	static final float ANSWER_PADDING = 1;
	
	/**
	 * Repositions the children in the given area
	 * @param cx The centre to reposition about
	 * @param cy The centre to reposition about
	 * @param width
	 * @param height
	 */
	public void repositionAnswers(float cx, float cy, float width, float height) {
		// TODO: Check this layout logic onscreen
		final int size = size();
		final int rows = (int) Math.round(Math.sqrt(size)); // Rough idea of how many rows to show
		final int maxCols = (int) Math.ceil((float)size / (float)rows);
		
		int count = 0;
		// TODO: Deal with padding etc. Perhaps don't strictly use width & height (be more lenient)?
		// Counts across and up, positioning each element in a reasonable place.
		for(int y = 0; y < rows; y++) {
						// Start at bottom,		increment in steps
			final float centreY = cy - height / 2 + (height * (float)y / rows);
			final int countAcross = Math.min(maxCols, size - maxCols * y); // Number of things in this row.
			for(int x = 0; x < countAcross; x++) {
				//														.5 to move to centre - half a block
				final float centreX = cx - width / 2 + (width * (float)(x+.5f) / countAcross);
				get(count++).setXYZ(centreX, centreY, 0);
			}
		}
	}
	
	public void draw(GL10 gl) {
		for(T t : this) {
			t.draw(gl);
		}
	}
}
