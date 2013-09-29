package uk.digitalsquid.remme.mgr;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uk.digitalsquid.remme.mgr.db.DB;
import uk.digitalsquid.remme.misc.Config;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
			if(in != null) in.close();
			return bmp;
		} catch (IOException e) {
			Log.e(TAG, "Failed to decode contact photo", e);
		}
		return null;
	}
	
	/**
	 * Gets the contact IDs of those with pictures available.
	 * @param idNum
	 */
	public List<Integer> getContactsWithPictures() {
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI,
				new String[] { ContactsContract.Data.CONTACT_ID },
				ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Photo.PHOTO + " IS NOT NULL",
				new String[] { ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE },
				null);
		List<Integer> ret = new ArrayList<Integer>(cur.getCount());
		while(cur.moveToNext()) {
			ret.add(cur.getInt(0));
		}
		cur.close();
		return ret;
	}
	
	/**
	 * Class wrapper around a byte array
	 * @author william
	 *
	 */
	private static final class ByteArray {
		public byte[] data;
		
		public ByteArray(byte[] data) {
			this.data = data;
		}
	}
	
	/**
	 * Saves a picture to the given contact ID asynchronously.
	 * @param image
	 * @param contactId
	 * @param onDone
	 */
	public void saveImageToContactAsync(final Bitmap image, final int contactId, final Runnable onDone) {
		// Note: even though pictures are retrieved using the new API, they
		// are still saved using this method.
		AsyncTask<Void, Void, ByteArray> task = new AsyncTask<Void, Void, ByteArray>() {
			@Override
			protected ByteArray doInBackground(Void... params) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(200 * 1024); // 200kB
				image.compress(CompressFormat.JPEG, 85, baos);
				return new ByteArray(baos.toByteArray());
			}
			
			@Override
			protected void onPostExecute(ByteArray result) {
				ContentValues values = new ContentValues();
				values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
				values.put(ContactsContract.CommonDataKinds.Photo.RAW_CONTACT_ID, contactId);
				values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, result.data);
				cr.insert(ContactsContract.Data.CONTENT_URI, values);
				
				onDone.run();
			}
		};
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
}
