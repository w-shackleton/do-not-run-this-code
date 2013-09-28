package uk.digitalsquid.contactrecall.ingame.views;

import android.content.Context;
import android.graphics.Bitmap;

public interface ImageLoader<ViewType> {
	public Bitmap loadImage(Context context);
	public void onImageLoaded(ViewType asyncImageView);
}