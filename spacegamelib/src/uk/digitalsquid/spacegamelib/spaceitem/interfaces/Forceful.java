package uk.digitalsquid.spacegamelib.spaceitem.interfaces;

import org.jbox2d.common.Vec2;

/**
 * Allows a SpaceItem class to send back velocity and Force data to loop
 * @author william
 *
 */
public interface Forceful
{
	/**
	 * Calculate the force that this item puts upon the ball.
	 * @param itemC	The item's position
	 * @return		The force created
	 */
	public Vec2 calculateRF(Vec2 itemC);
	
	/**
	 * Calculate the new velocity of the ball, ie if it has collided with something,
	 * or if it is slowing down.
	 * @param itemPos			The ball's current position
	 * @param itemV		The ball's current velocity
	 * @param itemRadius	The ball's radius
	 * @return				The ball's new velocity
	 */
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius);
	
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV, float itemRadius);
	
}
