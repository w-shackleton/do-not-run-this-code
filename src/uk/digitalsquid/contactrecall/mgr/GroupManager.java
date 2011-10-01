package uk.digitalsquid.contactrecall.mgr;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.digitalsquid.contactrecall.mgr.db.DB;
import uk.digitalsquid.contactrecall.misc.Config;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

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
	
	private final Map<Integer, Group> data = new HashMap<Integer, GroupManager.Group>();
	
	public Map<Integer, Group> getContactGroups() {
		List<Integer> selectedIds = db.groups.getSelectedGroupIds();
		boolean selectAll = selectedIds.size() == 0; // None selected, select all.
		
		Cursor cur = cr.query(ContactsContract.Groups.CONTENT_URI, null, "group_visible=0", null, null);
		if(cur.getCount() > 0) {
			final int titleIndex = cur.getColumnIndex(ContactsContract.Groups.TITLE);
			final int idIndex = cur.getColumnIndex(ContactsContract.Groups._ID);
			while(cur.moveToNext()) {
				String groupName = cur.getString(titleIndex);
				int id = cur.getInt(idIndex);
				boolean selected = selectAll || selectedIds.contains(id);
				data.put(id, new Group(id, groupName, selected));
			}
		}
		
		return data;
	}
	
	public static class Group implements Serializable {

		private static final long serialVersionUID = 3288621634924366350L;
		
		public final String name;
		public final int id;
		
		public final boolean visible;
		
		protected Group(int id, String name, boolean visible) {
			this.id = id;
			this.name = name;
			this.visible = visible;
		}
	}
}
