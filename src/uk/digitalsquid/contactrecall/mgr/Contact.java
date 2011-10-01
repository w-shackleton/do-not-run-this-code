package uk.digitalsquid.contactrecall.mgr;

import android.graphics.Bitmap;

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
	public Bitmap getPhoto(ContactManager mgr) {
		return mgr.getContactPicture(id);
	}
}
