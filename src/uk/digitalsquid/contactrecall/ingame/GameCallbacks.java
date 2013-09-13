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
	
	public static final int CHOICE_CORRECT = 1;
	public static final int CHOICE_INCORRECT = 2;
	public static final int CHOICE_TIMEOUT = 3;
	public static final int CHOICE_DISCARD = 4;

	/**
	 * Notifies the activity / fragment that the user has made a choice
	 * and that it is time to move on.
	 * @param choice The contact that was chosen (if one was)
	 * @param choiceType The type of choice. See {@link GameCallbacks}.CHOICE_..
	 * @param timeTaken The time taken, in seconds
	 */
	void choiceMade(Contact choice, int choiceType, float timeTaken);

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
	
	/**
	 * Sets the logical status of the game to paused
	 * @param paused
	 */
	void setGamePaused(boolean paused);
	
	/**
	 * Actually pauses the game, logically and visually
	 */
	void pauseGame();
	
	void dataErrorFound(ArrayList<DataItem> possibleErrors);
}
