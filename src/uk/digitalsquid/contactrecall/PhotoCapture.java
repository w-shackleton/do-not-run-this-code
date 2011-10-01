package uk.digitalsquid.contactrecall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * Captures a photo, and saves it to the contacts DB
 * @author william
 *
 */
public class PhotoCapture extends Activity {
	
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
				app.getPhotos().saveImageToContactAsync(image, id, new Runnable() {
					@Override
					public void run() {
						setResult(RESULT_OK);
						finish();
					}
				});
			} else if(contactUri != null) { // Use user selected contact URI
				int id;
				try {
					id = Integer.parseInt(contactUri.getLastPathSegment());
				} catch(NumberFormatException e) {
					setResult(RESULT_CANCELED);
					finish();
					return;
				}
				app.getPhotos().saveImageToContactAsync(image, id, new Runnable() {
					@Override
					public void run() {
						setResult(RESULT_OK);
						finish();
					}
				});
			} else { // No contact passed, ask user
				Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(intent, ACTIVITY_CONTACT_RESULT);
			}
		} else {
			setResult(RESULT_CANCELED);
			finish();
		}
	}
}
