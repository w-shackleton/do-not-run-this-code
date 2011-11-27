package uk.digitalsquid.contactrecall.game;

import java.util.LinkedList;
import java.util.List;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.AsyncLoadBuffer;
import uk.digitalsquid.contactrecall.misc.AsyncLoadBuffer.Source;
import android.graphics.Bitmap;

public class PhotoToNameGame extends GameInstance {

	final App app;
	
	final LinkedList<Contact> questions;
	
	final AsyncLoadBuffer<Bitmap> bitmapLoader;

	public PhotoToNameGame(App app) {
		this.app = app;
		questions = app.getGame().getRandomPhotoSet(10); // TODO: Customise number of photos
		bitmapLoader = new AsyncLoadBuffer<Bitmap>(bitmapSource);
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
		return bitmapLoader.get();
	}

	@Override
	public Object[] getToObjects() {
		// TODO: Implement
		return new String[] {"111", "222", "333", "444" };
	}
	
	int bitmapPosition = 0;
	
	private Source<Bitmap> bitmapSource = new Source<Bitmap>() {
		
		@Override
		public Bitmap getElement(int pos) {
			return questions.get(bitmapPosition).getPhoto(app.getPhotos());
		}

		@Override
		public boolean hasMore() {
			return bitmapPosition < questions.size();
		}

		@Override
		public void finish() {
		}
	};
}
