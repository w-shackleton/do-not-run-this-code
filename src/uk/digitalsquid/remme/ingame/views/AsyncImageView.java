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
	
	private LoadTask task;

	/**
	 * Sets the bitmap to display in this {@link AsyncImageView},
	 * but loads it on a background thread
	 * @param imageLoader
	 */
	@SuppressWarnings("unchecked")
	public void setImageBitmapAsync(final ImageLoader<AsyncImageView> imageLoader) {
		if(task != null) {
			task.backRef = null;
			task.cancel(true);
			task = null;
		}
		task = new LoadTask(context, this);
		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, imageLoader);
	}
	
	private static class LoadTask extends AsyncTask<ImageLoader<AsyncImageView>, Void, Bitmap> {
		public AsyncImageView backRef;
		private Context context;
		
		public LoadTask(Context context, AsyncImageView backRef) {
			this.backRef = backRef;
			this.context = context;
		}
		
		ImageLoader<AsyncImageView> imageLoader;
	
		@Override
		protected Bitmap doInBackground(ImageLoader<AsyncImageView>... params) {
			imageLoader = params[0];
			return imageLoader.loadImage(context);
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			if(backRef == null) return;
			backRef.setImageBitmap(result);
			imageLoader.onImageLoaded(backRef);
			
			// Reset all
			backRef.task = null;
			backRef = null;
		}
	}
}
