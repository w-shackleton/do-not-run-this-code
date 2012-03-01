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
	/**
	 * The last contact to be shown onscreen. Used to not have to reload the contact on screen rotate etc.
	 */
	int lastShown = -2;

	public abstract List<Contact> getQuestions();
	
	/**
	 * Gets the next question contact. <code>null</code> indicates end of game.
	 * @return
	 */
	public Contact getNext() {
		progress++;
		Log.i(TAG, "Contact nr. " + progress);
		if(lastShown == progress) { // Showing current one again, so don't do anything
			return getCurrent();
		} else {
			try {
				lastShown = (progress-1);
				Contact ret = getQuestions().get(progress);
				currentToData = generateToData(); // Make exception happen first
				return ret;
			} catch(IndexOutOfBoundsException e) {
				return null;
			}
		}
	}
	
	/**
	 * Gets the current question contact.
	 * @return
	 */
	public Contact getCurrent() {
		if(progress >= getQuestions().size()) return null;
		return getQuestions().get(progress);
	}
	
	// Various different modes
	public static final int FROM_PHOTO = 1;
	
	public static final int TO_NAME = 1;
	
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
	 * @return
	 */
	public List<Object> getToObjects() {
		return currentToData.objects;
	}
	
	/**
	 * Gets the {@link ToData} for the answers part. What this should be is determined by the getFromMode result.
	 * Calls will be made only once per contact.
	 * @return
	 */
	protected abstract ToData generateToData();
	
	private ToData currentToData;
	
	public static class ToData {
		private final List<Object> objects;
		/**
		 * The index in objects of the correct choice for this question
		 */
		private final int correctChoice;
		
		public ToData(List<Object> objects, int correctChoice) {
			this.objects = objects;
			this.correctChoice = correctChoice;
		}

		public List<Object> getObjects() {
			return objects;
		}

		public int getCorrectChoice() {
			return correctChoice;
		}
	}
	
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
