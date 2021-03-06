package uk.digitalsquid.remme.ingame.questions;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.ingame.views.AsyncImageView;
import uk.digitalsquid.remme.ingame.views.ImageLoader;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.details.Contact;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Displays a question with a picture as the question and
 * text as the answers.
 * @author william
 *
 */
public class ImageTextMCFragment extends MultiChoiceFragment<AsyncImageView, Button> {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The timer should start once the image is loaded
		setStartTimerImmediately(false);
	}

	@Override
	protected int getRootLayoutId(Question question) {
		return R.layout.imagetextmcfragment;
	}

	@Override
	protected AsyncImageView getQuestionView(View rootView) {
        return (AsyncImageView) rootView.findViewById(R.id.photo);
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
		final App app = (App) getActivity().getApplication();
		questionView.setImageBitmapAsync(new ImageLoader<AsyncImageView>() {
			@Override
			public void onImageLoaded(AsyncImageView asyncImageView) {
				if(timer != null) timer.start();
			}
			@Override
			public Bitmap loadImage(Context context) {
		        return question.getContact().getPhoto(app.getPhotos());
			}
		});
	}
}
