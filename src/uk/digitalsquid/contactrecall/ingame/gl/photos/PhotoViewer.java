package uk.digitalsquid.contactrecall.ingame.gl.photos;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.ingame.gl.TextureManager;
import uk.digitalsquid.contactrecall.misc.Config;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Displays a set of photos and animates them.
 * @author william
 *
 */
public abstract class PhotoViewer implements Config {
	public static final float PHOTO_GAP = .5f;
	
	public static final float ANIM_OUT_DISTANCE = -10f; // Down
	
	List<RectMesh> photos;
	
	Images bmps;
	int[] texIds = new int[0];
	
	final float photoWidth;
	
	public PhotoViewer(float width) {
		photos = new LinkedList<RectMesh>();
		photoWidth = width;
	}
	
	/**
	 * If true, textures need reloading.
	 */
	boolean needUpdating = false;
	
	public void setBitmaps(Images bmps) {
		this.bmps = bmps;
		needUpdating = true;
		if(bmps != null) {
			Log.i(TAG, "Showing picture " + bmps.contactId);
		}
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
				photos.add(newBlankTextureMesh(photoWidth));
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
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);
		for(RectMesh mesh : photos) {
			mesh.draw(gl);
			gl.glTranslatef(photoWidth + PHOTO_GAP, 0, 0);
		}
		gl.glPopMatrix();
	}
	
	/**
	 * When true, 0 = out and 1 = in. When false, 0 = in and 1 = out.
	 * Note that although PhotoViewer defines the animations, it is up to the implementation to actually do any animating.
	 */
	protected boolean animatingIn = false;
	protected float animStage = 1;
	
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
	
	private static final RectMesh newBlankTextureMesh(float photoWidth) {
		RectMesh ret = new RectMesh(0, 0, photoWidth, photoWidth, 1, 1, 1, 1);
		ret.setTextureCoordinates(RectMesh.texCoords.clone());
		return ret;
	}
	
	private float x, y, z;
	private float rx, ry, rz;
	
	public void setXYZ(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Sets the X, Y, Z rotations in DEGREES
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRXYZ(float rx, float ry, float rz) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}
}
