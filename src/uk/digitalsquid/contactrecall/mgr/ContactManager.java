package uk.digitalsquid.contactrecall.mgr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.misc.Config;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Abstracts the contacts API to the list of contacts.
 * @author william
 *
 */
public final class ContactManager implements Config {
	private final ContentResolver cr;
	
	private final Handler eventHandler;
	
	private List<Contact> contacts;
	
	private final App app;
	
	public ContactManager(Context context, App app) {
		this.app = app;
		cr = context.getContentResolver();
		
		eventHandler = new Handler();
		
		cr.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, observer);
		
		loadBaseData();
	}
	
	/**
	 * Loads the contact base data, ie. the data from the first table. Extra data is only loaded as needed.
	 */
	private void loadBaseData() {
		
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME },
				null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
		
		Map<Integer, List<Integer>> groupContactRelations = app.getGroups().getGroupContactRelations(true);
		
		contacts = new ArrayList<Contact>(cur.getCount());
		
		if(cur.getCount() > 0) {
			while(cur.moveToNext()) {
				int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				if(contactInVisibleGroup(groupContactRelations.values(), id)) {
					Contact contact = new Contact();
					contact.setId(id);
					contact.setDisplayName(displayName);
					
					Log.v(TAG, displayName);
					
					contacts.add(contact);
				}
			}
		}
	}
	
	/**
	 * Checks if a contact is in any of the given group lists.
	 * @param groups
	 * @param contactId
	 * @return
	 */
	final static boolean contactInVisibleGroup(Collection<List<Integer>> groups, int contactId) {
		for(List<Integer> list : groups) {
			if(list.contains(contactId)) return true;
		}
		return false;
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}

	private final ContentObserver observer = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			loadBaseData();
			eventHandler.post(new Runnable() {
				@Override
				public void run() {
					// Send notification to each listener
					for(WeakReference<ContactChangeListener> l : changeListeners) {
						ContactChangeListener ll = l.get();
						if(ll != null)
							ll.onContactsChanged(getContacts());
						else changeListeners.remove(l);
					}
				}
			});
		}
	};
	
	public void refresh() {
		loadBaseData();
		observer.onChange(true); // Update UI
	}
	
	private final List<WeakReference<ContactChangeListener>> changeListeners = new LinkedList<WeakReference<ContactChangeListener>>();
	
	/**
	 * Registers a listener to receive new contact lists. Keeps a weak reference to stop objects being held on to.
	 * @param l
	 */
	public void registerChangeListener(ContactChangeListener l) {
		changeListeners.add(new WeakReference<ContactChangeListener>(l));
	}
	
	public void unregisterChangeListener(ContactChangeListener l) {
		for(WeakReference<ContactChangeListener> listener : changeListeners) {
			if(listener.get() == l)
				changeListeners.remove(listener);
		}
	}
	
	/**
	 * Notification interface for contacts changing
	 * @author william
	 *
	 */
	public static interface ContactChangeListener {
		/**
		 * Called when the contacts list has changed.
		 * @param newContacts
		 */
		public void onContactsChanged(List<Contact> newContacts);
	}
}
