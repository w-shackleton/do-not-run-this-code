package uk.digitalsquid.contactrecall;

import uk.digitalsquid.contactrecall.mgr.GroupManager;
import uk.digitalsquid.contactrecall.mgr.db.DB;
import android.app.Application;

public class App extends Application {
	private DB db;
	
	private GroupManager groups;

	public DB getDb() {
		if(db == null) db = new DB(this);
		return db;
	}

	public GroupManager getGroups() {
		if(groups == null) groups = new GroupManager(this, getDb());
		return groups;
	}
}
