package uk.digitalsquid.remme.mgr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.misc.Config;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
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
			"uk.digitalsquid.remme.mgr.ContactManager.LoadStatus";
	public static final String LOADSTATUS_STATUS =
			"uk.digitalsquid.remme.mgr.ContactManager.LoadStatus.Value";

	private final ContentResolver cr;
	
	private HashMap<String, Contact> contacts;
	/**
	 * A map of {@link Contact}, indexed by ID. This is only to be used for
	 * non-persistent data.
	 */
	private HashMap<Integer, Contact> contactIdMap;
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
		Log.d(TAG, "Starting load");
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
				new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.LOOKUP_KEY,
				},
				null, null, ContactsContract.Contacts._ID + " ASC");
		
		contacts = new HashMap<String, Contact>();
		contactIdMap = new HashMap<Integer, Contact>();
		
		// Get hidden contacts
		Log.d(TAG, "Loading hidden contacts");
		Set<String> hiddenContacts = app.getDb().hidden.getHiddenContacts();
		Log.d(TAG, "Loading visible contact data");
		Set<Integer> visibleContactsByAccount = app.getGroups().getContactsVisibleByAccount();
		Log.d(TAG, "Loading group relations");
		SparseArray<List<Integer>> groupContactRelations = app.getGroups().getVisibleGroupContactRelations();
		
		
		
		// Load base data and populate HashMap
		int total = cur.getCount();
		int i = 0;
		if(total > 0) {
			while(cur.moveToNext()) {
				String key = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				if(hiddenContacts.contains(key)) continue;

				int id = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts._ID));
				String displayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				
				if(isSaneName(displayName)) {
					Contact contact = new Contact();
					contact.setId(id);
					contact.setDisplayName(displayName);
					contact.setLookupKey(key);
					
					contacts.put(contact.getLookupKey(), contact);
					contactIdMap.put(contact.getId(), contact);
				}
				i++;
				if(i % 10 == 0)
					listener.onBaseDataLoadProgress((float)i / (float)total);
			}
		}
		cur.close();
		
		Log.d(TAG, "Loaded base");
		
		Log.d(TAG, "Filtering based on group data");
		// Mark all contacts that belong to a selected group.
		for(int pos = 0; pos < groupContactRelations.size(); pos++) {
			List<Integer> relations = groupContactRelations.valueAt(pos);
			if(relations == null) continue;
			for(Integer contactId : relations) {
				Contact contact = contactIdMap.get((int)contactId);
				if(contact == null) continue;
				contact.setInVisibleGroup(true);
			}
		}
		Log.d(TAG, "Filtering based on account data");
		for(Iterator<Entry<String, Contact>> it = contacts.entrySet().iterator();
				it.hasNext(); ) {
			Contact contact = it.next().getValue();
			boolean accountSelected = visibleContactsByAccount.contains(contact.getId());
			boolean groupSelected = contact.isInVisibleGroup();
			Log.v(TAG, String.format("A:%b G:%b %s", accountSelected, groupSelected, contact.getDisplayName()));
			if(!(accountSelected || groupSelected)) {
				it.remove();
				contactIdMap.remove(contact.getId());
				Log.v(TAG,     "    Removing   : " + contact.getDisplayName());
			} else {
				if(accountSelected && groupSelected)
					Log.v(TAG, "Account & group: " + contact.getDisplayName());
				else if(groupSelected)
					Log.v(TAG, "          group: " + contact.getDisplayName());
				else
					Log.v(TAG, "Account        : " + contact.getDisplayName());
			}
		}
		Log.v(TAG, String.format("%d in accounts", visibleContactsByAccount.size()));
		
		Log.d(TAG, "Querying aux data");

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
				ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
				ContactsContract.CommonDataKinds.Website.URL,
				ContactsContract.CommonDataKinds.Relation.NAME,
				ContactsContract.CommonDataKinds.Relation.TYPE,
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
		int addressTypeIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.TYPE);
		int addressIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);
		int websiteIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Website.URL);
		int relationNameIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Relation.NAME);
		int relationTypeIdx	= cur.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Relation.TYPE);

		total = cur.getCount();
		i = 0;
		while(cur.moveToNext()) {
			i++;
			Contact contact = contactIdMap.get(cur.getInt(idIdx));
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
			else if(mime.equals(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)) {
				switch(cur.getInt(addressTypeIdx)) {
				case StructuredPostal.TYPE_HOME:
					contact.getDetails().setOtherDetail(Question.FIELD_ADDRESS_HOME, cur.getString(addressIdx));
					break;
				case StructuredPostal.TYPE_WORK:
					contact.getDetails().setOtherDetail(Question.FIELD_ADDRESS_WORK, cur.getString(addressIdx));
					break;
				case StructuredPostal.TYPE_OTHER:
					contact.getDetails().setOtherDetail(Question.FIELD_ADDRESS_OTHER, cur.getString(addressIdx));
					break;
				}
			}
			else if(mime.equals(ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)) {
				String website = cur.getString(websiteIdx);
				// No Google+ profiles
				if(website.contains("google.com/profiles")) break;
				contact.setOtherDetail(Question.FIELD_WEBSITE, website);
			}
			else if(mime.equals(ContactsContract.CommonDataKinds.Relation.CONTENT_ITEM_TYPE)) {
				String name = cur.getString(relationNameIdx);
				switch(cur.getInt(relationTypeIdx)) {
				case ContactsContract.CommonDataKinds.Relation.TYPE_ASSISTANT:
					contact.setOtherDetail(Question.FIELD_ASSISTANT, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_BROTHER:
					contact.setOtherDetail(Question.FIELD_BROTHER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_CHILD:
					contact.setOtherDetail(Question.FIELD_CHILD, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_DOMESTIC_PARTNER:
					contact.setOtherDetail(Question.FIELD_DOMESTIC_PARTNER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_FATHER:
					contact.setOtherDetail(Question.FIELD_FATHER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_FRIEND:
					contact.setOtherDetail(Question.FIELD_FRIEND, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_MANAGER:
					contact.setOtherDetail(Question.FIELD_MANAGER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_MOTHER:
					contact.setOtherDetail(Question.FIELD_MOTHER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_PARENT:
					contact.setOtherDetail(Question.FIELD_PARENT, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_PARTNER:
					contact.setOtherDetail(Question.FIELD_PARTNER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_REFERRED_BY:
					contact.setOtherDetail(Question.FIELD_REFERRED_BY, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_RELATIVE:
					contact.setOtherDetail(Question.FIELD_RELATIVE, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_SISTER:
					contact.setOtherDetail(Question.FIELD_SISTER, name); break;
				case ContactsContract.CommonDataKinds.Relation.TYPE_SPOUSE:
					contact.setOtherDetail(Question.FIELD_SPOUSE, name); break;
				}
			}
		}
		cur.close();
		
		Log.d(TAG, "Loaded aux");

		// Fill-in photo column
		List<Integer> contactsWithPictures = app.getPhotos().getContactsWithPictures();
		
		Log.d(TAG, "Loaded photos");

		total = contactsWithPictures.size();
		i = 0;
		for(int id : contactsWithPictures) {
			Contact contact = contactIdMap.get(id);
			if(contact == null) continue;
			contact.getDetails().setHasPicture(true);
			if(i % 10 == 0)
				listener.onPhotoLoadProgress((float)(i) / (float)total);
			i++;
		}
		
		// Now, remove all fields that the user wants to hide.
		for(Entry<String, List<Integer>> pair : app.getDb().hidden.getHiddenFields().entrySet()) {
			Contact contact = contacts.get(pair.getKey());
			if(contact == null) continue;
			for(Integer field : pair.getValue()) {
				contact.removeField(field);
			}
		}
		
		// Finally, remove any suspiciously incorrect fields.
		final int[] nameFields = {
				Question.FIELD_DISPLAY_NAME,
				Question.FIELD_FIRST_NAME,
				Question.FIELD_LAST_NAME
				};
		for(Contact contact : contacts.values()) {
			for(int field : nameFields) {
				if(contact.hasField(field)) {
					if(!isSaneName(contact.getTextField(field))) {
						Log.v(TAG, "Removing " + contact.getTextField(field));
						contact.removeField(field);
					}
				}
			}
		}
		
		// Convert to a Set for ease of use
		contactCollection = contacts.values();
		dataLoaded = true;
		Log.i(TAG, "Loading contacts completed");
	}
	
	/**
	 * Checks for a few obviously wrong names that I noticed in the wild
	 * @param name
	 * @return
	 */
	final static boolean isSaneName(String name) {
		// Currently, just delete stuff that is numbers, symbols and whitespace
		// No names are 1 letter alphanum right?
		if(name.contains("@") && !name.contains(" ")) return false;
		return !name.matches("^[ 0-9+-,.;\\s]+$") && !name.matches("^[a-zA-Z].$");
	}
	
	public Collection<Contact> getContacts() {
		synchronized(loadingSync) {
			if(!dataLoaded) loadBaseData();
		}
		return contactCollection;
	}

	public Map<String, Contact> getContactMap() {
		synchronized(loadingSync) {
			if(!dataLoaded) loadBaseData();
		}
		return contacts;
	}
	
	public Contact getContact(String lookupKey) {
		synchronized(loadingSync) {
			if(!dataLoaded) loadBaseData();
		}
		return contacts.get(lookupKey);
	}
	
	private static interface LoadingStatusListener {
		public void onBaseDataLoadProgress(float progress);
		public void onAuxiliaryDataLoadProgress(float progress);
		public void onPhotoLoadProgress(float progress);
		public void onFilterProgress(float progress);
		public static final LoadingStatusListener NULL_LISTENER = new LoadingStatusListener() {
			@Override public void onBaseDataLoadProgress(float progress) { }
			@Override public void onAuxiliaryDataLoadProgress(float progress) { }
			@Override public void onPhotoLoadProgress(float progress) { }
			@Override public void onFilterProgress(float progress) { }
		};
	}
	
	/**
	 * Begins an async load.
	 * @return <code>true</code> if loading was started, <code>false</code> if
	 * already loaded.
	 */
	public synchronized boolean beginBackgroundLoad() {
		if(dataLoaded) return false;
		if(backgroundLoader != null) return false;
		new BackgroundLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		return true;
	}
	
	/**
	 * Reloads the data into the game; reinitialises this class.
	 */
	public synchronized void beginBackgroundReload() {
		new BackgroundLoader().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	private BackgroundLoader backgroundLoader;
	
	private final class BackgroundLoader extends AsyncTask<Void, Float, Void> {
		
		private int broadcastCount = 0;

		@Override
		protected Void doInBackground(Void... params) {
			backgroundLoader = this;
			synchronized (loadingSync) {
				dataLoaded = false;
				contacts = null;
				contactIdMap = null;
				contactCollection = null;
				// In this system we are assuming that base data is 1/4 of the job,
				// aux is 3/4.
				loadData(new LoadingStatusListener() {
					@Override
					public void onBaseDataLoadProgress(float progress) {
						if(broadcastCount++ == 4) {
							broadcastCount = 0;
							Log.v(TAG, "Base data load " + (int)(progress * 100) + "%");
							publishProgress(progress / 4);
						}
					}
					
					@Override
					public void onAuxiliaryDataLoadProgress(float progress) {
						if(broadcastCount++ == 4) {
							broadcastCount = 0;
							Log.v(TAG, "Aux data load " + (int)(progress * 100) + "%");
							publishProgress(0.25f + progress * 0.60f);
						}
					}

					@Override
					public void onPhotoLoadProgress(float progress) {
						if(broadcastCount++ == 4) {
							broadcastCount = 0;
							Log.v(TAG, "Photo data load " + (int)(progress * 100) + "%");
							publishProgress(0.85f + progress * 0.10f);
						}
					}

					@Override
					public void onFilterProgress(float progress) {
						if(broadcastCount++ == 4) {
							broadcastCount = 0;
							Log.v(TAG, "Filter load " + (int)(progress * 100) + "%");
							publishProgress(0.95f + progress * 0.05f);
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
			// Remove own reference
			backgroundLoader = null;
			// Send completion broadcast. A value of 1 indicates completion
			Intent broadcast = new Intent();
			broadcast.setAction(BROADCAST_LOADSTATUS);
			broadcast.putExtra(LOADSTATUS_STATUS, (float)1);
			localBroadcastManager.sendBroadcast(broadcast);
		}
	};
	
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
		Contact ref = getContact(contact.getLookupKey());
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
		contacts.remove(contact.getLookupKey());
		contactCollection.remove(contact);
	}
}
