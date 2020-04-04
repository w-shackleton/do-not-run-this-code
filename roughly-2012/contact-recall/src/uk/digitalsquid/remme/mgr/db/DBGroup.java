package uk.digitalsquid.remme.mgr.db;

import java.util.ArrayList;
import java.util.List;

import uk.digitalsquid.remme.mgr.GroupManager.AccountDetails;
import uk.digitalsquid.remme.mgr.GroupManager.Group;
import uk.digitalsquid.remme.mgr.db.DB.DBSubclass;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

/**
 * DB methods to do with contact groups
 * @author william
 *
 */
public final class DBGroup extends DBSubclass {
	@Override
	void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE groups (" +
				"id INTEGER" +
						");");
		db.execSQL("CREATE TABLE accounts (" +
				"name TEXT," +
				"type TEXT" +
						");");
	}
	
	/**
	 * Gets the ID numbers of the groups the user wants to hide.
	 * @return
	 */
	public List<Integer> getVisibleGroupIds() {
		List<Integer> ids = new ArrayList<Integer>();
		
		Cursor cur = db.query("groups", new String[] {"id"}, null, null, null, null, null);
		
		int idCol = cur.getColumnIndex("id");
		while(cur.moveToNext()) {
			int id = cur.getInt(idCol);
			ids.add(id);
		}
		return ids;
	}
	
	/**
	 * Returns the visible accounts. Each {@link Pair} holds the name and type of
	 * the account
	 */
	public List<Pair<String, String>> getVisibleAccounts() {
		Cursor cur = db.query("accounts", new String[] {"name", "type"}, null, null, null, null, null);
		List<Pair<String, String>> result =
				new ArrayList<Pair<String,String>>(cur.getCount());
		int nameCol = cur.getColumnIndex("name");
		int typeCol = cur.getColumnIndex("type");
		while(cur.moveToNext()) {
			String name = cur.getString(nameCol);
			String type = cur.getString(typeCol);
			result.add(new Pair<String, String>(name, type));
		}
		return result;
	}
	
	public synchronized void setVisibleGroupIds(List<Integer> ids) {
		db.beginTransaction();
		try {
			db.delete("groups", null, null); // Remove old list.
			
			for(int id : ids) {
				ContentValues values = new ContentValues(1);
				values.put("id", id);
				db.insert("groups", null, values);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	public synchronized void setVisibleAccounts(List<Pair<String, String>> accounts) {
		db.beginTransaction();
		try {
			db.delete("accounts", null, null);
			
			for(Pair<String, String> account : accounts) {
				ContentValues values = new ContentValues(2);
				values.put("name", account.first);
				values.put("type", account.second);
				db.insert("accounts", null, values);
			}
			
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	/**
	 * Sets the accounts and groups visible.
	 * @param details
	 */
	public void setVisibleAccountsAndGroups(List<AccountDetails> details) {
		List<Pair<String, String>> accounts = new ArrayList<Pair<String,String>>(details.size());
		List<Integer> groups = new ArrayList<Integer>();

		for(AccountDetails detail : details) {
			if(detail.isUserVisible())
				accounts.add(new Pair<String, String>(
						detail.getAccountName(), detail.getAccountType()));
			for(Group group : detail.getGroups()) {
				if(group.isUserVisible())
					groups.add(group.id);
			}
		}
		setVisibleAccounts(accounts);
		setVisibleGroupIds(groups);
	}
}
