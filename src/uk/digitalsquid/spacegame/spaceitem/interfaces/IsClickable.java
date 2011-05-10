package uk.digitalsquid.spacegame.spaceitem.interfaces;


/**
 * Add to a subclass of SpaceItem to define code that calculates whether the object
 * has been clicked.
 * This interface allows the code to be split up and reused for objects of the same shape. 
 * @author william
 *
 */
public interface IsClickable
{
	/**
	 * Determines whether the object has been clicked.
	 * @param point The point in the game where the click occurred
	 * @return
	 */
	public boolean isClicked(float x, float y);
}
