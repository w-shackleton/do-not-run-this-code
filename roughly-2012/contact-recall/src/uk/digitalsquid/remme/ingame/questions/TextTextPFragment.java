package uk.digitalsquid.remme.ingame.questions;

import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.mgr.details.Contact;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class TextTextPFragment
		extends PairingFragment<TextView, TextView> {

	@Override
	protected int getRootLayoutId() {
		return R.layout.texttextpfragment;
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
        int questionType = question.getQuestionType();
        int answerType = question.getAnswerType();
        
        // Configure answers. Questions are in the order of getContacts,
        // answers are mapped through correctPairings
        for(int i = 0; i < contacts.length && i < choiceViews.length; i++) {
        	Contact question = contacts[i];
        	questionViews[i].setText(
        			question.getTextField(questionType));

        	final int idx = correctPairings[i];
        	if(idx >= contacts.length) continue;
        	Contact choice = contacts[idx];
    		choiceViews[i].setText(
    				choice.getTextField(answerType));
        }
        return ret;
	}
}
