package uk.digitalsquid.remme.ingame.questions;

import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.details.Contact;
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
public class TextTextTFFragment extends TrueFalseFragment<TextView> {

	@Override
	protected int getRootLayoutId(Question question) {
		return R.layout.texttexttffragment;
	}

	@Override
	protected TextView getQuestionView(View rootView) {
        return (TextView) rootView.findViewById(R.id.question);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		View ret = super.onCreateView(inflater, root, savedInstanceState);

        Contact contact = question.getContact();
        int questionType = question.getQuestionType();
        int answerType = question.getAnswerType();
        
        // Configure question
        questionView.setText(contact.getTextField(questionType));
        
        TextView answerView = (TextView) ret.findViewById(R.id.tf_answer);

        boolean correctAnswer = question.getCorrectPosition() == 0;
        Contact correct = question.getContact();
        Contact incorrect = question.getOtherAnswers()[0];
        
        // Configure answer text
        answerView.setText((correctAnswer ? correct : incorrect).getTextField(answerType));
        return ret;
	}
}
