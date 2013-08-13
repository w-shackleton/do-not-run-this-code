package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public final class ImageTextPFragment
		extends PairingFragment<ImageView, TextView> {

	@Override
	protected int getRootLayoutId() {
		return R.layout.imagetextpfragment;
	}

	@Override
	protected ImageView[] getQuestionViews(View rootView) {
		return new ImageView[] {
				(ImageView) rootView.findViewById(R.id.question1),
				(ImageView) rootView.findViewById(R.id.question2),
				(ImageView) rootView.findViewById(R.id.question3),
				(ImageView) rootView.findViewById(R.id.question4),
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		App app = (App) getActivity().getApplication();
		
		Contact[] contacts = question.getContacts();
        for(int i = 0; i < contacts.length; i++) {
	        Bitmap bmp = contacts[i].getPhoto(app.getPhotos());

	        questionViews[i].setImageBitmap(bmp);
        }
	}
}
