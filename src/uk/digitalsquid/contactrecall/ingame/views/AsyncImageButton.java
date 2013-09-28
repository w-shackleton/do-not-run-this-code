package uk.digitalsquid.contactrecall.ingame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class AsyncImageButton extends ImageButton {
	
	private final Context context;

	public AsyncImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	public AsyncImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	/**
	 * Sets the bitmap to display in this {@link AsyncImageButton},
	 * but loads it on a background thread
	 * @param imageLoader
	 */
	@SuppressWarnings("unchecked")
	public void setImageBitmapAsync(final ImageLoader<AsyncImageButton> imageLoader) {
		AsyncTask<ImageLoader<AsyncImageButton>, Void, Bitmap> task = new AsyncTask<ImageLoader<AsyncImageButton>, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(ImageLoader<AsyncImageButton>... params) {
				ImageLoader<AsyncImageButton> loader = params[0];
				return loader.loadImage(context);
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				setImageBitmap(result);
				imageLoader.onImageLoaded(AsyncImageButton.this);
			}
		};
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageLoader);
	}
}
