package uk.digitalsquid.spacegame.spaceitem.assistors;

import java.util.List;

import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful.BallData;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.PlayerBase;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegame.spaceitem.items.Tether;

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
	
	public static final int ITERS = 5;
	public static final float AIR_RESISTANCE = 1f; // 1 = no resistance, must NEVER be greater than 1
	public static final int SPEED_SCALE = 20;

	protected static final float WALL_BOUNCINESS = 0.8f;
	
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
	 * @param testRun Whether to run code that affects the game's state, namely the 'Mutable' functions
	 */
	public void calculate(final LevelItem level, final PlayerBase p, final Portal portal, final Tether tether, boolean paused, boolean gravOn, final int millistep, final boolean testRun) {
		final List<SpaceItem> planetList = level.planetList;
		
		for(int iter = 0; iter < ITERS; iter++) // Main physics loop
		{
			if(!paused)
			{
				// Check for collisions with wall
				if(p.itemC.x > level.bounds.x / 2 - AnimatedPlayer.BALL_RADIUS)
				{
					p.itemVC.x = -p.itemVC.x * WALL_BOUNCINESS;
					if(p.itemVC.x > 0) // Solves physics error
						p.itemVC.x = 0;
					callbacks.wallBounced((float) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE));
				}
				if(p.itemC.x < -(level.bounds.x / 2 - AnimatedPlayer.BALL_RADIUS))
				{
					p.itemVC.x = -p.itemVC.x * WALL_BOUNCINESS;
					if(p.itemVC.x < 0)
						p.itemVC.x = 0;
					callbacks.wallBounced((float) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE));
				}
				if(p.itemC.y > level.bounds.y / 2 - AnimatedPlayer.BALL_RADIUS)
				{
					p.itemVC.y = -p.itemVC.y * WALL_BOUNCINESS;
					if(p.itemVC.y > 0) // Solves physics error
						p.itemVC.y = 0;
					callbacks.wallBounced((float) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE));
				}
				if(p.itemC.y < -(level.bounds.y / 2 - AnimatedPlayer.BALL_RADIUS))
				{
					p.itemVC.y = -p.itemVC.y * WALL_BOUNCINESS;
					if(p.itemVC.y < 0)
						p.itemVC.y = 0;
					callbacks.wallBounced((float) (p.itemVC.getLength() / SpaceItem.ITEM_SCALE));
				}
			}
			
			p.itemRF.reset();
			for(SpaceItem obj : planetList)
			{
				if(!paused)
				{
					if(obj instanceof Forceful)
					{
						Forceful item = (Forceful) obj;
						if(gravOn) // Stage for gravity forces
						{
							p.itemRF.addThis(item.calculateRF(p.itemC, p.itemVC));
						}
						
						// Stage for velocity changes
						BallData data = item.calculateVelocityImmutable(p.itemC, p.itemVC, AnimatedPlayer.BALL_RADIUS, testRun);
						if(data != null)
						{
							if(data.itemC != null)
								p.itemC.copyFrom(data.itemC);
							if(data.itemVC != null)
								p.itemVC.copyFrom(data.itemVC);
							if(data.stopBall) {
								callbacks.onStop();
							}
						}
						if(!testRun) item.calculateVelocityMutable(p.itemC, p.itemVC, AnimatedPlayer.BALL_RADIUS);
					}
				}
			}
			
			if(gravOn)
			{
				if(portal != null)
					p.itemRF.addThis(portal.calculateRF(p.itemC, p.itemVC));
				if(tether != null)
					p.itemRF.addThis(tether.calculateRF(p.itemC, p.itemVC));
			}
			
			portal.calculateVelocityImmutable(p, Player.BALL_RADIUS, testRun);
			if(!testRun) portal.calculateVelocityMutable(p, Player.BALL_RADIUS);
			
			if(!paused)
			{
				gravityEffectMultiplier = CompuFuncs.TrimMinMax(gravityEffectMultiplier, -0.1f, 1);
				gravityEffectMultiplier = (gravityEffectMultiplier - 1) * 0.99f + 1; // Slowly reset to 1
				// Log.v("SpaceGame", "Grav is " + gravityEffectMultiplier);
				p.itemVC.x += p.itemRF.x * millistep / ITERS / 1000f * gravityEffectMultiplier;
				p.itemVC.y += p.itemRF.y * millistep / ITERS / 1000f;
				
				p.itemC.x  += p.itemVC.x * millistep / ITERS / 1000f * SPEED_SCALE;
				p.itemC.y  += p.itemVC.y * millistep / ITERS / 1000f * SPEED_SCALE;
				
				// Air resistance
				p.itemVC.scaleThis(AIR_RESISTANCE);
			}
		}
	}

	public static interface SimulationCallbackListener {
		void wallBounced(float amount);
		void onStop();
	}
}
