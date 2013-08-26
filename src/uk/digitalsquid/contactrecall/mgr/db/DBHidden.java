package uk.digitalsquid.contactrecall.mgr.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.mgr.ContactManager;
import uk.digitalsquid.contactrecall.mgr.db.DB.DBSubclass;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHidden extends DBSubclass {
	
	private ContactManager contactMgr;
	
	public DBHidden(App app) {
		contactMgr = app.getContacts();
	}

	@Override
	void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE hidden_fields (" +
				"contact_id INTEGER NOT NULL," +
				"field INTEGER NOT NULL);");
	}

	public void addHiddenField(Contact contact, int field) {
		ContentValues values = new ContentValues(2);
		values.put("contact_id", contact.getId());
		values.put("field", field);
		db.insert("hidden_fields", null, values);

		contactMgr.hideContactField(contact, field);
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
		} finally {
			if(cur != null) cur.close();
		}
		return result;
	}
}
