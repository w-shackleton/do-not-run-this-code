package uk.digitalsquid.contactrecall.mgr;

import java.io.ByteArrayOutputStream;

import uk.digitalsquid.contactrecall.mgr.db.DB;
import uk.digitalsquid.contactrecall.misc.Config;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.ContactsContract;

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
	 * Gets the first picture for a given contact ID
	 * @param idNum
	 * @return
	 */
	public Bitmap getContactPicture(int idNum) {
		return getContactPicture(idNum, 0);
	}
	
	/**
	 * Gets and loads the nth picture(s) for a given contact ID
	 * @param idNum
	 * @param position
	 */
	public Bitmap getContactPicture(int idNum, int position) {
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI,
				new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO },
				ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.CONTACT_ID + "= ?",
				new String[] { ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, String.valueOf(idNum) },
				null);
		final int photoCol = cur.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO);
		for(int i = 0; i < position; i++) cur.moveToNext();
		while(cur.moveToNext()) {
			byte[] data = cur.getBlob(photoCol);
			if(data == null) continue;
			cur.close();
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		}
		cur.close();
		return null;
	}
	
	/**
	 * Gets the number of pictures for a given contact.
	 * @param idNum
	 */
	public int getContactPictureCount(int idNum) {
		Cursor cur = cr.query(ContactsContract.Data.CONTENT_URI,
				new String[] { },
				ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.Data.CONTACT_ID + "= ?",
				new String[] { ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, String.valueOf(idNum) },
				null);
		final int count = cur.getCount();
		cur.close();
		return count;
	}
	
	/**
	 * Saves a contact's image to the DB
	 * @param image
	 * @param contactId
	 */
	public void saveImageToContact(Bitmap image, int contactId) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(200 * 1024); // 200kB
		image.compress(CompressFormat.JPEG, 85, baos);
		
		ContentValues values = new ContentValues();
		values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
		values.put(ContactsContract.CommonDataKinds.Photo.CONTACT_ID, contactId);
		values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, baos.toByteArray());
		cr.insert(ContactsContract.Data.CONTENT_URI, values);
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
	
	public void saveImageToContactAsync(final Bitmap image, final int contactId, final Runnable onDone) {
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
		task.execute();
	}
}
