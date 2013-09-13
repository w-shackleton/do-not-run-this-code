package uk.digitalsquid.contactrecall.ingame.questions;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.views.AsyncImageView;
import uk.digitalsquid.contactrecall.ingame.views.AsyncImageView.ImageLoader;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class ImageTextPFragment
		extends PairingFragment<AsyncImageView, TextView> {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pendingImages = new ArrayList<AsyncImageView>();
	}

	@Override
	protected int getRootLayoutId() {
		return R.layout.imagetextpfragment;
	}

	@Override
	protected AsyncImageView[] getQuestionViews(View rootView) {
		return new AsyncImageView[] {
				(AsyncImageView) rootView.findViewById(R.id.question1),
				(AsyncImageView) rootView.findViewById(R.id.question2),
				(AsyncImageView) rootView.findViewById(R.id.question3),
				(AsyncImageView) rootView.findViewById(R.id.question4),
		};
	}

	@Override
	protected TextView[] getChoiceViews(View rootView) {
		return new TextView[] {
				(TextView) rootView.findViewById(R.id.choice1),
				(TextView) rootView.findViewById(R.id.choice2),
				(TextView) rootView.findViewById(R.id.choice3),
				(TextView) rootView.findViewById(R.id.choice4),
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        Contact[] contacts = question.getContacts();
        int[] correctPairings = question.getCorrectPairings();
        int answerType = question.getAnswerType();
        
        // Configure answers. Questions are in the order of getContacts,
        // answers are mapped through correctPairings
        for(int i = 0; i < contacts.length && i < choiceViews.length; i++) {
        	final int idx = correctPairings[i];
        	if(idx >= contacts.length) continue;
        	Contact choice = contacts[idx];
    		choiceViews[i].setText(
    				choice.getTextField(answerType));
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
		
		// Spark each AsyncImageView off loading its image.
		// Once each view finishes, it marks itself as completed
		// by removing itself from pendingImages. Once this list
		// is empty, the timer may be started.
		final Contact[] contacts = question.getContacts();
        for(int i = 0; i < contacts.length; i++) {
        	final Contact contact = contacts[i];
        	pendingImages.add(questionViews[i]);
        	questionViews[i].setImageBitmapAsync(new ImageLoader() {
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
