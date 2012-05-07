package uk.digitalsquid.spacegame.spaceitem.assistors;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.levels.LevelItem;
import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.PlayerBase;
import uk.digitalsquid.spacegame.spaceitem.items.Portal;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;

public final class Simulation implements Constants {
	
	public Simulation() {
	}
	
	public static final int ITERS = 1;
	public static final int SPEED_SCALE = 20;

	/**
	 * Sets a temporary multiplier for the gravity; this is used when firing the ball, 
	 * to improve gameplay slightly by giving the character a 'head start' on gravity.
	 * If only NASA knew about this.
	 */
	public float gravityEffectMultiplier = 1;
	
	Lines debugForceLines = DEBUG ? new Lines(0, 0, 2, GL10.GL_LINES, 1, 0, 0, 1) : null;
	Lines debugRFLine = DEBUG ? new Lines(0, 0, 2 * 3, GL10.GL_LINES, 0, 1, 0, 1) : null;
	
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
	public void calculate(final SimulationContext context, final LevelItem level, final PlayerBase p, final Portal portal, final LaunchingMechanism launch, boolean paused, boolean gravOn, final int millistep) {
		final List<SpaceItem> planetList = level.planetList;
		
		for(int iter = 0; iter < ITERS; iter++) // Main physics loop
		{
			p.itemRF.setZero();
			p.apparentRF.setZero();
			/**
			 * When true, indicates that something has an exclusive hold on the force, and that nothing else should apply force.
			 */
			boolean exclusiveForce = false;
			int count = 0;
			for(SpaceItem obj : planetList) {
				if(!paused) {
					if(obj instanceof Forceful) {
						Forceful item = (Forceful) obj;
						if(gravOn && !exclusiveForce) /* Stage for gravity forces */ {
							if(item.isForceExclusive()) {
								exclusiveForce = true;
								p.itemRF.setZero(); // Makes this force the only one.
							}
							Vec2 tmp = item.calculateRF(p.itemC, p.getVelocity());
							if(tmp != null) {
								if(DEBUG) {
									if(debugForceLines.getVertices().capacity() / 3 / 2 <= count) { // 3 dimensions, 2 points
										debugForceLines.setVertices((count + 1) * 2); // 1 more
									}
									debugForceLines.getVertices().put(count * 3 * 2 + 3, tmp.x * 1); // Second point, x and y
									debugForceLines.getVertices().put(count * 3 * 2 + 4, tmp.y * 1);
								}
								p.itemRF.addLocal(tmp);
								count++;
							}
						}
						
						// Stage for velocity changes
						Vec2 data = item.calculateVelocityImmutable(p.itemC, p.getVelocity(), AnimatedPlayer.BALL_RADIUS);
						if(data != null) {
							if(data != null && !paused) {
								p.setVelocity(data);
							}
						}
						item.calculateVelocityMutable(p.itemC, p.getVelocity(), AnimatedPlayer.BALL_RADIUS);
					}
				}
			}
			
			if(DEBUG) {
				debugRFLine.getVertices().put(3, p.itemRF.x * 1);
				debugRFLine.getVertices().put(4, p.itemRF.y * 1);
				
				debugRFLine.setXY(p.getPosX(), p.getPosY());
				debugForceLines.setXY(p.getPosX(), p.getPosY());
			}
			
			p.apparentRF.set(p.itemRF); // Duplicate - different from here on
			
			if(gravOn)
			{
				if(portal != null) {
					if(portal.isForceExclusive()) {
						exclusiveForce = true;
						p.itemRF.setZero(); // Makes this force the only one.
						p.apparentRF.setZero();
					}
					Vec2 tmp = portal.calculateRF(p.itemC, p.getVelocity());
					if(tmp != null) p.itemRF.addLocal(tmp);
					if(tmp != null) p.apparentRF.addLocal(tmp);
				}
				if(launch != null) {
					if(launch.isForceExclusive()) {
						exclusiveForce = true;
						p.itemRF.setZero(); // Makes this force the only one.
						p.apparentRF.setZero();
					}
					Vec2 tmp = launch.calculateRF(p.itemC, p.getVelocity());
					if(tmp != null) tmp.mulLocal(10);
					if(tmp != null) p.itemRF.addLocal(tmp);
					if(tmp != null) p.apparentRF.subLocal(tmp);
				}
			}
			
			portal.calculateVelocityImmutable(p, Player.BALL_RADIUS);
			portal.calculateVelocityMutable(p, Player.BALL_RADIUS);
			
			if(!paused) {
				gravityEffectMultiplier = CompuFuncs.trimMinMax(gravityEffectMultiplier, -0.1f, 1);
				gravityEffectMultiplier = (gravityEffectMultiplier - 1) * 0.99f + 1; // Slowly reset to 1
				p.itemRF.mulLocal(gravityEffectMultiplier);
				
				p.itemRF.mulLocal(.5f);
				
				p.getBody().applyForce(p.itemRF, p.itemC);
				
				context.world.step(millistep / ITERS / 1000f * SPEED_SCALE, 2, 2);
			}
		}
	}
	
	public void debugDraw(GL10 gl) {
		if(DEBUG) {
			debugForceLines.draw(gl);
			debugRFLine.draw(gl);
		}
	}
}
