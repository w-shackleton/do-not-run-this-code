package uk.digitalsquid.contactrecall.mgr;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.SparseArray;

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
		
		SparseArray<List<Integer>> groupContactRelations = app.getGroups().getGroupContactRelations(true);
		Set<List<Integer>> groupContactRelationsValues = ListUtils.values(groupContactRelations);
		
		contacts = new ArrayList<Contact>(cur.getCount());
		
		if(cur.getCount() > 0) {
			while(cur.moveToNext()) {
				int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				if(contactInVisibleGroup(groupContactRelationsValues, id)) {
					Contact contact = new Contact();
					contact.setId(id);
					contact.setDisplayName(displayName);
					
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
	
	public LinkedList<RawContact> getRawContacts(int contactId) {
		Cursor cur = cr.query(ContactsContract.RawContacts.CONTENT_URI,
				new String[] {
					ContactsContract.RawContacts.ACCOUNT_NAME,
					ContactsContract.RawContacts.CONTACT_ID,
					ContactsContract.RawContacts._ID,
				}, "contact_id=?", new String[] {
					String.valueOf(contactId),
				}, null);
		
		if(cur.getCount() > 0) {
			int colAccountName = cur.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME);
			int colContactId = cur.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID);
			int colRawId = cur.getColumnIndex(ContactsContract.RawContacts._ID);
			
			LinkedList<RawContact> contacts = new LinkedList<RawContact>();
			
			while(cur.moveToNext()) {
				RawContact contact = new RawContact();
				contact.setAccountName(cur.getString(colAccountName));
				contact.setContactId(cur.getInt(colContactId));
				contact.setId(cur.getInt(colRawId));
				
				contacts.add(contact);
			}
			return contacts;
		}
		return new LinkedList<RawContact>();
	}
	
	public List<Contact> getContacts() {
		return contacts;
	}
	
	public Contact getContact(int id) {
		for(Contact contact : contacts) {
			if(contact.getId() == id)
				return contact;
		}
		return null;
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
