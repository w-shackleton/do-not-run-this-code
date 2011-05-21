package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import android.content.Context;

public class SimulatedPlayer extends PlayerBase {

	public SimulatedPlayer(Context context) {
		super(context, new Coord(), BALL_RADIUS);
	}

	@Override
	public void draw(GL10 gl, float worldZoom) { }

	public final void simulateMove(final FloatBuffer outFb, Coord start, Coord velocity, int steps) {
		
	}
}
