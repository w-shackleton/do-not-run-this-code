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

public class ImageTextTFFragment extends TrueFalseFragment<ImageView> {

	@Override
	protected ImageView getQuestionView(View rootView) {
		return (ImageView) rootView.findViewById(R.id.photo);
	}

	@Override
	protected int getRootLayoutId() {
		return R.layout.imagetexttffragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        boolean correctAnswer = question.getCorrectPosition() == 0;
        Contact correct = question.getContact();
        Contact incorrect = question.getOtherAnswers()[0];
        int answerType = question.getAnswerType();
        
        // Configure answer text
        TextView answerView = (TextView) ret.findViewById(R.id.tf_answer);
        answerView.setText((correctAnswer ? correct : incorrect).getTextField(answerType));
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
