package uk.digitalsquid.contactrecall.stats;

import java.util.HashMap;

import uk.digitalsquid.contactrecall.mgr.db.DB;
import uk.digitalsquid.contactrecall.mgr.db.ProgressDB;
import uk.digitalsquid.contactrecall.mgr.db.ProgressDB.GroupedMeanAttempt;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.content.Context;

/**
 * Generates statistics on contacts based upon previous
 * game experience.
 * @author william
 *
 */
public final class Stats {
	
	private ProgressDB progress;
	
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
			case ProgressDB.ATTEMPT_STATUS_SUCCESS:
				stats.successes = meanAttempt.getCount();
				stats.successTime = meanAttempt.getMeanDelay();
				break;
			case ProgressDB.ATTEMPT_STATUS_FAIL:
				stats.fails = meanAttempt.getCount();
				stats.failTime = meanAttempt.getMeanDelay();
				break;
			case ProgressDB.ATTEMPT_STATUS_TIMEOUT:
				stats.timeouts = meanAttempt.getCount();
				break;
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
		private float successTime = 0;
		private float failTime = 0;
		public Contact getContact() { return contact; }
		void setContact(Contact contact) { this.contact = contact; }
		public int getSuccesses() { return successes; }
		void setSuccesses(int successes) { this.successes = successes; }
		public int getFails() { return fails; }
		void setFails(int fails) { this.fails = fails; }
		public int getTimeouts() { return timeouts; }
		void setTimeouts(int timeouts) { this.timeouts = timeouts; }
		public float getSuccessTime() { return successTime; }
		void setSuccessTime(float successTime) { this.successTime = successTime; }
		public float getFailTime() { return failTime; }
		void setFailTime(float failTime) { this.failTime = failTime; }
		
		@Override
		public int hashCode() {
			return contact.hashCode();
		}
	}
}
