package uk.digitalsquid.contactrecall.ingame.gl;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.game.PhotoToNameGame.Images;

/**
 * Shows a question on the screen. Detects the type of question.
 * @author william
 *
 */
public class QAViewer {
	PhotoViewer photoQuestion;
	
	public void setQuestion(Object question) {
		if(question instanceof Images) {
			if(photoQuestion == null) photoQuestion = new PhotoViewer();
			photoQuestion.setBitmaps((Images) question);
			// TODO: Set other q types to null to stop multiple drawing,
		}
	}
	
	public void draw(GL10 gl) {
		if(photoQuestion != null) {
			photoQuestion.loadTexs(gl);
			photoQuestion.draw(gl);
		}
	}
}
