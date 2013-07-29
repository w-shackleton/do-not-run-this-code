package uk.digitalsquid.contactrecall.mgr.details;

import java.util.Comparator;

import uk.digitalsquid.contactrecall.mgr.PhotoManager;
import uk.digitalsquid.contactrecall.mgr.Question;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents the basic info about a contact in the phone's database.
 * @author william
 *
 */
public class Contact implements Parcelable, Comparable<Contact> {
	private int id;
	
	private boolean realContact = true;
	
	private String firstName;
	private String lastName;
	private String displayName;
	
	private final Details details;
	
    private Contact(Parcel in) {
    	id = in.readInt();
    	realContact = in.readInt() == 1;
    	firstName = in.readString();
    	lastName = in.readString();
    	displayName = in.readString();
    	details = new Details();
    }
	
	public Contact() {
		details = new Details();
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
		dest.writeInt(realContact ? 1 : 0);
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(displayName);
		dest.writeParcelable(details, 0);
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
	@Deprecated
	public String getNamePart(int part) {
		switch(part) {
		case Question.FIELD_DISPLAY_NAME:
		default:
			return displayName;
		case Question.FIELD_FIRST_NAME:
			return firstName;
		case Question.FIELD_LAST_NAME:
			return lastName;
		}
	}
	
	/**
	 * Returns a {@link String} representation of the given field.
	 * Note that this shouldn't be shown to the user, but only used
	 * to compare two contacts in a certain aspect.
	 * 
	 */
	public String getStringFieldRepresentation(int part) {
		switch(part) {
		case Question.FIELD_DISPLAY_NAME:
			return displayName;
		case Question.FIELD_FIRST_NAME:
			return firstName;
		case Question.FIELD_LAST_NAME:
			return lastName;
		case Question.FIELD_PHOTO: // Just use ID to differentiate photos.
		default:
			return "" + id;
		}
	}
	
	/**
	 * Returns a human-readable string representation of a field.
	 * If the specified part can't be string represented, the display
	 * name is returned as a fail-safe.
	 */
	public String getTextField(int part) {
		switch(part) {
		default:
		case Question.FIELD_DISPLAY_NAME:
			return displayName;
		case Question.FIELD_FIRST_NAME:
			return firstName;
		case Question.FIELD_LAST_NAME:
			return lastName;
		}
	}
	
	private static final Contact nullContact = new Contact();
	static {
		nullContact.setDisplayName("");
		nullContact.setFirstName("");
		nullContact.setLastName("");
	}

	public static Contact getNullContact() {
		return nullContact;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public int compareTo(Contact another) {
		if(another == null) return 1;
		return getId() - another.getId();
	}

	public Details getDetails() {
		return details;
	}
	
	/**
	 * @param field The field to check for (See {@link Question})
	 * @return <code>true</code> if this contact has the specified field.
	 */
	public boolean hasField(int field) {
		switch(field) {
		case Question.FIELD_DISPLAY_NAME: return displayName != null;
		case Question.FIELD_FIRST_NAME: return firstName != null;
		case Question.FIELD_LAST_NAME: return lastName != null;
		case Question.FIELD_PHOTO: return details.hasPicture();
		default:
			return false;
		}
	}

	public boolean isRealContact() {
		return realContact;
	}

	public void setRealContact(boolean realContact) {
		this.realContact = realContact;
	}
}
