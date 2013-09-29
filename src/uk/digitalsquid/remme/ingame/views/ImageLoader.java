package uk.digitalsquid.remme.ingame.views;

import android.content.Context;
import android.graphics.Bitmap;

public interface ImageLoader<ViewType> {
	public Bitmap loadImage(Context context);
	public void onImageLoaded(ViewType asyncImageView);
}