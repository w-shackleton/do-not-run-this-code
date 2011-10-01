package uk.digitalsquid.contactrecall;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.mgr.ContactManager.ContactChangeListener;
import uk.digitalsquid.contactrecall.mgr.GroupManager.Group;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

/**
 * Shows the contact list, allows contact viewing and editing.
 * @author william
 *
 */
public class ContactViewer extends Activity implements ContactChangeListener {
	private App app;
	
	private ListView lv;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.contactviewer);
		app = (App) getApplication();
		
		lv = (ListView) findViewById(R.id.list);
		
		contactAdapter = new ContactAdapter();
		lv.setAdapter(contactAdapter);
		
		app.getContacts().registerChangeListener(this);
	}
	
	private List<Contact> contacts;
	
	@Override
	public void onResume() {
		super.onResume();
		contacts = app.getContacts().getContacts();
		contactAdapter.setContacts(contacts);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		app.getContacts().unregisterChangeListener(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.contactviewermenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.editgroups:
	    	showDialog(DIALOG_GROUPS);
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	private static final int DIALOG_GROUPS = 1;
	
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_GROUPS:
			Builder builder = new Builder(this);
			
			builder.setTitle(R.string.changegroups);
			
			final Map<Integer, Group> groups = app.getGroups().getContactGroups();
			String[] names = new String[groups.size()];
			final boolean[] selecteds = new boolean[groups.size()];
			int i = 0;
			for(Group g : groups.values()) {
				names[i] = g.name;
				selecteds[i] = !g.hidden;
				i++;
			}
			
			builder.setMultiChoiceItems(names, selecteds, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					// selection made.
					selecteds[which] = isChecked;
				}
			});
			builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// OK Selected
					// Create map of indices to new selected values
					List<Integer> newSelectedValues = new LinkedList<Integer>();
					int i = 0;
					for(Group g : groups.values()) {
						if(!selecteds[i]) {
							newSelectedValues.add(g.id);
						}
						i++;
					}
					app.getDb().groups.setHiddenGroupIds(newSelectedValues);
					app.getContacts().refresh();
				}
			});
			
			return builder.create();
		default:
			return null;
		}
	}
	
	private ContactAdapter contactAdapter;
	
	private final class ContactAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		
		public ContactAdapter() {
			inflater = LayoutInflater.from(ContactViewer.this);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
            if(contacts == null) {
		        if (convertView == null) {
		            convertView = inflater.inflate(R.layout.listloadingitem, null);
		            convertView.setEnabled(false);
		        }
            } else {
		        if (convertView != null) {
		        	if(convertView.findViewById(R.id.contactname) == null) // Must be other view
			            convertView = inflater.inflate(R.layout.contactlistitem, null);
		        } else {
		            convertView = inflater.inflate(R.layout.contactlistitem, null);
		        }
		        
		        Contact contact = contacts.get(position);
		        
		        // Fill
		        TextView name = (TextView) convertView.findViewById(R.id.contactname);
		        QuickContactBadge badge = (QuickContactBadge) convertView.findViewById(R.id.picture);
		        Button addPhoto = (Button) convertView.findViewById(R.id.addphoto);
		        
		        name.setText(contact.getDisplayName());
		        
		        Bitmap image = contact.getPhoto(app.getContacts());
		        badge.setImageBitmap(image);
		        badge.assignContactUri(Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getId())));
            }

            return convertView;
		}
		
		@Override
		public long getItemId(int position) {
			return contacts.get(position).getId();
		}
		
		@Override
		public Object getItem(int position) {
			return contacts.get(position);
		}
		
		@Override
		public int getCount() {
			if(contacts == null) return 0;
			return contacts.size();
		}
		
		private List<Contact> contacts;
		
		public void setContacts(List<Contact> contacts) {
			this.contacts = contacts;
			
			notifyDataSetChanged();
		}
	}

	@Override
	public void onContactsChanged(List<Contact> newContacts) {
		contactAdapter.setContacts(newContacts);
		contacts = newContacts;
	};
}
