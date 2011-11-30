package uk.digitalsquid.contactrecall.ingame.gl;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;
import android.graphics.Bitmap;

/**
 * Displays a set of photos and animates them.
 * @author william
 *
 */
public final class PhotoViewer implements Moveable {
	public static final float PHOTO_WIDTH = 9f;
	public static final float PHOTO_GAP = .5f;
	
	public static final float ANIM_OUT_DISTANCE = -10f; // Down
	
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
		if(texIds.length > 0) TextureManager.deleteRawTextures(gl, texIds); // Free old texs
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
		
		texIds = TextureManager.getRawTextures(gl, bmps.images.toArray(new Bitmap[bmps.images.size()])); // Load new
		int i = 0;
		for(int id : texIds) {
			photos.get(i++).setTextureId(id);
		}
	}
	
	public void draw(GL10 gl) {
		gl.glPushMatrix();
		for(RectMesh mesh : photos) {
			mesh.draw(gl);
			gl.glTranslatef(PHOTO_WIDTH + PHOTO_GAP, 0, 0);
		}
		gl.glPopMatrix();
	}
	
	/**
	 * When true, 0 = out and 1 = in. When false, 0 = in and 1 = out
	 */
	private boolean animatingIn = true;
	private float animStage;
	
	/**
	 * if in is <code>true</code>, start to animate in. Otherwise, start to animate out.
	 * @param in
	 */
	public void setAnimation(boolean in) {
		animatingIn = in;
		animStage = 0;
	}
	
	public void setAnimationStage(float stage) {
		animStage = stage;
	}
	
	private static final RectMesh newBlankTextureMesh() {
		RectMesh ret = new RectMesh(0, 0, PHOTO_WIDTH, PHOTO_WIDTH, 1, 1, 1, 1);
		ret.setTextureCoordinates(RectMesh.texCoords.clone());
		return ret;
	}

	@Override
	public void move(float millis) {
		if(animatingIn) {
			for(RectMesh photo : photos) {
				photo.setXYZ((1-animStage) * ANIM_OUT_DISTANCE, 0, 0);
			}
			
		} else {
			for(RectMesh photo : photos) {
				photo.setXYZ(0, animStage * ANIM_OUT_DISTANCE, 0);
			}
		}
	}
}
