package uk.digitalsquid.contactrecall.mgr.details;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a single piece of data from a {@link Contact}.
 * @author william
 *
 */
public class DataItem implements Parcelable {
	
	private Contact contact;
	
	private int field;
	
	public DataItem() {
		
	}

	public DataItem(Contact contact, int field) {
		this.contact = contact;
		this.field = field;
	}
	
	protected DataItem(Parcel in) {
		contact = in.readParcelable(Contact.class.getClassLoader());
		field = in.readInt();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(contact, 0);
		dest.writeInt(field);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public int getField() {
		return field;
	}

	public void setField(int field) {
		this.field = field;
	}

	public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
		@Override
		public DataItem[] newArray(int size) {
			return new DataItem[size];
		}
		
		@Override
		public DataItem createFromParcel(Parcel source) {
			return new DataItem(source);
		}
	};
}
