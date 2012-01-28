package uk.digitalsquid.contactrecall.game;

import java.util.List;

import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import android.util.Log;

/**
 * A specific instance of a certain game.
 * @author william
 *
 */
public abstract class GameInstance implements Config {
	/**
	 * The progress through this game so far.
	 */
	int progress = -1;

	public abstract List<Contact> getQuestions();
	
	/**
	 * Gets the next question contact. <code>null</code> indicates end of game.
	 * @return
	 */
	public Contact getNext() {
		Log.i(TAG, "Contact nr. " + (progress + 1));
		try {
			return getQuestions().get(++progress);
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	// Various different modes
	public static final int FROM_PHOTO = 0;
	
	public static final int TO_NAME = 0;
	
	public abstract int getFromMode();
	public abstract int getToMode();
	
	/**
	 * Gets the {@link Object} for the question part. What this should be is determined by the getFromMode result.
	 * Calls will be made only once per contact.
	 * @return
	 */
	public abstract Object getFromObject();
	/**
	 * Gets the {@link Object}s for the answers part. What this should be is determined by the getFromMode result.
	 * Calls will be made only once per contact.
	 * @return
	 */
	public abstract Object[] getToObjects();
	
	/**
	 * Returns the current question's position in the queue
	 * @return
	 */
	public int getProgress() {
		return progress;
	}
	
	/**
	 * Sets the position to the given place.
	 * @param position
	 */
	public void windTo(int position) {
		progress = position - 1; // -1 so next get gets this element.
	}
	
	public int size() {
		return getQuestions().size();
	}
}
