package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import android.util.Pair;

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

	/**
	 * Notifies the activity / fragment that the user has made a pairing choice
	 * and that it is time to move on.
	 * @param correct
	 * @param incorrect {@link Pair}s of {@link Contact} and the {@link Contact}
	 * they were mistaken for.
	 * @param timeout
	 * @param timeTaken
	 */
	void pairingChoiceMade(
			ArrayList<Contact> correct,
			ArrayList<Pair<Contact, Contact>> incorrect,
			ArrayList<Contact> timeout,
			float timeTaken);
	
	void setGamePaused(boolean paused);
	
	void dataErrorFound(ArrayList<DataItem> possibleErrors);
}
