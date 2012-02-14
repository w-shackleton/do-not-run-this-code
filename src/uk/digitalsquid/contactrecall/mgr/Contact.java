package uk.digitalsquid.contactrecall.mgr;

import java.util.Comparator;
import java.util.LinkedList;

import android.graphics.Bitmap;

/**
 * Represents the basic info about a contact in the phone's database.
 * @author william
 *
 */
public class Contact {
	private int id;
	
	private String firstName;
	private String lastName;
	private String displayName;
	
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getFirstName() {
		return firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getLastName() {
		return lastName;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * Loads the photo from the given manager. Returns the first one.
	 */
	public Bitmap getPhoto(PhotoManager mgr) {
		return mgr.getContactPicture(id);
	}
	
	/**
	 * Loads the photo from the given manager. Returns the nth one.
	 */
	public Bitmap getPhoto(PhotoManager mgr, int position) {
		return mgr.getContactPicture(id, position);
	}
	
	/**
	 * Loads the photo from the given manager. Returns all photos.
	 */
	public LinkedList<Bitmap> getPhotos(PhotoManager mgr) {
		return mgr.getContactPictures(id);
	}
	
	@Override
	public String toString() {
		return displayName;
	}
	
	@Override
	public boolean equals(Object c2) {
		if(c2 instanceof Contact)
			return ((Contact) c2).getId() == getId();
		return false;
	}
	
	public static final Comparator<Contact> CONTACT_NAME_COMPARATOR = new Comparator<Contact>() {
		@Override
		public int compare(Contact lhs, Contact rhs) {
			return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());
		}
	};
}
