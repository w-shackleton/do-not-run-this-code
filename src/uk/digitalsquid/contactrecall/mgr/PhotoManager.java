package uk.digitalsquid.contactrecall.mgr;

import java.io.IOException;
import java.io.InputStream;

import uk.digitalsquid.contactrecall.mgr.db.DB;
import uk.digitalsquid.contactrecall.misc.Config;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

/**
 * Opens photo resources from the phone's contacts.
 * @author william
 *
 */
public class PhotoManager implements Config {
	// private final Context context;
	// private final DB db;
	private final ContentResolver cr;
	
	public PhotoManager(Context context, DB db) {
		// this.context = context;
		// this.db = db;
		this.cr = context.getContentResolver();
	}
	
	/**
	 * Opens a stream to a photo resource.
	 * @param contactId
	 * @param highRes If possible, return a high-resolution photo.
	 */
	@SuppressLint("NewApi")
	InputStream openPhotoInputStream(int contactId, boolean highRes) {
		Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);	
		if(VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return ContactsContract.Contacts.openContactPhotoInputStream(cr, uri, highRes);
		} else {
			return ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
		}
	}
	
	public Bitmap getContactPicture(int contactId, boolean highRes) {
		InputStream in = openPhotoInputStream(contactId, highRes);
		
		try {
			// This method handles null cases and errors
			Bitmap bmp = BitmapFactory.decodeStream(in);
			in.close();
			return bmp;
		} catch (IOException e) {
			Log.e(TAG, "Failed to decode contact photo", e);
		}
		return null;
	}
}
