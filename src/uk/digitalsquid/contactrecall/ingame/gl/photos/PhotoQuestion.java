package uk.digitalsquid.contactrecall.ingame.gl.photos;

import uk.digitalsquid.contactrecall.ingame.gl.Moveable;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.misc.Animator;

/**
 * A question which is a (set?) of photos of a single person.
 * @author william
 *
 */
public class PhotoQuestion extends PhotoViewer implements Moveable {
	public PhotoQuestion() {
		super();
		setRXYZ(0, 10, 0);
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
