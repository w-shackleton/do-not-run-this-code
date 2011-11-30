package uk.digitalsquid.contactrecall.game;

import java.util.List;

import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.Config;

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
	 * Returns the current question.
	 * @return
	 */
	public int getProgress() {
		return progress;
	}
	
	public int size() {
		return getQuestions().size();
	}
}
