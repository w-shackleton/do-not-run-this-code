package uk.digitalsquid.spacegamelib;

import org.jbox2d.dynamics.World;

import android.content.Context;
import android.content.ContextWrapper;

/**
 * Various different contexts when creating elements.
 * @author william
 *
 */
public final class SimulationContext extends ContextWrapper {
	public final Context context;
	public final World world;
	
	public SimulationContext(Context context, World world) {
		super(context);
		this.context = context;
		this.world = world;
	}
}
