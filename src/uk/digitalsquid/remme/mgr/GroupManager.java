package uk.digitalsquid.remme.mgr;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.remme.mgr.db.DB;
import uk.digitalsquid.remme.misc.Config;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.SparseArray;

/**
 * Manages contact groups
 * @author william
 *
 */
public class GroupManager implements Config {
	@SuppressWarnings("unused")
	private final Context context;
	private final ContentResolver cr;
	private final DB db;
	
	public GroupManager(Context context, DB db) {
		this.context = context;
		cr = context.getContentResolver();
		this.db = db;
	}
	
	private final SparseArray<Group> data = new SparseArray<GroupManager.Group>();
	
	public SparseArray<Group> getContactGroups() {
		List<Integer> hiddenIds = db.groups.getHiddenGroupIds();
		
		Cursor cur = cr.query(ContactsContract.Groups.CONTENT_URI, null, null, null, null);
		if(cur.getCount() > 0) {
			final int titleIndex = cur.getColumnIndex(ContactsContract.Groups.TITLE);
			final int idIndex = cur.getColumnIndex(ContactsContract.Groups._ID);
			while(cur.moveToNext()) {
				String groupName = cur.getString(titleIndex);
				int id = cur.getInt(idIndex);
				boolean hidden = hiddenIds.contains(id);
				data.put(id, new Group(id, groupName, hidden));
			}
		}
		cur.close();
		
		return data;
	}
	
	/**
	 * 
	 * @param visibleGroupsOnly If true, will only return mappings for visible groups
	 * @return A map from group IDs to the contacts in that group.
	 */
	public SparseArray<List<Integer>> getGroupContactRelations(boolean visibleGroupsOnly) {
		final SparseArray<List<Integer>> data = new SparseArray<List<Integer>>();
		final String groupWhere = ContactsContract.Data.MIMETYPE + " = ?";
		
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, new String[] {
				ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID,
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID },
				groupWhere, new String[] {ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null);
		
		SparseArray<Group> visibleGroups = null;
		if(visibleGroupsOnly) {
			visibleGroups = getContactGroups();
		}
		
		if(cur.getCount() > 0) {
			final int contactIdIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);
			final int groupIdIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID);
			while(cur.moveToNext()) {
				int contactId = cur.getInt(contactIdIndex);
				int groupId = cur.getInt(groupIdIndex);
				
				// Check if group is visible
				if(visibleGroups != null) {
					Group g = visibleGroups.get(groupId);
					if(g != null) if(g.hidden) continue; // Don't add if invisible
				}
				
				if(data.get(groupId) == null)
					data.put(groupId, new LinkedList<Integer>());
				data.get(groupId).add(contactId);
			}
		}
		cur.close();
		return data;
	}
	
	public static class Group implements Serializable {

		private static final long serialVersionUID = 3288621634924366350L;
		
		public final String name;
		public final int id;
		
		public final boolean hidden;
		
		protected Group(int id, String name, boolean hidden) {
			this.id = id;
			this.name = name;
			this.hidden = hidden;
		}
	}
}
