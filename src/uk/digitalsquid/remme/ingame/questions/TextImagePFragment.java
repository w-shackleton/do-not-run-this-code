package uk.digitalsquid.remme.ingame.questions;

import java.util.ArrayList;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.ingame.views.AsyncImageView;
import uk.digitalsquid.remme.ingame.views.ImageLoader;
import uk.digitalsquid.remme.mgr.details.Contact;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class TextImagePFragment
		extends PairingFragment<TextView, AsyncImageView> {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pendingImages = new ArrayList<AsyncImageView>();
	}

	@Override
	protected int getRootLayoutId() {
		return R.layout.textimagepfragment;
	}

	@Override
	protected AsyncImageView[] getChoiceViews(View rootView) {
		return new AsyncImageView[] {
				(AsyncImageView) rootView.findViewById(R.id.choice1),
				(AsyncImageView) rootView.findViewById(R.id.choice2),
				(AsyncImageView) rootView.findViewById(R.id.choice3),
				(AsyncImageView) rootView.findViewById(R.id.choice4),
		};
	}

	@Override
	protected TextView[] getQuestionViews(View rootView) {
		return new TextView[] {
				(TextView) rootView.findViewById(R.id.question1),
				(TextView) rootView.findViewById(R.id.question2),
				(TextView) rootView.findViewById(R.id.question3),
				(TextView) rootView.findViewById(R.id.question4),
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        Contact[] contacts = question.getContacts();
        int questionType = question.getQuestionType();
        
        // Configure answers. Questions are in the order of getContacts,
        // answers are mapped through correctPairings
        for(int i = 0; i < contacts.length && i < choiceViews.length; i++) {
        	Contact choice = contacts[i];
    		questionViews[i].setText(
    				choice.getTextField(questionType));
        }
        return ret;
	}
	
	/**
	 * A list of images that are still being loaded
	 */
	private ArrayList<AsyncImageView> pendingImages;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		final App app = (App) getActivity().getApplication();

        final int[] correctPairings = question.getCorrectPairings();
		
		// Spark each AsyncImageView off loading its image.
		// Once each view finishes, it marks itself as completed
		// by removing itself from pendingImages. Once this list
		// is empty, the timer may be started.
		final Contact[] contacts = question.getContacts();
        for(int i = 0; i < contacts.length; i++) {
        	final int idx = correctPairings[i];
        	if(idx >= contacts.length) continue;
        	final Contact contact = contacts[i];

        	pendingImages.add(choiceViews[idx]);
        	choiceViews[idx].setImageBitmapAsync(new ImageLoader<AsyncImageView>() {
				@Override
				public Bitmap loadImage(Context context) {
			        return contact.getPhoto(app.getPhotos());
				}

				@Override
				public void onImageLoaded(AsyncImageView asyncImageView) {
					pendingImages.remove(asyncImageView);
					if(pendingImages.size() == 0) {
						if(timer != null)
							timer.start();
					}
				}
			});
        }
	}
}
