package uk.digitalsquid.contactrecall.mgr.db;

import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.mgr.db.DB.DBSubclass;
import android.content.ContentValues;
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
public class ProgressDB extends DBSubclass {
	
	private static final int ATTEMPT_STATUS_SUCCESS = 1;
	private static final int ATTEMPT_STATUS_FAIL = 2;
	private static final int ATTEMPT_STATUS_TIMEOUT = 3;
	
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
		addAttempt(contact.getId(), -1, delay, ATTEMPT_STATUS_SUCCESS);
	}
	public void addFail(Contact contact, Contact mistake, float delay) {
		addAttempt(contact.getId(), mistake != null ? mistake.getId() : -1, delay, ATTEMPT_STATUS_FAIL);
	}
	public void addTimeout(Contact contact, float delay) {
		addAttempt(contact.getId(), -1, delay, ATTEMPT_STATUS_TIMEOUT);
	}
}
