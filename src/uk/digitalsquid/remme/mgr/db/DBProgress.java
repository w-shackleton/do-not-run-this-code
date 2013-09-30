package uk.digitalsquid.remme.mgr.db;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.ingame.GameCallbacks;
import uk.digitalsquid.remme.mgr.db.DB.DBSubclass;
import uk.digitalsquid.remme.mgr.details.Contact;
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
	 * @see uk.digitalsquid.remme.mgr.db.DB.DBSubclass#onCreate(android.database.sqlite.SQLiteDatabase)
	 */

	@Override
	void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE attempts (" +
							"id INTEGER PRIMARY KEY AUTOINCREMENT," +
							"contactKey TEXT," + // The ID of the contact
							"mistakeKey TEXT," + // The ID of the mistaken contact
							"delay REAL," + // How long it took to guess
							"time INTEGER," + // The time when this result was generated
							"status INTEGER" + // status
							");");
			db.execSQL("CREATE INDEX contact_idx ON attempts(contactKey);");
		} catch(SQLException e) {
			// TODO: This is critical - alert user / submit a crash/wtf report.
			Log.e(TAG, "Failed to create attempts table!", e);
		}
	}
	
	void addAttempt(String contactKey, String mistakeKey, float delay, int status) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues(4);
			values.put("contactKey", contactKey);
			values.put("mistakeKey", mistakeKey);
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
		addAttempt(contact.getLookupKey(), null, delay, GameCallbacks.CHOICE_CORRECT);
	}
	public void addFail(Contact contact, Contact mistake, float delay) {
		addAttempt(contact.getLookupKey(), mistake != null ? mistake.getLookupKey() : null,
				delay, GameCallbacks.CHOICE_INCORRECT);
	}
	public void addDiscard(Contact contact, float delay) {
		addAttempt(contact.getLookupKey(), null, delay, GameCallbacks.CHOICE_DISCARD);
	}
	public void addTimeout(Contact contact, float delay) {
		addAttempt(contact.getLookupKey(), null, delay, GameCallbacks.CHOICE_TIMEOUT);
	}
	
	/**
	 * @return Data about contact attempt responses.
	 * Some values in this array may be null.
	 */
	public GroupedMeanAttempt[] getGroupedMeanAttemptData() {
		Cursor c = db.query(
				"attempts",
				new String[] { "contactKey", "status", "AVG(delay)", "COUNT(contactKey)" },
				null,
				null,
				"contactKey, status",
				null,
				"contactKey, status");
		GroupedMeanAttempt[] result = new GroupedMeanAttempt[c.getCount()];
		int i = 0;
		while(c.moveToNext()) {
			GroupedMeanAttempt val = new GroupedMeanAttempt();
			
			Contact contact = app.getContacts().getContact(c.getString(0));
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
