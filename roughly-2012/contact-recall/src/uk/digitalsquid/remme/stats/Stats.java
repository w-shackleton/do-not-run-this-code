package uk.digitalsquid.remme.stats;

import java.util.HashMap;

import uk.digitalsquid.remme.ingame.GameCallbacks;
import uk.digitalsquid.remme.mgr.db.DB;
import uk.digitalsquid.remme.mgr.db.DBProgress;
import uk.digitalsquid.remme.mgr.db.DBProgress.GroupedMeanAttempt;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.misc.Utils;
import android.content.Context;

/**
 * Generates statistics on contacts based upon previous
 * game experience.
 * @author william
 *
 */
public final class Stats {
	
	private DBProgress progress;
	
	/**
	 * Do not call this constructor directly.
	 * Use App.getStats();
	 * @param context
	 * @param db
	 */
	public Stats(Context context, DB db) {
		progress = db.progress;
		loadBaseData();
	}
	
	HashMap<Contact, ContactStats> contactStats;
	
	private void loadBaseData() {
		GroupedMeanAttempt[] meanAttempts = progress.getGroupedMeanAttemptData();
		
		contactStats = new HashMap<Contact, Stats.ContactStats>();
		
		for(GroupedMeanAttempt meanAttempt : meanAttempts) {
			if(!contactStats.containsKey(meanAttempt.getContact())) {
				contactStats.put(meanAttempt.getContact(), new ContactStats());
			}
			ContactStats stats = contactStats.get(meanAttempt.getContact());
			switch(meanAttempt.getStatus()) {
			case GameCallbacks.CHOICE_CORRECT:
				stats.successes = meanAttempt.getCount();
				stats.successTime = meanAttempt.getMeanDelay();
				break;
			case GameCallbacks.CHOICE_INCORRECT:
				stats.fails = meanAttempt.getCount();
				stats.failTime = meanAttempt.getMeanDelay();
				break;
			case GameCallbacks.CHOICE_TIMEOUT:
				stats.timeouts = meanAttempt.getCount();
				break;
			case GameCallbacks.CHOICE_DISCARD:
				stats.discards = meanAttempt.getCount();
			}
		}
	}
	
	/**
	 * Represents some basic statistics about a contact.
	 * @author william
	 *
	 */
	public static final class ContactStats {
		private Contact contact;
		private int successes = 0;
		private int fails = 0;
		private int timeouts = 0;
		private int discards = 0;
		private float successTime = 0;
		private float failTime = 0;
		public Contact getContact() { return contact; }
		// void setContact(Contact contact) { this.contact = contact; }
		public int getSuccesses() { return successes; }
		// void setSuccesses(int successes) { this.successes = successes; }
		public int getFails() { return fails; }
		// void setFails(int fails) { this.fails = fails; }
		public int getTimeouts() { return timeouts; }
		// void setTimeouts(int timeouts) { this.timeouts = timeouts; }
		public float getSuccessTime() { return successTime; }
		// void setSuccessTime(float successTime) { this.successTime = successTime; }
		public float getFailTime() { return failTime; }
		// void setFailTime(float failTime) { this.failTime = failTime; }
		public int getDiscards() { return discards; }
		public void setDiscards(int discards) { this.discards = discards; }
		
		public int getTotalTries() {
			return successes + fails + timeouts + discards;
		}
		
		@Override
		public int hashCode() {
			return contact.hashCode();
		}
	}
	
	public ContactStats getContactStats(Contact contact) {
		return contactStats.get(contact);
	}
	
	/**
	 * Computes the score for correctly answering a question on a given contact
	 * @param contact
	 * @return
	 */
	public float computeScoreWeight(Contact contact) {
		final ContactStats stats = getContactStats(contact);
		if(stats == null) return 1;
		final float totalTries = stats.getTotalTries();

		// Since we don't want scores flying around at first, we assume a min of
		// 20 answers at first
		final float breakinTries = Math.max(totalTries, 20);
		float scoreWeight = 1;
		// If the user answered correctly every time, this would go to 0
		scoreWeight -= (float)stats.getSuccesses() / breakinTries;
		scoreWeight += (float)stats.getFails() / breakinTries * 3;
		scoreWeight += (float)stats.getTimeouts() / breakinTries * 0.3f;
		scoreWeight += (float)stats.getDiscards() / breakinTries * 1;
		
		return Utils.minMax(scoreWeight, 0, 5);
	}
}
