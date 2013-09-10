package uk.digitalsquid.contactrecall.ingame.questions;

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

public class ImageTextTFFragment extends TrueFalseFragment<AsyncImageView> {

	@Override
	protected AsyncImageView getQuestionView(View rootView) {
		return (AsyncImageView) rootView.findViewById(R.id.photo);
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
		final App app = (App) getActivity().getApplication();
		questionView.setImageBitmapAsync(new ImageLoader() {
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
