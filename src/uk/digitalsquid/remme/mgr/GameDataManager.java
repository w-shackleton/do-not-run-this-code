package uk.digitalsquid.remme.mgr;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.misc.Config;
import android.content.Context;

public class GameDataManager implements Config {
	
	private final App app;
	
	public GameDataManager(Context context, App app) {
		this.app = app;
	}
	
	/**
	 * Returns all contacts matching a predicate
	 * @param predicate
	 * @return
	 */
	public LinkedList<Contact> getContactSet(ContactPredicate predicate) {
		final LinkedList<Contact> set = new LinkedList<Contact>();
		final Collection<Contact> potentials = app.getContacts().getContacts();
		for(Contact c : potentials) {
			if(predicate.include(c)) set.add(c);
		}
		return set;
	}
	
	private interface ContactPredicate {
		public boolean include(Contact contact);
	}
	
	public LinkedList<Contact> getAllPhotoContacts() {
		final List<Integer> contactsWithIds = app.getPhotos().getContactsWithPictures();
		return getContactSet(new ContactPredicate() {
			@Override
			public boolean include(Contact contact) {
				return contactsWithIds.contains(contact.getId());
			}
		});
	}
}
