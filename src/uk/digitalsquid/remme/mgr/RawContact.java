package uk.digitalsquid.remme.mgr;

/**
 * A raw contact
 * @author william
 *
 */
public class RawContact {
	private String accountName;
	private int contactId;
	private int id;
	
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public int getContactId() {
		return contactId;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAccountName() {
		return accountName;
	}
}
