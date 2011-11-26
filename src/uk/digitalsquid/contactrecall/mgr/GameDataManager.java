package uk.digitalsquid.contactrecall.mgr;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.misc.Config;
import android.content.Context;

public class GameDataManager implements Config {
	
	private final Context context;
	private final App app;
	
	public GameDataManager(Context context, App app) {
		this.context = context;
		this.app = app;
	}
	
	public LinkedList<Contact> getRandomContactSet(ContactPredicate predicate, int number) {
		final LinkedList<Contact> set = new LinkedList<Contact>();
		final List<Contact> potentials = app.getContacts().getContacts();
		for(Contact c : potentials) {
			if(predicate.include(c)) set.add(c);
		}
		Collections.shuffle(potentials);
		int size = potentials.size();
		for(int i = number; i < size; i++) {
			potentials.remove(i);
		}
		return set;
	}
	
	private abstract class ContactPredicate {
		public abstract boolean include(Contact contact);
	}
	
	public LinkedList<Contact> getRandomPhotoSet(int number) {
		return getRandomContactSet(new ContactPredicate() {
			@Override
			public boolean include(Contact contact) {
				return app.getPhotos().getContactPictureCount(contact.getId()) > 0;
			}
		}, number);
	}
}
