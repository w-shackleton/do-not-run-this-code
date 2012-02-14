package uk.digitalsquid.contactrecall.ingame.gl;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.GameInstance;
import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;
import uk.digitalsquid.contactrecall.ingame.gl.names.NameAnswer;
import uk.digitalsquid.contactrecall.ingame.gl.photos.PhotoQuestion;
import uk.digitalsquid.contactrecall.misc.Config;

/**
 * Shows a question on the screen. Detects the type of question.
 * @author william
 *
 */
public class QAViewer implements Config, Moveable {
	
	PhotoQuestion photoQuestion;
	
	AnswerCollection<NameAnswer> nameAnswers;
	
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
		
		// Reposition onscreen on size change
		if(nameAnswers != null) nameAnswers.repositionAnswers(0, -height / 2 + 10, width - 4, 20);
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
	
	/**
	 * Sets the current question's answer.
	 * @param mode The answer mode according to {@link GameInstance}
	 * @param answer
	 */
	public void setAnswer(int mode, List<?> answers) {
		switch(mode) {
		case GameInstance.TO_NAME:
			if(nameAnswers == null) nameAnswers = new AnswerCollection<NameAnswer>();
			nameAnswers.clear();
			
			// Add each one to the answer collection, then organise it.
			
			final int deltaAnswers = nameAnswers.size() - answers.size();
			@SuppressWarnings("unchecked")
			List<CharSequence> list = ((List<CharSequence>)answers);
			if(deltaAnswers > 0) { // Too many
				for(int i = 0; i < deltaAnswers; i++) {
					nameAnswers.remove(list.size() - 1);
				}
			} else if(deltaAnswers < 0) {
				for(int i = 0; i < -deltaAnswers; i++) {
					nameAnswers.add(new NameAnswer());
				}
			}
			
			int pos = 0;
			for(NameAnswer a : nameAnswers) {
				a.setName(list.get(pos++).toString());
			}
			nameAnswers.repositionAnswers(0, -height / 2 + 10, width - 4, 20);
			break;
		}
	}
	
	public void draw(GL10 gl) {
		if(photoQuestion != null) {
			photoQuestion.setXYZ(-width / 2 + 6, height / 2 - 6, 0);
			photoQuestion.loadTexs(gl);
			photoQuestion.draw(gl);
		}
		if(nameAnswers != null) {
			nameAnswers.draw(gl);
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
		if(nameAnswers != null) {
			for(NameAnswer a : nameAnswers) {
				a.setAnimationStage(animStage);
				a.move(millis);
			}
		}
	}
	
	private Runnable animateInFinish, animateOutFinish;
	
	/**
	 * Begins the in animation
	 */
	public void animateIn(Runnable onDone) {
		animateInFinish = onDone;
		animStage = 0;
		if(photoQuestion != null) photoQuestion.setAnimation(true);
		if(nameAnswers != null) {
			for(NameAnswer a : nameAnswers) {
				a.setAnimation(true);
			}
		}
	}
	
	/**
	 * Begins the out animation
	 */
	public void animateOut(Runnable onDone) {
		animateOutFinish = onDone;
		animStage = 0;
		if(photoQuestion != null) photoQuestion.setAnimation(false);
		if(nameAnswers != null) {
			for(NameAnswer a : nameAnswers) {
				a.setAnimation(false);
			}
		}
	}
}
