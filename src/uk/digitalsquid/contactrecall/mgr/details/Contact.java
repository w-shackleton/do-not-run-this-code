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
	
	private String firstName;
	private String lastName;
	private String displayName;
	
	private final Details details;
	
    private Contact(Parcel in) {
    	id = in.readInt();
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
	 * Also, if this {@link Contact} doesn't have the specified field
	 * then the display name is returned, also as a fail-safe.
	 */
	public String getTextField(int part) {
		if(!hasField(part)) return displayName;
		switch(part) {
		default:
		case Question.FIELD_DISPLAY_NAME:	return displayName;
		case Question.FIELD_FIRST_NAME:		return firstName;
		case Question.FIELD_LAST_NAME:		return lastName;
		case Question.FIELD_COMPANY:		return details.getCompany();
		case Question.FIELD_DEPARTMENT:		return details.getDepartment();
		case Question.FIELD_COMPANY_TITLE:	return details.getCompanyTitle();
		case Question.FIELD_PHONE_HOME:		return details.getHomePhone();
		case Question.FIELD_PHONE_WORK:		return details.getWorkPhone();
		case Question.FIELD_PHONE_MOBILE:	return details.getMobilePhone();
		case Question.FIELD_PHONE_OTHER:	return details.getOtherPhone();
		case Question.FIELD_EMAIL_HOME:		return details.getHomeEmail();
		case Question.FIELD_EMAIL_WORK:		return details.getWorkEmail();
		case Question.FIELD_EMAIL_MOBILE:	return details.getMobileEmail();
		case Question.FIELD_EMAIL_OTHER:	return details.getOtherEmail();
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

		case Question.FIELD_COMPANY:		return details.getCompany() != null;
		case Question.FIELD_DEPARTMENT:		return details.getDepartment() != null;
		case Question.FIELD_COMPANY_TITLE:	return details.getCompanyTitle() != null;
		case Question.FIELD_PHONE_HOME:		return details.getHomePhone() != null;
		case Question.FIELD_PHONE_WORK:		return details.getWorkPhone() != null;
		case Question.FIELD_PHONE_MOBILE:	return details.getMobilePhone() != null;
		case Question.FIELD_PHONE_OTHER:	return details.getOtherPhone() != null;
		case Question.FIELD_EMAIL_HOME:		return details.getHomeEmail() != null;
		case Question.FIELD_EMAIL_WORK:		return details.getWorkEmail() != null;
		case Question.FIELD_EMAIL_MOBILE:	return details.getMobileEmail() != null;
		case Question.FIELD_EMAIL_OTHER:	return details.getOtherEmail() != null;
		default:
				return false;
		}
	}
}
