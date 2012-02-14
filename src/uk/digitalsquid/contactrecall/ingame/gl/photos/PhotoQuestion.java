package uk.digitalsquid.contactrecall.ingame.gl.photos;

import java.util.Random;

import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;
import uk.digitalsquid.contactrecall.ingame.gl.Moveable;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.misc.Animator;
import android.graphics.Bitmap;

/**
 * A question which is a (set?) of photos of a single person.
 * @author william
 *
 */
public class PhotoQuestion extends PhotoViewer implements Moveable {
	public static final float PHOTO_WIDTH = 9f;
	
	public PhotoQuestion() {
		super(PHOTO_WIDTH);
		setRXYZ(0, 10, 0);
	}
	
	private static final Random RANDOM = new Random();
	
	/**
	 * Overloaded version of bitmap setter which only keeps one image for this question - this behaviour might not be used for answers
	 */
	@Override
	public void setBitmaps(Images bmps) {
		Bitmap singleImg = bmps.images.get(RANDOM.nextInt(bmps.images.size()));
		bmps.images.clear();
		bmps.images.add(singleImg);
		super.setBitmaps(bmps);
	}

	@Override
	public void move(float millis) {
		float smoothStage = Animator.anim1d(Animator.TYPE_SINE, animStage);
		if(animatingIn) {
			for(RectMesh photo : photos) {
				photo.setXYZ((1-smoothStage) * ANIM_OUT_DISTANCE, 0, 0);
				photo.setAlpha(smoothStage);
			}
		} else {
			for(RectMesh photo : photos) {
				photo.setXYZ(0, smoothStage * ANIM_OUT_DISTANCE, 0);
				photo.setAlpha(1-smoothStage);
			}
		}
	}
}
