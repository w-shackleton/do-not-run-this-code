package uk.digitalsquid.contactrecall.ingame.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AsyncImageView extends ImageView {
	
	private final Context context;

	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}
	public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public static interface ImageLoader {
		public Bitmap loadImage(Context context);
		public void onImageLoaded(AsyncImageView asyncImageView);
	}
	
	/**
	 * Sets the bitmap to display in this {@link AsyncImageView},
	 * but loads it on a background thread
	 * @param imageLoader
	 */
	public void setImageBitmapAsync(final ImageLoader imageLoader) {
		AsyncTask<ImageLoader, Void, Bitmap> task = new AsyncTask<AsyncImageView.ImageLoader, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(ImageLoader... params) {
				ImageLoader loader = params[0];
				return loader.loadImage(context);
			}
			
			@Override
			protected void onPostExecute(Bitmap result) {
				super.onPostExecute(result);
				setImageBitmap(result);
				imageLoader.onImageLoaded(AsyncImageView.this);
			}
		};
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageLoader);
	}
}
