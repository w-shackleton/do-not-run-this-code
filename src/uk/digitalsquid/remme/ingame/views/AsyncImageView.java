package uk.digitalsquid.remme.ingame.views;

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

	/**
	 * Sets the bitmap to display in this {@link AsyncImageView},
	 * but loads it on a background thread
	 * @param imageLoader
	 */
	@SuppressWarnings("unchecked")
	public void setImageBitmapAsync(final ImageLoader<AsyncImageView> imageLoader) {
		AsyncTask<ImageLoader<AsyncImageView>, Void, Bitmap> task = new AsyncTask<ImageLoader<AsyncImageView>, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(ImageLoader<AsyncImageView>... params) {
				ImageLoader<AsyncImageView> loader = params[0];
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
