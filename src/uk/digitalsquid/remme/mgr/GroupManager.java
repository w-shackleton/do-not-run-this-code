package uk.digitalsquid.remme.mgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import uk.digitalsquid.remme.mgr.db.DB;
import uk.digitalsquid.remme.misc.Config;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.SparseArray;

/**
 * Manages contact groups
 * @author william
 *
 */
public class GroupManager implements Config {
	private final Context context;
	private final ContentResolver cr;
	private final DB db;
	
	public GroupManager(Context context, DB db) {
		this.context = context;
		cr = context.getContentResolver();
		this.db = db;
	}
	
	@SuppressLint("UseSparseArrays")
	public Map<Integer, Group> getContactGroups() {
		final Map<Integer, Group> data = new HashMap<Integer, Group>();
		
		Cursor cur = cr.query(ContactsContract.Groups.CONTENT_URI, new String[] {
				ContactsContract.Groups._ID,
				ContactsContract.Groups.TITLE,
				ContactsContract.Groups.ACCOUNT_NAME,
				ContactsContract.Groups.ACCOUNT_TYPE,
				ContactsContract.Groups.GROUP_VISIBLE
		}, null, null, null);
		if(cur.getCount() > 0) {
			final int titleIndex = cur.getColumnIndex(ContactsContract.Groups.TITLE);
			final int idIndex = cur.getColumnIndex(ContactsContract.Groups._ID);
			final int accountNameIdx = cur.getColumnIndex(ContactsContract.Groups.ACCOUNT_NAME);
			final int accountTypeIdx = cur.getColumnIndex(ContactsContract.Groups.ACCOUNT_TYPE);
			final int visibleIdx = cur.getColumnIndex(ContactsContract.Groups.GROUP_VISIBLE);
			while(cur.moveToNext()) {
				String groupName = cur.getString(titleIndex);
				int id = cur.getInt(idIndex);
				
				String accountName = cur.getString(accountNameIdx);
				String accountType = cur.getString(accountTypeIdx);

				boolean visible = cur.getInt(visibleIdx) == 1;
				Group group = new Group(id, groupName, visible);
				group.accountName = accountName;
				group.accountType = accountType;
				data.put(id, group);
			}
		}
		cur.close();
		
		return data;
	}
	
	/**
	 * 
	 * @param visibleGroupsOnly If true, will only return mappings for visibleInContacts groups
	 * @return A map from group IDs to the contacts in that group.
	 */
	public SparseArray<List<Integer>> getGroupContactRelations(boolean visibleGroupsOnly) {
		final SparseArray<List<Integer>> data = new SparseArray<List<Integer>>();
		final String groupWhere = ContactsContract.Data.MIMETYPE + " = ?";
		
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI, new String[] {
				ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID,
				ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID },
				groupWhere, new String[] {ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE}, null);
		
		Map<Integer, Group> visibleGroups = null;
		if(visibleGroupsOnly) {
			visibleGroups = getContactGroups();
		}
		
