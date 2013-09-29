package uk.digitalsquid.remme.ingame.questions;

import java.util.ArrayList;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.ingame.views.AsyncImageButton;
import uk.digitalsquid.remme.ingame.views.ImageLoader;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.details.Contact;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Displays a question with a picture as the question and
 * text as the answers.
 * @author william
 *
 */
public class TextImageMCFragment extends MultiChoiceFragment<TextView, AsyncImageButton> {
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The timer should start once the image is loaded
		setStartTimerImmediately(false);
		pendingImages = new ArrayList<AsyncImageButton>(4);
	}

	@Override
	protected int getRootLayoutId(Question question) {
		return R.layout.textimagemcfragment;
	}

	@Override
	protected TextView getQuestionView(View rootView) {
        return (TextView) rootView.findViewById(R.id.question);
	}

	@Override
	protected AsyncImageButton[] getChoiceButtons(View rootView) {
		AsyncImageButton[] result = new AsyncImageButton[8];
        result[0] = (AsyncImageButton) rootView.findViewById(R.id.choice1);
        result[1] = (AsyncImageButton) rootView.findViewById(R.id.choice2);
        result[2] = (AsyncImageButton) rootView.findViewById(R.id.choice3);
        result[3] = (AsyncImageButton) rootView.findViewById(R.id.choice4);
        result[4] = (AsyncImageButton) rootView.findViewById(R.id.choice5);
        result[5] = (AsyncImageButton) rootView.findViewById(R.id.choice6);
        result[6] = (AsyncImageButton) rootView.findViewById(R.id.choice7);
        result[7] = (AsyncImageButton) rootView.findViewById(R.id.choice8);
		return result;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        Contact contact = question.getContact();
        int questionType = question.getQuestionType();

        // Configure question
        questionView.setText(contact.getTextField(questionType));
        return ret;
	}

	/**
	 * A list of images that are still being loaded
	 */
	private ArrayList<AsyncImageButton> pendingImages;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		final App app = (App) getActivity().getApplication();
        
		// Spark each AsyncImageView off loading its image.
		// Once each view finishes, it marks itself as completed
		// by removing itself from pendingImages. Once this list
		// is empty, the timer may be started.

        int correctChoice = question.getCorrectPosition();
        int numberOfChoices = question.getNumberOfChoices();
        Contact correct = question.getContact();
        int posThroughOthers = 0;
        for(int i = 0; i < numberOfChoices; i++) {
        	final Contact contact = i == correctChoice ? correct :
        		question.getOtherAnswers()[posThroughOthers++];

        	pendingImages.add(choiceButtons[i]);
        	choiceButtons[i].setImageBitmapAsync(new ImageLoader<AsyncImageButton>() {
				@Override
				public Bitmap loadImage(Context context) {
			        return contact.getPhoto(app.getPhotos());
				}

				@Override
				public void onImageLoaded(AsyncImageButton asyncImageButton) {
					pendingImages.remove(asyncImageButton);
					if(pendingImages.size() == 0) {
						if(timer != null)
							timer.start();
					}
				}
			});
        }
	}
}
