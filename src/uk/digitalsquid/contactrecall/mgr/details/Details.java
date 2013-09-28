package uk.digitalsquid.contactrecall.mgr.details;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Holds more specific details about a contact
 * @author william
 *
 */
public class Details implements Parcelable {
	
	Details() {}
	
	/**
	 * The company / organisation the user works for
	 */
	private String company;
	
	private String department;

	/**
	 * The contact's title within their company
	 */
	private String companyTitle;
	
	private String homePhone;
	private String workPhone;
	private String mobilePhone;
	private String otherPhone;

	private String homeEmail;
	private String workEmail;
	private String mobileEmail;
	private String otherEmail;
	
	// The number of other details is just getting silly. From here on,
	// we'll just store the more rare details in a HashMap
	@SuppressLint("UseSparseArrays")
	public HashMap<Integer, String> otherDetails = new HashMap<Integer, String>();
	
	/**
	 * If <code>true</code>, indicates that this contact has a picture.
	 */
	private boolean picture;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(company);
		dest.writeString(department);
		dest.writeString(companyTitle);
		dest.writeString(homePhone);
		dest.writeString(workPhone);
		dest.writeString(mobilePhone);
		dest.writeString(otherPhone);

		dest.writeString(homeEmail);
		dest.writeString(workEmail);
		dest.writeString(mobileEmail);
		dest.writeString(otherEmail);

		dest.writeMap(otherDetails);
		
		dest.writeInt(picture ? 1 : 0);
	}
	
	public static final Parcelable.Creator<Details> CREATOR =
			new Creator<Details>() {
				@Override
				public Details[] newArray(int size) {
					return new Details[size];
				}
				
				@Override
				public Details createFromParcel(Parcel source) {
					return new Details(source);
				}
	};
	
	@SuppressWarnings("unchecked")
	private Details(Parcel in) {
		company = in.readString();
		department = in.readString();
		companyTitle = in.readString();
		homePhone = in.readString();
		workPhone = in.readString();
		mobilePhone = in.readString();
		otherPhone = in.readString();

		otherDetails = in.readHashMap(String.class.getClassLoader());
		
		picture = in.readInt() == 1;
	}

	public String getCompanyTitle() {
		return companyTitle;
	}

	public void setCompanyTitle(String companyTitle) {
		this.companyTitle = companyTitle;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public void setWorkPhone(String workPhone) {
		this.workPhone = workPhone;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getOtherPhone() {
		return otherPhone;
	}

	public void setOtherPhone(String otherPhone) {
		this.otherPhone = otherPhone;
	}

	public String getHomeEmail() {
		return homeEmail;
	}

	public void setHomeEmail(String homeEmail) {
		this.homeEmail = homeEmail;
	}

	public String getWorkEmail() {
		return workEmail;
	}

	public void setWorkEmail(String workEmail) {
		this.workEmail = workEmail;
	}

	public String getMobileEmail() {
		return mobileEmail;
	}

	public void setMobileEmail(String mobileEmail) {
		this.mobileEmail = mobileEmail;
	}

	public String getOtherEmail() {
		return otherEmail;
	}

	public void setOtherEmail(String otherEmail) {
		this.otherEmail = otherEmail;
	}

	public boolean hasPicture() {
		return picture;
	}

	public void setHasPicture(boolean picture) {
		this.picture = picture;
	}
	
	public String getOtherDetail(int field) {
		return otherDetails.get(field);
	}
	public boolean hasOtherDetail(int field) {
		return otherDetails.containsKey(field);
	}
	public void setOtherDetail(int field, String value) {
		otherDetails.put(field, value);
	}
	public void removeOtherDetail(int field) {
		otherDetails.remove(field);
	}
}
