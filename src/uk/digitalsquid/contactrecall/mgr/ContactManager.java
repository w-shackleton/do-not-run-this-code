package uk.digitalsquid.contactrecall.mgr;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.ListUtils;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;

/**
 * Abstracts the contacts API to the list of contacts.
 * @author william
 *
 */
@SuppressLint("UseSparseArrays")
public final class ContactManager implements Config {
	public static final String BROADCAST_LOADSTATUS =
			"uk.digitalsquid.contactrecall.mgr.ContactManager.LoadStatus";
	public static final String LOADSTATUS_STATUS =
			"uk.digitalsquid.contactrecall.mgr.ContactManager.LoadStatus.Value";

	private final ContentResolver cr;
	
	private final Handler eventHandler;
	
	private HashMap<Integer, Contact> contacts;
	private Collection<Contact> contactCollection;
	
	private final App app;
	
	private final LocalBroadcastManager localBroadcastManager;
	
	/**
	 * Sync object for BG data loading.
	 */
	private Object loadingSync = new Object();
	/**
	 * If <code>true</code>, shows that the base data has been loaded.
	 */
	private boolean dataLoaded;
	
	public ContactManager(Context context, App app) {
		this.app = app;
		cr = context.getContentResolver();
		
		localBroadcastManager = LocalBroadcastManager.getInstance(context);
		
		eventHandler = new Handler();
		
		cr.registerContentObserver(ContactsContract.Contacts.CONTENT_URI, false, observer);
	}
	
	private void loadBaseData() {
		loadData(LoadingStatusListener.NULL_LISTENER);
	}
	
	/**
	 * 
	 * @return <code>true</code> if all data is successfully loaded,
	 * <code>false</code> if data isn't loaded or is being loaded.
	 */
	public boolean isLoaded() {
		return dataLoaded;
	}
	
