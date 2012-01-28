package uk.digitalsquid.contactrecall.ingame.gl;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.GameInstance;
import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;
import uk.digitalsquid.contactrecall.ingame.gl.photos.PhotoQuestion;

/**
 * Shows a question on the screen. Detects the type of question.
 * @author william
 *
 */
public class QAViewer implements Moveable {
	PhotoQuestion photoQuestion;
	
	static final int ANIM_STATE_HIDDEN = 1;
	static final int ANIM_STATE_ENTERING = 2;
	static final int ANIM_STATE_VISIBLE = 3;
	static final int ANIM_STATE_LEAVING = 4;
	int animState;
	
	/**
	 * From 0 to 1, shows the percentage of the animation done so far.
	 */
	float animStage = 0;
	/**
	 * If <code>true</code>, the animation is an in one.
	 */
	boolean animatingIn;
	
	/**
	 * Time animation lasts for in milliseconds
	 */
	public static final float ANIM_TIME = 700;
	
	boolean landscape;
	float width, height;
	
	public void setOrientation(boolean landscape, float width, float height) {
		this.landscape = landscape;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * 
	 * @param mode The mode according to {@link GameInstance}
	 * @param question
	 */
	public void setQuestion(int mode, Object question) {
		switch(mode) {
		case GameInstance.FROM_PHOTO:
			if(photoQuestion == null) photoQuestion = new PhotoQuestion();
			photoQuestion.setBitmaps((Images) question);
			// TODO: Set other q types to null to stop multiple drawing,
			break;
		}
	}
	
	public void draw(GL10 gl) {
		if(photoQuestion != null) {
			photoQuestion.setXYZ(-width / 2 + 6, height / 2 - 6, 0);
			photoQuestion.loadTexs(gl);
			photoQuestion.draw(gl);
		}
	}
	
	/**
	 * Animates this {@link QAViewer}.
	 * @param millis
	 */
	@Override
	public void move(float millis) {
		animStage += millis / ANIM_TIME;
		if(animStage > 1) {
			animStage = 1;
			if(animatingIn) {
				if(animateInFinish != null) {
					animateInFinish.run();
					animateInFinish = null;
				}
			} else {
				if(animateOutFinish != null) {
					animateOutFinish.run();
					animateOutFinish = null;
				}
			}
		}
		if(photoQuestion != null) photoQuestion.setAnimationStage(animStage);
		if(photoQuestion != null) photoQuestion.move(millis);
	}
	
	private Runnable animateInFinish, animateOutFinish;
	
	/**
	 * Begins the in animation
	 */
	public void animateIn(Runnable onDone) {
		animateInFinish = onDone;
		animStage = 0;
		if(photoQuestion != null) photoQuestion.setAnimation(true);
	}
	
	/**
	 * Begins the out animation
	 */
	public void animateOut(Runnable onDone) {
		animateOutFinish = onDone;
		animStage = 0;
		if(photoQuestion != null) photoQuestion.setAnimation(false);
	}
}
