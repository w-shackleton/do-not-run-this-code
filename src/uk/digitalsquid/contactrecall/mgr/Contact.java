package uk.digitalsquid.contactrecall.mgr;

import java.util.Comparator;
import java.util.LinkedList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the basic info about a contact in the phone's database.
 * @author william
 *
 */
public class Contact implements Parcelable {
	private int id;
	
	private String firstName;
	private String lastName;
	private String displayName;
	
    private Contact(Parcel in) {
    	id = in.readInt();
    	firstName = in.readString();
    	lastName = in.readString();
    	displayName = in.readString();
    }
	
	public Contact() {
	}

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
	 * Loads the photo from the given manager.
	 * A high-resolution photo can be requested
	 */
	public Bitmap getPhoto(PhotoManager mgr) {
		return getPhoto(mgr, true);
	}
	
	/**
	 * Loads the photo from the given manager. Returns the first one.
	 */
	public Bitmap getPhoto(PhotoManager mgr, boolean highRes) {
		return mgr.getContactPicture(id, highRes);
	}
	
	/**
	 * Loads the photo from the given manager. Returns the nth one.
	 */
	@Deprecated
	public Bitmap getPhoto(OldPhotoManager mgr, int position) {
		return mgr.getContactPicture(id, position);
	}
	
	/**
	 * Loads the photo from the given manager. Returns all photos.
	 */
	@Deprecated
	public LinkedList<Bitmap> getPhotos(OldPhotoManager mgr) {
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
			if(lhs == null) return -1;
			if(rhs == null) return 1;
			return lhs.getDisplayName().compareToIgnoreCase(rhs.getDisplayName());
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(displayName);
	}
	
	public static final Parcelable.Creator<Contact> CREATOR =
			new Creator<Contact>() {
				@Override
				public Contact[] newArray(int size) {
					return new Contact[size];
				}
				
				@Override
				public Contact createFromParcel(Parcel source) {
					return new Contact(source);
				}
	};
			
	/**
	 * Gets the part of a name. The part is defined in {@link Question}.
	 * If an invalid part is given, the display name is returned as a fail-safe.
	 * @param part
	 * @return
	 */
	public String getNamePart(int part) {
		switch(part) {
		case Question.TYPE_DISPLAY_NAME:
		default:
			return displayName;
		case Question.TYPE_FIRST_NAME:
			return firstName;
		case Question.TYPE_LAST_NAME:
			return lastName;
		}
	}
	
	/**
	 * Returns a null contact - one with blank details.
	 * @return
	 */
	public static final Contact getNullContact() {
		Contact c = new Contact();
		// TODO: Localise
		c.id = -1;
		c.displayName = "MissingNo";
		c.firstName = "Not found";
		c.lastName = "Not found";
		return c;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
}
