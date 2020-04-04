package uk.digitalsquid.remme;

import uk.digitalsquid.remme.mgr.ContactManager;
import uk.digitalsquid.remme.mgr.GroupManager;
import uk.digitalsquid.remme.mgr.PhotoManager;
import uk.digitalsquid.remme.mgr.db.DB;
import uk.digitalsquid.remme.stats.Stats;
import android.app.Application;

/**
 * Base {@link Application}, which holds instances to various managers.
 * @author william
 *
 */
public class App extends Application {
	private DB db;
	
	private GroupManager groups;
	private ContactManager contacts;
	private PhotoManager photos;
	private Stats stats;
	
	public DB getDb() {
		if(db == null) db = new DB(this);
		return db;
	}

	public GroupManager getGroups() {
		if(groups == null) groups = new GroupManager(this, getDb());
		return groups;
	}

	public ContactManager getContacts() {
		if(contacts == null) contacts = new ContactManager(getApplicationContext(), this);
		return contacts;
	}

	public PhotoManager getPhotos() {
		if(photos == null) photos = new PhotoManager(getApplicationContext(), getDb());
		return photos;
	}
	
	public Stats getStats() {
		if(stats == null) stats = new Stats(getApplicationContext(), getDb());
		return stats;
	}
}