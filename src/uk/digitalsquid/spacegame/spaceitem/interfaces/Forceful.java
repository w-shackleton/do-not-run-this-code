package uk.digitalsquid.spacegame.spaceitem.interfaces;

import uk.digitalsquid.spacegame.Coord;

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
	public Coord calculateRF(Coord itemC, Coord itemVC);
	
	/**
	 * Calculate the new velocity of the ball, ie if it has collided with something,
	 * or if it is slowing down.
	 * @param itemC			The ball's current position
	 * @param itemVC		The ball's current velocity
	 * @param itemRadius	The ball's radius
	 * @return				The ball's new velocity
	 */
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius);
	
	/**
	 * A simple class that contains both position and speed of the ball, as well as other data that may need to be returned to the main thread.
	 * @author william
	 *
	 */
	public static class BallData
	{
		public Coord itemC, itemVC;
		public boolean stopBall;
		
		public BallData(Coord itemC, Coord itemVC)
		{
			this.itemC = itemC;
			this.itemVC = itemVC;
		}
	}
}
