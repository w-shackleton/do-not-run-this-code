package uk.digitalsquid.remme.mgr.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.mgr.ContactManager;
import uk.digitalsquid.remme.mgr.db.DB.DBSubclass;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.mgr.details.DataItem;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class DBHidden extends DBSubclass {
	
	private ContactManager contactMgr;
	
	public DBHidden(App app) {
		contactMgr = app.getContacts();
	}

	@Override
	void onCreate(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE hidden_fields (" +
					"contactKey TEXT NOT NULL," +
					"field INTEGER NOT NULL);");
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to create hidden_fields table", e);
		}
		try {
			db.execSQL("CREATE TABLE hidden_contacts (" +
					"contactKey TEXT NOT NULL);");
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to create hidden_contacts table", e);
		}
	}

	public void addHiddenField(Contact contact, int field) {
		ContentValues values = new ContentValues(2);
		values.put("contactKey", contact.getLookupKey());
		values.put("field", field);
		db.insert("hidden_fields", null, values);

		contactMgr.hideContactField(contact, field);
	}
	
	public void addHiddenField(DataItem error) {
		addHiddenField(error.getContact(), error.getField());
	}
	
	public void addHiddenContact(Contact contact) {
		ContentValues values = new ContentValues(1);
		values.put("contactKey", contact.getLookupKey());
		db.insert("hidden_contacts", null, values);

		contactMgr.hideContact(contact);
	}

	public void addHiddenContact(DataItem error) {
		addHiddenContact(error.getContact());
	}
	
	/**
	 * Gets all hidden fields, as a {@link HashMap} of Contact ID
	 * to lists hidden fields.
	 * @return
	 */
	public HashMap<String, List<Integer>> getHiddenFields() {
		Cursor cur = null;
		HashMap<String, List<Integer>> result =
				new HashMap<String, List<Integer>>();
		try {
			cur = db.query("hidden_fields", new String[] {
					"contactKey", "field"
			}, null, null, null, null, null);
			
			final int keyIdx = cur.getColumnIndex("contactKey");
			final int fieldIdx = cur.getColumnIndex("field");
			
			while(cur.moveToNext()) {
				final String key = cur.getString(keyIdx);
				final int field = cur.getInt(fieldIdx);
				if(!result.containsKey(key))
					result.put(key, new ArrayList<Integer>());
				result.get(key).add(field);
			}
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to query database", e);
			// Try re-creating DB
			onCreate(db);
		} finally {
			if(cur != null) cur.close();
		}
		return result;
	}
	
	public Set<String> getHiddenContacts() {
		Cursor cur = null;
		try {
			cur = db.query("hidden_contacts", new String[] {
					"contactKey"
			}, null, null, null, null, null);
			
			HashSet<String> result = new HashSet<String>(cur.getCount());
			
			final int keyIdx = cur.getColumnIndex("contactKey");
			
			while(cur.moveToNext()) {
				final String key = cur.getString(keyIdx);
				result.add(key);
			}
			return result;
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to query database", e);
			// Try re-creating DB
			onCreate(db);
		} finally {
			if(cur != null) cur.close();
		}
		return new HashSet<String>();
	}
}
