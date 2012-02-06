package uk.digitalsquid.contactrecall.game;

import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.mgr.PhotoManager;
import uk.digitalsquid.contactrecall.misc.AsyncLoadBuffer;
import uk.digitalsquid.contactrecall.misc.AsyncLoadBuffer.Source;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class PhotoToNameGame extends GameInstance {

	final App app;
	
	final LinkedList<Contact> questions;
	
	final AsyncLoadBuffer<Images> bitmapLoader;

	public PhotoToNameGame(App app) {
		this.app = app;
		questions = app.getGame().getRandomPhotoSet(10); // TODO: Customise number of photos
		bitmapLoader = new AsyncLoadBuffer<Images>(bitmapSource);
		bitmapLoader.start();
	}

	@Override
	public List<Contact> getQuestions() {
		return questions;
	}

	@Override
	public int getFromMode() {
		return FROM_PHOTO;
	}

	@Override
	public int getToMode() {
		return TO_NAME;
	}

	@Override
	public Object getFromObject() {
		Object bmp = bitmapLoader.get(progress);
		if(bmp == null) Log.e(TAG, "Null bitmap!");
		return bmp;
	}

	@Override
	public Object[] getToObjects() {
		// TODO: Implement
		return new String[] {"111", "222", "333", "444" };
	}
	
	@Override
	public void windTo(int position) {
		super.windTo(position);
		bitmapLoader.windTo(position);
	}
	
	private Source<Images> bitmapSource = new Source<Images>() {
		
		@Override
		public Images getElement(int pos) {
			Log.i(TAG, "Picture nr. " + pos);
			Contact c = questions.get(pos);
			return new Images(c.getId(), c, app.getPhotos());
		}

		@Override
		public boolean hasMore(int pos) {
			return pos < questions.size();
		}

		@Override
		public void finish() {
		}

		@Override
		public Images ifNull() {
			// TODO: Change to a question mark image
			return new Images();
		}
	};
	
	/**
	 * Loads a set of images for a contact into memory.
	 * @author william
	 *
	 */
	public static class Images {
		public final List<Bitmap> images;
		
		public final int contactId;
		
		static final int bitmapSize = 512;
		
		private static final Paint PAINT = new Paint();
		static {
			PAINT.setAntiAlias(true); // TODO: Option?
			PAINT.setColor(0xFFFFFFFF);
		}
		
		/**
		 * Blank constructor
		 * @param id
		 */
		public Images() {
			images = new LinkedList<Bitmap>();
			contactId = -1;
		}
		public Images(int id, Contact contact, PhotoManager mgr) {
			images = new LinkedList<Bitmap>();
			contactId = id;
			List<Bitmap> srcs = contact.getPhotos(mgr);
			for(Bitmap src : srcs) {
				if(src == null) continue;
				Log.v(TAG, "Bitmap size is " + src.getWidth() + ", " + src.getHeight());
				Bitmap dest = Bitmap.createBitmap(bitmapSize, bitmapSize, Config.ARGB_8888);
				Canvas c = new Canvas(dest);
				float aspectRatio = (float)src.getWidth() / (float)src.getHeight();
				float width = bitmapSize;
				float height = bitmapSize;
				if(aspectRatio > 1) { // Landscape
					height = width / aspectRatio;
				} else {
					width = height / aspectRatio;
				}
				
				c.drawBitmap(src, new Rect(0, 0, src.getWidth(), src.getHeight()),
						new RectF((bitmapSize - width) / 2, (bitmapSize - height) / 2, width, height), PAINT);
				images.add(dest);
				src.recycle();
			}
		}
		
		public int size() {
			return images.size();
		}
	}
}
