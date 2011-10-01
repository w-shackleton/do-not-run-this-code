package uk.digitalsquid.contactrecall.mgr.db;

import java.util.ArrayList;
import java.util.List;

import uk.digitalsquid.contactrecall.mgr.db.DB.DBSubclass;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * DB methods to do with contact groups
 * @author william
 *
 */
public final class DBGroup extends DBSubclass {
	
	static final String GROUPS = "groups";
	static final String GROUPS_ID = "id";

	@Override
	void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + GROUPS + "(" +
				GROUPS_ID + " INTEGER" +
						");");
	}
	
	/**
	 * Gets the ID numbers of the groups the user wants to view.
	 * @return
	 */
	public List<Integer> getSelectedGroupIds() {
		List<Integer> ids = new ArrayList<Integer>();
		
		Cursor cur = db.query(GROUPS, new String[] {GROUPS_ID}, null, null, null, null, null);
		
		int idCol = cur.getColumnIndex(GROUPS_ID);
		while(cur.moveToNext()) {
			int id = cur.getInt(idCol);
			ids.add(id);
		}
		return ids;
	}
	
	public synchronized void setSelectedGroupIds(List<Integer> ids) {
		db.beginTransaction();
		try {
			db.delete(GROUPS, null, null); // Remove old list.
			
			for(int id : ids) {
				ContentValues values = new ContentValues(ids.size());
				values.put(GROUPS_ID, id);
				db.insert(GROUPS, null, values);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
}
