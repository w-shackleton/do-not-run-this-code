package uk.digitalsquid.spacegame.spaceitem.assistors;

import java.util.List;

import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.PlayerBase;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegame.spaceitem.items.Tether;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;

public final class Simulation {
	
	private final SimulationCallbackListener callbacks;
	
	public Simulation() {
		this(null);
	}
	
	public Simulation(SimulationCallbackListener callbacks) {
		if(callbacks == null)
			this.callbacks = new SimulationCallbackListener() {
				@Override
				public void wallBounced(float amount) { }
				@Override
				public void onStop() { }
			};
		else
			this.callbacks = callbacks;
	}
	
	public static final int ITERS = 1;
	public static final int SPEED_SCALE = 20;

	/**
	 * Sets a temporary multiplier for the gravity; this is used when firing the ball, 
	 * to improve gameplay slightly by giving the character a 'head start' on gravity.
	 * If only NASA knew about this.
	 */
	public float gravityEffectMultiplier = 1;
		
	/**
	 * 
	 * @param level
	 * @param p
	 * @param portal
	 * @param tether
	 * @param paused
	 * @param gravOn
	 * @param millistep
	 */
	public void calculate(final SimulationContext context, final LevelItem level, final PlayerBase p, final Portal portal, final Tether tether, boolean paused, boolean gravOn, final int millistep) {
		final List<SpaceItem> planetList = level.planetList;
		
		for(int iter = 0; iter < ITERS; iter++) // Main physics loop
		{
			p.itemRF.setZero();
			p.apparentRF.setZero();
			for(SpaceItem obj : planetList)
			{
				if(!paused)
				{
					if(obj instanceof Forceful)
					{
						Forceful item = (Forceful) obj;
						if(gravOn) // Stage for gravity forces
						{
							Vec2 tmp = item.calculateRF(p.itemC);
							if(tmp != null) p.itemRF.addLocal(tmp);
						}
						
						// Stage for velocity changes
						Vec2 data = item.calculateVelocityImmutable(p.itemC, p.getVelocity(), AnimatedPlayer.BALL_RADIUS);
						if(data != null)
						{
							if(data != null)
								p.setVelocity(data);
						}
						item.calculateVelocityMutable(p.itemC, p.getVelocity(), AnimatedPlayer.BALL_RADIUS);
					}
				}
			}
			
			p.apparentRF.set(p.itemRF); // Duplicate - different from here on
			
			if(gravOn)
			{
				if(portal != null) {
					Vec2 tmp = portal.calculateRF(p.itemC);
					if(tmp != null) p.itemRF.addLocal(tmp);
					if(tmp != null) p.apparentRF.addLocal(tmp);
				}
				if(tether != null) {
					Vec2 tmp = tether.calculateRF(p.itemC);
					if(tmp != null) p.itemRF.addLocal(tmp);
					if(tmp != null) p.apparentRF.subLocal(tmp);
				}
			}
			
			portal.calculateVelocityImmutable(p, Player.BALL_RADIUS);
			portal.calculateVelocityMutable(p, Player.BALL_RADIUS);
			
			if(!paused)
			{
				gravityEffectMultiplier = CompuFuncs.TrimMinMax(gravityEffectMultiplier, -0.1f, 1);
				gravityEffectMultiplier = (gravityEffectMultiplier - 1) * 0.99f + 1; // Slowly reset to 1
				p.itemRF.mulLocal(gravityEffectMultiplier);
				
				p.itemRF.mulLocal(.005f);
				
				p.getBody().applyForce(p.itemRF, p.itemC);
				// p.getBody().setLinearVelocity(p.getBody().getLinearVelocity().addLocal(p.itemVC));
				
				context.world.step(millistep / ITERS / 1000f * SPEED_SCALE, 1, 1);
			}
		}
	}

	public static interface SimulationCallbackListener {
		void wallBounced(float amount);
		void onStop();
	}
}
