package uk.digitalsquid.contactrecall.ingame.questions;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.views.AsyncImageView;
import uk.digitalsquid.contactrecall.ingame.views.ImageLoader;
import uk.digitalsquid.contactrecall.mgr.Question;
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
	protected int getRootLayoutId(Question question) {
		if(question.getQuestionType() == Question.FIELD_PHOTO) {
			if(question.getAnswerType() == Question.FIELD_DISPLAY_NAME)
				return R.layout.imagetexttffragment_badge;
			if(question.getAnswerType() == Question.FIELD_FIRST_NAME)
				return R.layout.imagetexttffragment_badge;
			if(question.getAnswerType() == Question.FIELD_LAST_NAME)
				return R.layout.imagetexttffragment_badge;
		}
		if(question.getAnswerType() == Question.FIELD_PHOTO){
			if(question.getQuestionType() == Question.FIELD_DISPLAY_NAME)
				return R.layout.imagetexttffragment_badge;
			if(question.getQuestionType() == Question.FIELD_FIRST_NAME)
				return R.layout.imagetexttffragment_badge;
			if(question.getQuestionType() == Question.FIELD_LAST_NAME)
				return R.layout.imagetexttffragment_badge;
		}
		return R.layout.imagetexttffragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        TextView answerView = (TextView) ret.findViewById(R.id.tf_answer);

		if(question.getAnswerFormat() == Question.FORMAT_TEXT) {
	        boolean correctAnswer = question.getCorrectPosition() == 0;
	        Contact correct = question.getContact();
	        Contact incorrect = question.getOtherAnswers()[0];
	        int answerType = question.getAnswerType();
	        
	        // Configure answer text
	        answerView.setText((correctAnswer ? correct : incorrect).getTextField(answerType));
		} else { // Using TextImageTFFragment behaviour - basically flipped round
			// question.getQuestionFormat() === Question.FORMAT_TEXT in this case
	        Contact correct = question.getContact();

	        int questionType = question.getQuestionType();
	        
	        // Configure answer text
	        answerView.setText(correct.getTextField(questionType));
		}
        return ret;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		final App app = (App) getActivity().getApplication();
		if(question.getQuestionFormat() == Question.FORMAT_IMAGE) {
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
		} else { // Answer is the image
	        final boolean correctAnswer = question.getCorrectPosition() == 0;
	        final Contact correct = question.getContact();
	        final Contact incorrect = question.getOtherAnswers()[0];
	        
	        final Contact contact = correctAnswer ? correct : incorrect;

			questionView.setImageBitmapAsync(new ImageLoader<AsyncImageView>() {
				@Override
				public void onImageLoaded(AsyncImageView asyncImageView) {
					if(timer != null) timer.start();
				}
				@Override
				public Bitmap loadImage(Context context) {
			        return contact.getPhoto(app.getPhotos());
				}
			});
		}
	}
}
