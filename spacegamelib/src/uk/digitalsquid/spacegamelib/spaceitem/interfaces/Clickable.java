package uk.digitalsquid.spacegamelib.spaceitem.interfaces;

import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;

/**
 * Allows a SpaceItem to be clicked. This interface defines the actual function containing what to do when the click occurs.
 * @author william
 *
 */
public interface Clickable extends IsClickable
{
	/**
	 * Called when the {@link SpaceItem} is clicked. 
	 */
	public void onClick();
}
