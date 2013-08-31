package uk.digitalsquid.contactrecall.mgr.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.mgr.ContactManager;
import uk.digitalsquid.contactrecall.mgr.db.DB.DBSubclass;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import android.annotation.SuppressLint;
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
					"contact_id INTEGER NOT NULL," +
					"field INTEGER NOT NULL);");
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to create hidden_fields table", e);
		}
		try {
			db.execSQL("CREATE TABLE hidden_contacts (" +
					"contact_id INTEGER NOT NULL);");
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to create hidden_contacts table", e);
		}
	}

	public void addHiddenField(Contact contact, int field) {
		ContentValues values = new ContentValues(2);
		values.put("contact_id", contact.getId());
		values.put("field", field);
		db.insert("hidden_fields", null, values);

		contactMgr.hideContactField(contact, field);
	}
	
	public void addHiddenField(DataItem error) {
		addHiddenField(error.getContact(), error.getField());
	}
	
	public void addHiddenContact(Contact contact) {
		ContentValues values = new ContentValues(1);
		values.put("contact_id", contact.getId());
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
	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, List<Integer>> getHiddenFields() {
		Cursor cur = null;
		HashMap<Integer, List<Integer>> result =
				new HashMap<Integer, List<Integer>>();
		try {
			cur = db.query("hidden_fields", new String[] {
					"contact_id", "field"
			}, null, null, null, null, null);
			
			final int idIdx = cur.getColumnIndex("contact_id");
			final int fieldIdx = cur.getColumnIndex("field");
			
			while(cur.moveToNext()) {
				final int id = cur.getInt(idIdx);
				final int field = cur.getInt(fieldIdx);
				if(!result.containsKey(id))
					result.put(id, new ArrayList<Integer>());
				result.get(id).add(field);
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
	
	public Set<Integer> getHiddenContacts() {
		Cursor cur = null;
		try {
			cur = db.query("hidden_contacts", new String[] {
					"contact_id"
			}, null, null, null, null, null);
			
			HashSet<Integer> result = new HashSet<Integer>(cur.getCount());
			
			final int idIdx = cur.getColumnIndex("contact_id");
			
			while(cur.moveToNext()) {
				final int id = cur.getInt(idIdx);
				result.add(id);
			}
			return result;
		} catch(SQLiteException e) {
			Log.w(TAG, "Failed to query database", e);
			// Try re-creating DB
			onCreate(db);
		} finally {
			if(cur != null) cur.close();
		}
		return new HashSet<Integer>();
	}
}
