package uk.digitalsquid.contactrecall;

import java.util.LinkedList;

import uk.digitalsquid.contactrecall.mgr.RawContact;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Captures a photo, and saves it to the contacts DB
 * @author william
 *
 */
public class PhotoCapture extends Activity implements Config {
	
	private static final int ACTIVITY_PICTURE_RESULT = 1;
	private static final int ACTIVITY_CONTACT_RESULT = 2;
	
	private App app;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.processing);
		
		app = (App) getApplication();
		
		startCamera();
	}
	
	private void startCamera() {
		Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(camera, ACTIVITY_PICTURE_RESULT);
	}
	
	Bitmap image;
	Uri contactUri;
	
	// Picture finished
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	
		switch(requestCode) {
		case ACTIVITY_PICTURE_RESULT:
			switch(resultCode) {
			case RESULT_OK:
				if(data == null) break;
				if(data.getExtras() == null) break;
				image = (Bitmap) data.getExtras().get("data");
				
				startResultSave();
				break;
			default:
				setResult(RESULT_CANCELED);
				finish();
				break;
			}
			break;
		case ACTIVITY_CONTACT_RESULT:
			switch(resultCode) {
			case RESULT_OK:
				contactUri = data.getData();
				startResultSave();
				break;
			default:
				Toast.makeText(getBaseContext(), "Picture not saved", Toast.LENGTH_LONG).show();
				break;
			}
			break;
		}
	}
	
	private void startResultSave() {
		if(image != null) {
			
			Bundle extras = getIntent().getExtras();
			
			// If passed an ID, use that rather than asking
			if(extras != null && extras.containsKey("contactId")) {
				int id = extras.getInt("contactId", -1);
				askForAccountIfNecessary(id);
			} else if(contactUri != null) { // Use user selected contact URI
				int id;
				try {
					id = Integer.parseInt(contactUri.getLastPathSegment());
				} catch(NumberFormatException e) {
					setResult(RESULT_CANCELED);
					finish();
					return;
				}
				askForAccountIfNecessary(id);
			} else { // No contact passed, ask user
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, ACTIVITY_CONTACT_RESULT);
			}
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}
	
	/**
	 * Saves the picture if there is only 1 raw contact, otherwise asks user for an account to save to.
	 * @param id
	 */
	private void askForAccountIfNecessary(int id) {
		LinkedList<RawContact> rawContacts = app.getContacts().getRawContacts(id);
		Log.i(TAG, "" + rawContacts.size() + " raw contacts found for ID " + id);
		switch(rawContacts.size()) {
		case 0:
			Log.e(TAG, "No raw contacts found!");
			Toast.makeText(getBaseContext(), "No contacts found", Toast.LENGTH_SHORT).show();
			setResult(RESULT_CANCELED);
			finish();
			break;
		case 1:
			Log.i(TAG, "1 contact, using it");
			app.getOldPhotos().saveImageToContactAsync(image, rawContacts.get(0).getId(), new Runnable() {
				@Override
				public void run() {
					setResult(RESULT_OK);
					finish();
				}
			});
			break;
		default:
			Bundle bundle = new Bundle();
			bundle.putSerializable("contacts", rawContacts);
			showDialog(DIALOG_ASKFORACCOUNT, bundle);
			break;
		}
	}
	
	static final int DIALOG_ASKFORACCOUNT = 1;
	
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		switch(id) {
		case DIALOG_ASKFORACCOUNT:
			AlertDialog.Builder builder = new Builder(this);
			
			@SuppressWarnings("unchecked")
			final LinkedList<RawContact> contacts = (LinkedList<RawContact>) args.getSerializable("contacts");
			final LayoutInflater inflater = LayoutInflater.from(this);
			
			builder.setTitle("Choose an account");
			builder.setAdapter(new BaseAdapter() {
				
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
			        if (convertView != null) {
			        	if(convertView.findViewById(R.id.contactname) == null) // Must be other view
				            convertView = inflater.inflate(R.layout.accountitem, null);
			        } else {
			            convertView = inflater.inflate(R.layout.accountitem, null);
			        }
			        
			        final RawContact contact = getItem(position);
			        
			        // Fill
			        TextView name = (TextView) convertView.findViewById(R.id.accountname);
			        
			        name.setText(contact.getAccountName());
			        
		            return convertView;
				}
				
				@Override
				public long getItemId(int position) {
					return contacts.get(position).getId();
				}
				
				@Override
				public RawContact getItem(int position) {
					return contacts.get(position);
				}
				
				@Override
				public int getCount() {
					return contacts.size();
				}
			}, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					RawContact contact = contacts.get(which);
					app.getOldPhotos().saveImageToContactAsync(image, contact.getId(), new Runnable() {
						@Override
						public void run() {
							setResult(RESULT_OK);
							finish();
						}
					});
				}
			});
			
			return builder.create();
		default:
			return null;
		}
	}
}
