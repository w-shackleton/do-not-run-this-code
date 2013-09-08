package uk.digitalsquid.contactrecall.mgr.db;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.mgr.db.DB.DBSubclass;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


/**
 * Interfaces with a database that stores the user's successes and fails,
 * with respect to how much they know each contact. This information can
 * then be collated to provide data to further games.
 * @author william
 *
 */
public class DBProgress extends DBSubclass {
	
	private final App app;
	
	DBProgress(App app) {
		this.app = app;
	}
	
	/*
	 * The attempts table stores every attempt the user makes at guessing a
	 * contact.
	 * @see uk.digitalsquid.contactrecall.mgr.db.DB.DBSubclass#onCreate(android.database.sqlite.SQLiteDatabase)
	 */

	@Override
	void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE attempts (" +
							"id INTEGER PRIMARY KEY AUTOINCREMENT," +
							"contactId INTEGER," + // The ID of the contact
							"mistakeId INTEGER," + // The ID of the mistaken contact
							"delay REAL," + // How long it took to guess
							"time INTEGER," + // The time when this result was generated
							"status INTEGER" + // status
							");");
			db.execSQL("CREATE INDEX contact_idx ON attempts(contactId);");
		} catch(SQLException e) {
			// TODO: This is critical - alert user / submit a crash/wtf report.
			Log.e(TAG, "Failed to create attempts table!", e);
		}
	}
	
	void addAttempt(int contactId, int mistakeId, float delay, int status) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues(4);
			values.put("contactId", contactId);
			values.put("mistakeId", mistakeId);
			values.put("delay", delay);
			values.put("time", System.currentTimeMillis() / 1000L);
			values.put("status", status);
			db.insert("attempts", null, values);
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public void addSuccess(Contact contact, float delay) {
		addAttempt(contact.getId(), -1, delay, GameCallbacks.CHOICE_CORRECT);
	}
	public void addFail(Contact contact, Contact mistake, float delay) {
		addAttempt(contact.getId(), mistake != null ? mistake.getId() : -1,
				delay, GameCallbacks.CHOICE_INCORRECT);
	}
	public void addDiscard(Contact contact, float delay) {
		addAttempt(contact.getId(), -1, delay, GameCallbacks.CHOICE_DISCARD);
	}
	public void addTimeout(Contact contact, float delay) {
		addAttempt(contact.getId(), -1, delay, GameCallbacks.CHOICE_TIMEOUT);
	}
	
	/**
	 * @return Data about contact attempt responses.
	 * Some values in this array may be null.
	 */
	public GroupedMeanAttempt[] getGroupedMeanAttemptData() {
		Cursor c = db.query(
				"attempts",
				new String[] { "contactId", "status", "AVERAGE(delay)", "COUNT(contactId)" },
				null,
				null,
				"contactId, status",
				null,
				"contactId, status");
		GroupedMeanAttempt[] result = new GroupedMeanAttempt[c.getCount()];
		int i = 0;
		while(c.moveToNext()) {
			GroupedMeanAttempt val = new GroupedMeanAttempt();
			
			Contact contact = app.getContacts().getContact(c.getInt(0));
			val.setContact(contact);
			val.setStatus(c.getInt(1));
			val.setMeanDelay(c.getFloat(2));
			val.setCount(c.getInt(3));
			result[i++] = val;
		}
		c.close();
		return result;
	}
	
	public static class GroupedMeanAttempt {
		private Contact contact;
		private int status;
		private float meanDelay;
		private int count;
		public Contact getContact() {
			return contact;
		}
		void setContact(Contact contact) {
			this.contact = contact;
		}
		public int getStatus() {
			return status;
		}
		void setStatus(int status) {
			this.status = status;
		}
		public float getMeanDelay() {
			return meanDelay;
		}
		void setMeanDelay(float meanDelay) {
			this.meanDelay = meanDelay;
		}
		public int getCount() {
			return count;
		}
		void setCount(int count) {
			this.count = count;
		}
	}
}
