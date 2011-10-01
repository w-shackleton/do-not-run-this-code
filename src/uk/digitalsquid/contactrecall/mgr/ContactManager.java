package uk.digitalsquid.contactrecall.mgr;

import java.util.HashMap;
import java.util.Map;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.misc.Config;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

public final class ContactManager implements Config {
	private final ContentResolver cr;
	
	private final Map<Integer, Contact> contacts;
	
	private final App app;
	
	public ContactManager(Context context, App app) {
		this.app = app;
		cr = context.getContentResolver();
		
		contacts = new HashMap<Integer, Contact>();
		
		loadBaseData();
	}
	
	private void loadBaseData() {
		contacts.clear();
		
		app.getGroups().getContactGroups();
		
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		if(cur.getCount() > 0) {
			while(cur.moveToNext()) {
				int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				Contact contact = new Contact();
				contact.setId(id);
				
				Log.v(TAG, displayName);
				
				contacts.put(id, contact);
			}
		}
	}
}
