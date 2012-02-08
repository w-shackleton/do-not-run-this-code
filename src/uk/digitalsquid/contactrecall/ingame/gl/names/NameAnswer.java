package uk.digitalsquid.contactrecall.ingame.gl.names;

import uk.digitalsquid.contactrecall.ingame.gl.Moveable;
import uk.digitalsquid.contactrecall.misc.Animator;
import android.util.Log;

/**
 * Displays a name as an answer.
 * @author william
 *
 */
public class NameAnswer extends NameViewer implements Moveable {
	static final float TEXT_HEIGHT = 4;
	
	public NameAnswer() {
		super(TEXT_HEIGHT);
	}
	
	public void setName(String name) {
		setText(name);
	}

	@Override
	public void move(float millis) {
		float smoothStage = Animator.anim1d(Animator.TYPE_SINE, animStage);
		if(animatingIn) {
			// display.setXYZ((1-smoothStage) * ANIM_OUT_DISTANCE, 0, 0);
			display.setAlpha(smoothStage);
			Log.v(TAG, "IN: " + animStage);
			
		} else {
			// display.setXYZ(0, smoothStage * ANIM_OUT_DISTANCE, 0);
			display.setAlpha(1-smoothStage);
			Log.v(TAG, "OUT: " + animStage);
		}
	}

}
