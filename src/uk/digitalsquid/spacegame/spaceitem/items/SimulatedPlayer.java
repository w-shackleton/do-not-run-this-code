package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.gl.Lines;
import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation;
import android.content.Context;

@Deprecated
public final class SimulatedPlayer extends PlayerBase {
	private final Simulation sim = new Simulation(null);
	
	private final LevelItem level;
	private final Portal portal;
	
	private final Lines lines;
	
	private final int numSteps;
	
	public SimulatedPlayer(final Context context, final LevelItem level, final Portal portal, final int numSteps) {
		super(context, new Coord(), BALL_RADIUS);
		this.level = level;
		this.portal = portal;
		this.lines = new Lines(0, 0, numSteps, GL10.GL_LINE_STRIP, 1, 1, 1, 1);
		this.numSteps = numSteps;
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		lines.draw(gl);
	}

	public final void simulateMove(final Coord start, final Coord velocity) {
		itemC.copyFrom(start);
		itemVC.copyFrom(velocity);
		
		final FloatBuffer buf = lines.getVertices();
		
		sim.gravityEffectMultiplier = -0.1f;
		
		for(int i = 0; i < numSteps; i++) {
			sim.calculate(level, this, portal, null, false, true, (int) (1000f / 60f), true);
			buf.put(i * 3 + 0, (float) itemC.x);
			buf.put(i * 3 + 1, (float) itemC.y);
		}
	}
}