	/**
	 * Loads all contact data.
	 */
	private synchronized void loadData(LoadingStatusListener listener) {
		
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME },
				null, null, ContactsContract.Contacts._ID + " ASC");
		
		Log.d(TAG, "Starting load");
		
		SparseArray<List<Integer>> groupContactRelations = app.getGroups().getGroupContactRelations(true);
		Set<List<Integer>> groupContactRelationsValues = ListUtils.values(groupContactRelations);
		
		Log.d(TAG, "Loaded groups");

		contacts = new HashMap<Integer, Contact>();
		
		// Get hidden contacts
		Set<Integer> hiddenContacts = app.getDb().hidden.getHiddenContacts();
		
		// Load base data and populate HashMap
		int total = cur.getCount();
		int i = 0;
		if(total > 0) {
			while(cur.moveToNext()) {
				int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				if(hiddenContacts.contains(id)) continue;
				String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				if(contactInVisibleGroup(groupContactRelationsValues, id)) {
					Contact contact = new Contact();
					contact.setId(id);
					contact.setDisplayName(displayName);
					
					contacts.put(contact.getId(), contact);
				}
				i++;
				if(i % 10 == 0)
					listener.onBaseDataLoadProgress((float)i / (float)total);
			}
		}
		cur.close();
		
		Log.d(TAG, "Loaded base");

		// Get data from Data table and populate contacts further
		// This query could take a LONG time.
		cur = cr.query(ContactsContract.Data.CONTENT_URI,
				new String[] {
				ContactsContract.Data.CONTACT_ID,
				ContactsContract.Data.MIMETYPE,
				ContactsContract.CommonDataKinds.Organization.COMPANY,
				ContactsContract.CommonDataKinds.Organization.DEPARTMENT,
				ContactsContract.CommonDataKinds.Organization.TITLE,
				ContactsContract.CommonDataKinds.Phone.NUMBER,
				ContactsContract.CommonDataKinds.Phone.TYPE,
				ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
				ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
				ContactsContract.CommonDataKinds.Email.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Email.TYPE,
		},
				null, null, null);
		int idIdx		= cur.getColumnIndexOrThrow(ContactsContract.Data.CONTACT_ID);
		int mimeTypeIdx	= cur.getColumnIndexOrThrow(ContactsContract.Data.MIMETYPE);
		int companyIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.COMPANY);
		int departmentIdx=cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.DEPARTMENT);
		int titleIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Organization.TITLE);
		int numberIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER);
		int numberTypeIdx=cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE);
		int givenNameIdx= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME);
		int familyNameIdx=cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME);
		int emailNameIdx= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME);
		int emailtypeIdx= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.TYPE);

		total = cur.getCount();
		i = 0;
		while(cur.moveToNext()) {
			i++;
			Contact contact = contacts.get(cur.getInt(idIdx));
			// Send status
			if(contact == null) continue;
			String mime = cur.getString(mimeTypeIdx);
			if(mime == null) continue;
			if(i % 10 == 0)
				listener.onAuxiliaryDataLoadProgress((float)(i) / (float)total);
			if(mime.equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
				contact.getDetails().setCompany(cur.getString(companyIdx));
				contact.getDetails().setDepartment(cur.getString(departmentIdx));
				contact.getDetails().setCompanyTitle(cur.getString(titleIdx));
			}
			else if(mime.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
				switch(cur.getInt(numberTypeIdx)) {
				case Phone.TYPE_HOME:
					contact.getDetails().setHomePhone(cur.getString(numberIdx)); break;
				case Phone.TYPE_WORK:
					contact.getDetails().setWorkPhone(cur.getString(numberIdx)); break;
				case Phone.TYPE_MOBILE:
					contact.getDetails().setMobilePhone(cur.getString(numberIdx)); break;
				case Phone.TYPE_OTHER:
					contact.getDetails().setOtherPhone(cur.getString(numberIdx)); break;
				}
			}
			else if(mime.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
				contact.setFirstName(cur.getString(givenNameIdx));
				contact.setLastName(cur.getString(familyNameIdx));
			}
			else if(mime.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
				switch(cur.getInt(emailtypeIdx)) {
				case Email.TYPE_HOME:
					contact.getDetails().setHomeEmail(cur.getString(emailNameIdx)); break;
				case Email.TYPE_WORK:
					contact.getDetails().setWorkEmail(cur.getString(emailNameIdx)); break;
				case Email.TYPE_MOBILE:
					contact.getDetails().setMobileEmail(cur.getString(emailNameIdx)); break;
				case Email.TYPE_OTHER:
					contact.getDetails().setOtherEmail(cur.getString(emailNameIdx)); break;
				}
			}
		}
		cur.close();
		
		Log.d(TAG, "Loaded aux");

		// Fill-in photo column
		List<Integer> contactsWithPictures = app.getPhotos().getContactsWithPictures();
		
		Log.d(TAG, "Loaded photos");

		for(int id : contactsWithPictures) {
			Contact contact = contacts.get(id);
			if(contact == null) continue;
			contact.getDetails().setHasPicture(true);
		}
		
		// Finally, remove all fields that the user wants to hide.
		for(Entry<Integer, List<Integer>> pair : app.getDb().hidden.getHiddenFields().entrySet()) {
			Contact contact = contacts.get(pair.getKey());
			if(contact == null) continue;
			for(Integer field : pair.getValue()) {
				contact.removeField(field);
			}
		}
		
		// Convert to a Set for ease of use
		contactCollection = contacts.values();
		dataLoaded = true;
	}
	
	/**
	 * Checks if a contact is in any of the given group lists.
	 * @param groups The groups to check against
	 * @param contactId The ID of the contact to check
	 * @return <code>true</code> if contactId is in a visible group.
	 */
	// Doesn't need loading synchronisation
	final static boolean contactInVisibleGroup(Collection<List<Integer>> groups, int contactId) {
		for(List<Integer> list : groups) {
			if(list.contains(contactId)) return true;
		}
		return false;
	}
	
	// Doesn't need loading synchronisation
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
	
	public Collection<Contact> getContacts() {
		synchronized(loadingSync) {
			if(!dataLoaded) loadBaseData();
		}
		return contactCollection;
	}
	// TODO: Use this more?
	public Map<Integer, Contact> getContactMap() {
		synchronized(loadingSync) {
			if(!dataLoaded) loadBaseData();
		}
		return contacts;
	}
	
	public Contact getContact(int id) {
		synchronized(loadingSync) {
			if(!dataLoaded) loadBaseData();
		}
		return contacts.get(id);
	}

	private final ContentObserver observer = new ContentObserver(new Handler()) {
		@Override
		public void onChange(boolean selfChange) {
			// TODO: Deal with changes more efficiently.
			// loadBaseData();
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
		synchronized (loadingSync) {
			loadBaseData();
		}
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
		public void onContactsChanged(Collection<Contact> newContacts);
	}
	
	private static interface LoadingStatusListener {
		public void onBaseDataLoadProgress(float progress);
		public void onAuxiliaryDataLoadProgress(float progress);
		public static final LoadingStatusListener NULL_LISTENER = new LoadingStatusListener() {
			@Override public void onBaseDataLoadProgress(float progress) { }
			@Override public void onAuxiliaryDataLoadProgress(float progress) { }
		};
	}
	
	public synchronized void beginBackgroundLoad() {
		if(dataLoaded) return;
		if(backgroundLoader.getStatus() == Status.RUNNING) return;
		backgroundLoader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	private final AsyncTask<Void, Float, Void> backgroundLoader = new AsyncTask<Void, Float, Void>() {
		
		private int broadcastCount = 0;

		@Override
		protected Void doInBackground(Void... params) {
			synchronized (loadingSync) {
				// In this system we are assuming that base data is 1/4 of the job,
				// aux is 3/4.
				loadData(new LoadingStatusListener() {
					@Override
					public void onBaseDataLoadProgress(float progress) {
						if(broadcastCount++ == 4) {
							Log.v(TAG, "Base data load " + (int)(progress * 100) + "%");
							broadcastCount = 0;
							publishProgress(progress / 4);
						}
					}
					
					@Override
					public void onAuxiliaryDataLoadProgress(float progress) {
						if(broadcastCount++ == 4) {
							Log.v(TAG, "Aux data load " + (int)(progress * 100) + "%");
							broadcastCount = 0;
							publishProgress(0.25f + progress * 0.75f);
						}
					}
				});
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Float... values) {
			for(float value : values) {
				Intent broadcast = new Intent();
				broadcast.setAction(BROADCAST_LOADSTATUS);
				broadcast.putExtra(LOADSTATUS_STATUS, value);
				localBroadcastManager.sendBroadcast(broadcast);
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// Send completion broadcast. A value of 1 indicates completion
			Intent broadcast = new Intent();
			broadcast.setAction(BROADCAST_LOADSTATUS);
			broadcast.putExtra(LOADSTATUS_STATUS, (float)1);
			localBroadcastManager.sendBroadcast(broadcast);
		}
	};
	
	static final String getMimetype(int field) {
		switch(field) {
		case Question.FIELD_DISPLAY_NAME:
			Log.w(TAG, "Display name can't be deleted");
			return null;
		case Question.FIELD_PHOTO:
			Log.w(TAG, "Photo can't be deleted with a mimetype");
			return null;
		case Question.FIELD_FIRST_NAME:
		case Question.FIELD_LAST_NAME:
		case Question.FIELD_COMPANY:
		case Question.FIELD_DEPARTMENT:
		case Question.FIELD_COMPANY_TITLE:
		case Question.FIELD_PHONE_HOME:
		case Question.FIELD_PHONE_WORK:
		case Question.FIELD_PHONE_MOBILE:
		case Question.FIELD_PHONE_OTHER:
		case Question.FIELD_EMAIL_HOME:
		case Question.FIELD_EMAIL_WORK:
		case Question.FIELD_EMAIL_MOBILE:
		case Question.FIELD_EMAIL_OTHER:
		default:
			return null;
		}
	}
	
	/**
	 * Deletes the given field from the given {@link Contact}. If this
	 * field doesn't exist for this {@link Contact}, nothing happens.
	 * TODO: Implement
	 * @param contact
	 * @param field
	 */
	public void deleteContactField(Contact contact, int field) {
		hideContactField(contact, field);
		// TODO: Handle display name and photo
		final int id = contact.getId();
		final String mimetype = getMimetype(field);
		if(mimetype == null) return;
		cr.delete(ContactsContract.Data.CONTENT_URI,
				ContactsContract.Data.CONTACT_ID + " == ? AND " +
						ContactsContract.Data.MIMETYPE + " == ?",
				new String[] {
				String.valueOf(id),
				mimetype
		});
	}
	
	/**
	 * Removes the given field from the given {@link Contact},
	 * but only in the temporary storage. This is used to
	 * avoid the program from having to reload the entire {@link Contact}
	 * list whenever a field is removed or hidden within this app.
	 * @param contact
	 * @param field
	 */
	public void hideContactField(Contact contact, int field) {
		// Get original contact reference
		Contact ref = getContact(contact.getId());
		ref.removeField(field);
	}
	
	/**
	 * Removes the given {@link Contact} from the map and list,
	 * but only in the temporary storage. This is used to avoid
	 * the program having to reload the entire {@link Contact}
	 * list whenever a contact is hidden.
	 * @param contact
	 */
	public void hideContact(Contact contact) {
		contacts.remove(contact.getId());
		contactCollection.remove(contact);
	}
}
