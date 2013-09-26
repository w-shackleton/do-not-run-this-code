package uk.digitalsquid.contactrecall.ingame.questions;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import android.view.View;
import android.widget.Button;

public abstract class TrueFalseFragment<QView extends View> extends MultiChoiceFragment<QView, Button> {

	@Override
	protected Button[] getChoiceButtons(View rootView) {
		return new Button[] {
				(Button)rootView.findViewById(R.id.choice1),
				(Button)rootView.findViewById(R.id.choice2),
		};
	}

	@Override
	protected void onDataErrorPressed() {
		ArrayList<DataItem> dataItems = new ArrayList<DataItem>();
		DataItem questionItem = new DataItem(question.getContact(), question.getQuestionType());
		dataItems.add(questionItem);

        boolean correctAnswer = question.getCorrectPosition() == 0;
        Contact correct = question.getContact();
        Contact incorrect = question.getOtherAnswers()[0];
        int answerType = question.getAnswerType();

		DataItem answerItem = new DataItem(
				correctAnswer ? correct : incorrect, answerType);
		dataItems.add(answerItem);
        
        callbacks.dataErrorFound(dataItems);
	}
}