		if(cur.getCount() > 0) {
			final int contactIdIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);
			final int groupIdIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID);
			while(cur.moveToNext()) {
				int contactId = cur.getInt(contactIdIndex);
				int groupId = cur.getInt(groupIdIndex);
				
				// Check if group is visibleInContacts
				if(visibleGroups != null) {
					Group g = visibleGroups.get(groupId);
					if(g != null) if(!g.visibleInContacts) continue; // Don't add if invisible
				}
				
				if(data.get(groupId) == null)
					data.put(groupId, new LinkedList<Integer>());
				data.get(groupId).add(contactId);
			}
		}
		cur.close();
		return data;
	}
	
	public static class Group implements Parcelable {

		public final String name;
		public final int id;
		
		public final boolean visibleInContacts;
		
		public String accountName, accountType;
		
		protected Group(int id, String name, boolean visible) {
			this.id = id;
			this.name = name;
			this.visibleInContacts = visible;
		}
		
		private Group(Parcel in) {
			name = in.readString();
			id = in.readInt();
			visibleInContacts = in.readInt() == 1;
			accountName = in.readString();
			accountType = in.readString();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(name);
			dest.writeInt(id);
			dest.writeInt(visibleInContacts ? 1 : 0);
			dest.writeString(accountName);
			dest.writeString(accountType);
		}
		
		@Override
		public String toString() {
			return String.format(Locale.ENGLISH, "%s(%d)%s - %s(%s)", name, id, visibleInContacts ? "[visibleInContacts]" : "",
					accountName, accountType);
		}

		@Override
		public int describeContents() {
			return 0;
		}
		
		public static final Parcelable.Creator<Group> CREATOR = new Creator<GroupManager.Group>() {
			@Override
			public Group[] newArray(int size) {
				return new Group[size];
			}
			
			@Override
			public Group createFromParcel(Parcel source) {
				return new Group(source);
			}
		};
	}
	
	public static final class AccountDetails implements Parcelable {
		
		private String accountName;
		private String accountType;
		
		private String packageName;
		private int labelId, iconId;
		
		private ArrayList<Group> groups;
		
		public AccountDetails() {
			setGroups(new ArrayList<GroupManager.Group>());
		}

		@SuppressWarnings("unchecked")
		private AccountDetails(Parcel in) {
			accountName = in.readString();
			accountType = in.readString();
			packageName = in.readString();
			labelId = in.readInt();
			iconId = in.readInt();
			setGroups(in.readArrayList(Group.class.getClassLoader()));
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(accountName);
			dest.writeString(accountType);
			dest.writeString(packageName);
			dest.writeInt(labelId);
			dest.writeInt(iconId);
			dest.writeList(getGroups());
		}
		
		public static final Parcelable.Creator<AccountDetails> CREATOR = new Creator<GroupManager.AccountDetails>() {
			@Override
			public AccountDetails[] newArray(int size) {
				return new AccountDetails[size];
			}
			
			@Override
			public AccountDetails createFromParcel(Parcel source) {
				return new AccountDetails(source);
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public String getAccountType() {
			return accountType;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}

		public String getPackageName() {
			return packageName;
		}

		public void setPackageName(String packageName) {
			this.packageName = packageName;
		}

		private int getLabelId() {
			return labelId;
		}

		private void setLabelId(int labelId) {
			this.labelId = labelId;
		}

		private int getIconId() {
			return iconId;
		}

		private void setIconId(int iconId) {
			this.iconId = iconId;
		}
		
		public CharSequence getLabel(Context context) {
			final PackageManager pm = context.getPackageManager();
			if(packageName == null) return accountType;
			if(getLabelId() == 0) return accountType;
			CharSequence label = pm.getText(packageName, getLabelId(), null);
			if(label == null) return accountType;
			return label;
		}
		
		public Drawable getIcon(Context context) {
			final PackageManager pm = context.getPackageManager();
			if(packageName == null) return null;
			if(getIconId() == 0) return null;
			return pm.getDrawable(packageName, getIconId(), null);
		}

		public ArrayList<Group> getGroups() {
			return groups;
		}

		private void setGroups(ArrayList<Group> groups) {
			this.groups = groups;
		}
		
		void addGroup(Group group) {
			groups.add(group);
		}
		
		public String toString(Context context) {
			return String.format("Account %s (%s:%s)",
					getLabel(context),
					getAccountName(),
					getAccountType());
		}
	}
	
	/**
	 * Collects details about accounts, and groups within those accounts.
	 * Basically, everything the user can use to select contacts.
	 */
	public ArrayList<AccountDetails> getAccountDetails() {
		AccountManager accountManager = AccountManager.get(context);
		AuthenticatorDescription[] accountTypes = accountManager.getAuthenticatorTypes();
		Account[] accounts = accountManager.getAccounts();
		
		ArrayList<AccountDetails> accountDetails = new ArrayList<AccountDetails>();

		// Gather account information
		for(Account account : accounts) {
			AccountDetails details = new AccountDetails();
			details.setAccountName(account.name);
			details.setAccountType(account.type);
			
			AuthenticatorDescription accountType =
					getAuthenticatorDescription(accountTypes, account);
			if(accountType != null) {
				details.setLabelId(accountType.labelId);
				details.setIconId(accountType.iconId);
				details.setPackageName(accountType.packageName);
			}

			accountDetails.add(details);
		}
		
		// Gather group information
		final Map<Integer, Group> groups = getContactGroups();
		for(Group group : groups.values()) {
			// Find correct AccountDetails
			for(AccountDetails details : accountDetails) {
				if(details.getAccountName().equals(group.accountName) &&
						details.getAccountType().equals(group.accountType)) {
					details.addGroup(group);
				}
			}
		}
		return accountDetails;
	}
	
	private static AuthenticatorDescription getAuthenticatorDescription(
			AuthenticatorDescription[] descs, Account account) {
		for(AuthenticatorDescription desc : descs) {
			if(desc.type.equals(account.type))
				return desc;
		}
		return null;
	}
}
