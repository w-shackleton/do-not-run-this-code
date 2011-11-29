package uk.digitalsquid.contactrecall.ingame.gl;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;

/**
 * Displays a set of photos and animates them.
 * @author william
 *
 */
public final class PhotoViewer {
	public static final float PHOTO_WIDTH = 6f;
	
	List<RectMesh> photos;
	
	Images bmps;
	int[] texIds = new int[0];
	
	public PhotoViewer() {
		photos = new LinkedList<RectMesh>();
	}
	
	/**
	 * If true, textures need reloading.
	 */
	boolean needUpdating = false;
	
	public void setBitmaps(Images bmps) {
		this.bmps = bmps;
		needUpdating = true;
	}
	
	/**
	 * Loads the textures into the GL context. Must be called for pictures to update.
	 */
	public void loadTexs(GL10 gl) {
		if(bmps == null) return;
		if(!needUpdating) return;
		needUpdating = false;
		TextureManager.deleteRawTextures(gl, texIds); // Free old texs
		int sizeDiff = bmps.size() - photos.size();
		if(sizeDiff > 0) {
			for(int i = 0; i < sizeDiff; i++) {
				photos.add(newBlankTextureMesh());
			}
		}
		if(sizeDiff < 0) {
			for(int i = 0; i > sizeDiff; i--) {
				photos.remove(photos.size() - 1);
			}
		}
		
		texIds = TextureManager.getRawTextures(gl, (Bitmap[]) bmps.images.toArray()); // Load new
		for(int id : texIds) {
			photos.get(id).setTextureId(id);
		}
	}
	
	public void draw(GL10 gl) {
		for(RectMesh mesh : photos) {
			mesh.draw(gl);
		}
	}
	
	private static final RectMesh newBlankTextureMesh() {
		RectMesh ret = new RectMesh(0, 0, PHOTO_WIDTH, PHOTO_WIDTH, 0, 0, 0, 0);
		ret.setTextureCoordinates(RectMesh.texCoords.clone());
		return ret;
	}
}
