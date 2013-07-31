package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.mgr.details.Contact;

/**
 * Callback functions from the game itself to the fragment that is hosting it.
 * @author william
 *
 */
public interface GameCallbacks {
	/**
	 * Notifies the activity / fragment that the user has made a choice
	 * and that it is time to move on.
	 * @param choice The number of the choice made TODO may not be used
	 * @param correct If <code>true</code>, the choice was correct
	 */
	void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken);
	
	void setGamePaused(boolean paused);
}
