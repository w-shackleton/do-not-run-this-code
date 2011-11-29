package uk.digitalsquid.contactrecall.ingame.gl;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

/**
 * Displays a set of photos and animates them.
 * @author william
 *
 */
public final class PhotoViewer {
	public static final float PHOTO_MAX_WIDTH = 6f;
	
	List<RectMesh> photos;
	
	List<Bitmap> bmps;
	
	public void setBitmaps(List<Bitmap> bmps) {
		this.bmps = bmps;
	}
	
	/**
	 * Loads the textures into the GL context. Must be called for pictures to update.
	 */
	public void loadTexs(GL10 gl) {
		int sizeDiff = bmps.size() - photos.size();
		if(sizeDiff > 0) {
			for(int i = 0; i < sizeDiff; i++) {
				photos.add(new RectMesh(0, 0, PHOTO_MAX_WIDTH, PHOTO_MAX_WIDTH, 0, 0, 0, 0));
			}
		}
		if(sizeDiff < 0) {
			for(int i = 0; i > sizeDiff; i--) {
				photos.remove(photos.size() - 1);
			}
		}
	}
	
	public void draw(GL10 gl) {
		for(RectMesh mesh : photos) {
			mesh.draw(gl);
		}
	}
}
