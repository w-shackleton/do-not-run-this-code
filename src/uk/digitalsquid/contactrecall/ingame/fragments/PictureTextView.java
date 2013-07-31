package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;


/**
 * Displays a question with a picture as the question and
 * text as the answers.
 * @author william
 *
 */
public class PictureTextView extends MultiChoiceView<ImageView, Button> {

	@Override
	protected int getRootLayoutId() {
		return R.layout.picturetextview;
	}

	@Override
	protected ImageView getQuestionView(View rootView) {
        return (ImageView) rootView.findViewById(R.id.photo);
	}

	@Override
	protected Button[] getChoiceButtons(View rootView) {
		Button[] result = new Button[8];
        result[0] = (Button) rootView.findViewById(R.id.choice1);
        result[1] = (Button) rootView.findViewById(R.id.choice2);
        result[2] = (Button) rootView.findViewById(R.id.choice3);
        result[3] = (Button) rootView.findViewById(R.id.choice4);
        result[4] = (Button) rootView.findViewById(R.id.choice5);
        result[5] = (Button) rootView.findViewById(R.id.choice6);
        result[6] = (Button) rootView.findViewById(R.id.choice7);
        result[7] = (Button) rootView.findViewById(R.id.choice8);
		return result;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        int correctChoice = question.getCorrectPosition();
        int numberOfChoices = question.getNumberOfChoices();
        Contact contact = question.getContact();
        int answerType = question.getAnswerType();
        
        // Configure answers
        int posThroughOthers = 0;
        for(int i = 0; i < numberOfChoices; i++) {
        	if(i == correctChoice)
		        choiceButtons[i].setText(contact.getTextField(answerType));
        	else {
        		choiceButtons[i].setText(
        				question.getOtherAnswers()[posThroughOthers++].getTextField(answerType));
        	}
        }
        return ret;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		App app = (App) getActivity().getApplication();
		// TODO: Background this? Probably not - we want photo to appear along with UI
        Bitmap bmp = question.getContact().getPhoto(app.getPhotos());
        questionView.setImageBitmap(bmp);
	}
}
