package uk.digitalsquid.remme.ingame.questions;

import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.details.Contact;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Displays a question with a picture as the question and
 * text as the answers.
 * @author william
 *
 */
public class TextTextMCFragment extends MultiChoiceFragment<TextView, Button> {

	@Override
	protected int getRootLayoutId(Question question) {
		if(question.getQuestionType() == Question.FIELD_FIRST_NAME &&
				question.getAnswerType() == Question.FIELD_LAST_NAME)
			return R.layout.texttextmcfragment_vertical_left;
		if(question.getQuestionType() == Question.FIELD_LAST_NAME &&
				question.getAnswerType() == Question.FIELD_FIRST_NAME)
			return R.layout.texttextmcfragment_vertical_right;
		return R.layout.texttextmcfragment;
	}

	@Override
	protected TextView getQuestionView(View rootView) {
        return (TextView) rootView.findViewById(R.id.question);
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
        int questionType = question.getQuestionType();
        int answerType = question.getAnswerType();
        
        // Configure question
        questionView.setText(contact.getTextField(questionType));
        
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
}
