package uk.digitalsquid.remme.mgr.details;

import java.util.Comparator;

import uk.digitalsquid.remme.mgr.PhotoManager;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.misc.Config;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Represents the basic info about a contact in the phone's database.
 * @author william
 *
 */
public final class Contact implements Parcelable, Comparable<Contact>, Config {
	private int id;
	
	private String lookupKey;
	
	private String firstName;
	private String lastName;
	private String displayName;
	
	private final Details details;
	
	private transient int customSortField;
	
	private boolean photoRemoved = false;
	
    private Contact(Parcel in) {
    	id = in.readInt();
    	setLookupKey(in.readString());
    	firstName = in.readString();
    	lastName = in.readString();
    	displayName = in.readString();
    	details = in.readParcelable(Details.class.getClassLoader());
    	photoRemoved = in.readInt() == 1;
    }
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(getLookupKey());
		dest.writeString(firstName);
		dest.writeString(lastName);
		dest.writeString(displayName);
		dest.writeParcelable(details, 0);
		dest.writeInt(photoRemoved ? 1 : 0);
	}
	
	public Contact() {
		details = new Details();
	}

	public void setId(int id) {
		this.id = id;
	}
	/**
	 * Gets the id of this contact. Note that it is <b>much</b> better to use
	 * getLookupKey for referencing a contact
	 * @return
	 */
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
		if(photoRemoved) return null;
		return getPhoto(mgr, true);
	}
	
	/**
	 * Loads the photo from the given manager. Returns the first one.
	 */
	public Bitmap getPhoto(PhotoManager mgr, boolean highRes) {
		if(photoRemoved) return null;
		return mgr.getContactPicture(this, highRes);
	}
	
	@Override
	public String toString() {
		return displayName;
	}
	
	@Override
	public boolean equals(Object c2) {
		if(c2 instanceof Contact)
			return ((Contact) c2).getLookupKey().equals(getLookupKey());
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
	public static final Comparator<Contact> CUSTOM_COMPARATOR = new Comparator<Contact>() {
		@Override
		public int compare(Contact lhs, Contact rhs) {
			if(lhs == null) return -1;
			if(rhs == null) return 1;
			return lhs.getCustomSortField() - rhs.getCustomSortField();
		}
	};

	@Override
	public int describeContents() {
		return 0;
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
			return lookupKey;
		}
	}
	
	/**
	 * Returns a human-readable string representation of a field.
	 * If the specified part can't be string represented, the display
	 * name is returned as a fail-safe.
	 * Also, if this {@link Contact} doesn't have the specified field
	 * then the display name is returned, also as a fail-safe.
	 */
	public String getTextField(int field) {
		if(!hasField(field)) return displayName;
		switch(field) {
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
		// The start of the 'other' fields
		if(field >= Question.FIELD_OTHERS_START) {
			String result = details.getOtherDetail(field);
			if(result == null) return displayName;
			return result;
		}
		return displayName;
	}

	/**
	 * Removes the given field from this contact.
	 * @param field
	 */
	public void removeField(int field) {
		switch(field) {
		// case Question.FIELD_DISPLAY_NAME:	displayName = null; return;
		case Question.FIELD_PHOTO:
			photoRemoved = true;
			details.setHasPicture(false);
			return;
		case Question.FIELD_FIRST_NAME:		firstName = null; return;
		case Question.FIELD_LAST_NAME:		lastName = null; return;
		case Question.FIELD_COMPANY:		details.setCompany(null); return;
		case Question.FIELD_DEPARTMENT:		details.setDepartment(null); return;
		case Question.FIELD_COMPANY_TITLE:	details.setCompanyTitle(null); return;
		case Question.FIELD_PHONE_HOME:		details.setHomePhone(null); return;
		case Question.FIELD_PHONE_WORK:		details.setWorkPhone(null); return;
		case Question.FIELD_PHONE_MOBILE:	details.setMobilePhone(null); return;
		case Question.FIELD_PHONE_OTHER:	details.setOtherPhone(null); return;
		case Question.FIELD_EMAIL_HOME:		details.setHomeEmail(null); return;
		case Question.FIELD_EMAIL_WORK:		details.setWorkEmail(null); return;
		case Question.FIELD_EMAIL_MOBILE:	details.setMobileEmail(null); return;
		case Question.FIELD_EMAIL_OTHER:	details.setOtherEmail(null); return;
		}
		if(field >= Question.FIELD_OTHERS_START) {
			details.removeOtherDetail(field);
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
		return lookupKey.hashCode();
	}

	@Override
	public int compareTo(Contact another) {
		if(another == null) return 1;
		return lookupKey.compareTo(another.lookupKey);
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
		}
		if(field >= Question.FIELD_OTHERS_START) {
			return details.hasOtherDetail(field);
		}
		return false;
	}

	public int getCustomSortField() {
		return customSortField;
	}

	public void setCustomSortField(int customSortField) {
		this.customSortField = customSortField;
	}
	
	public void setOtherDetail(int field, String value) {
		if(field >= Question.FIELD_OTHERS_START) {
			details.setOtherDetail(field, value);
		} else {
			Log.w(TAG, "setOtherDetail called with a static detail value");
		}
	}
	
	public Uri getLookupUri() {
		return Uri.parse(ContactsContract.Contacts.CONTENT_LOOKUP_URI + "/" + getLookupKey());
	}

	public String getLookupKey() {
		return lookupKey;
	}

	public void setLookupKey(String lookupKey) {
		this.lookupKey = lookupKey;
	}
}
