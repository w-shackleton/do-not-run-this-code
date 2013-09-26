package uk.digitalsquid.contactrecall.ingame.questions;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.views.TimingView;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Displays a question in one question, multiple answer form.
 * @author william
 *
 */
public abstract class
		MultiChoiceFragment<QView extends View, AButton extends Button>
		extends QuestionFragment implements OnClickListener {
	
	protected QView questionView;
	protected AButton[] choiceButtons;
	
	/**
	 * Returns the ID of the root layout to use.
	 * @param question The question is given to allow custom layouts to be returned
	 * in special cases
	 * @return
	 */
	protected abstract int getRootLayoutId(Question question);
	protected abstract QView getQuestionView(View rootView);
	/**
	 * Returns the choice buttons. Note that these buttons must have IDs
	 * choice1 - choice8
	 * @param rootView
	 * @return
	 */
	protected abstract AButton[] getChoiceButtons(View rootView);
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		super.onCreateView(inflater, root, savedInstanceState);
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                getRootLayoutId(question), root, false);
        
        int numberOfChoices = question.getNumberOfChoices();
        
        choiceButtons = getChoiceButtons(rootView);
        questionView = getQuestionView(rootView);
        
        for(int i = 0; i < numberOfChoices && i < choiceButtons.length; i++) {
        	choiceButtons[i].setOnClickListener(this);
        }
        for(int i = numberOfChoices; i < choiceButtons.length; i++) {
        	choiceButtons[i].setVisibility(View.GONE);
        }
        
        startTime = System.nanoTime();
        
        configureGlobalViewItems(rootView);

		return rootView;
	}
	
	int completedChoice = -2;
	
	private void completeView(final int choice) {
		if(completedChoice != -2) return;
		if(timer != null) timer.cancel();
		completedChoice = choice;
		// Disable further button clicking
		
		// Even though multiple buttons can be pressed, it is
		// when the button is let go that this callback is called

		for(int i = 0; i < question.getNumberOfChoices(); i++) {
			choiceButtons[i].setEnabled(false);
		}

		int choiceType = GameCallbacks.CHOICE_INCORRECT;
		if(choice == question.getCorrectPosition()) choiceType = GameCallbacks.CHOICE_CORRECT;
		if(choice == -1) choiceType = GameCallbacks.CHOICE_TIMEOUT;
		
		Contact chosenContact = null;
		
		// Set button styles accordingly
		switch(choiceType) {
		case GameCallbacks.CHOICE_CORRECT:
			choiceButtons[choice].setBackgroundColor(
					getActivity().getResources().getColor(R.color.correct_actual_bg));
			/* TODO: Do we want to change BG col for other buttons
			for(int i = 0; i < numberOfChoices; i++) {
				if(i == choice) continue;
				choiceButtons[i].setBackgroundColor(context
						getActivity().getResources().getColor(R.color.correct_other_bg));
			} */
			chosenContact = question.getContact();
			break;
		case GameCallbacks.CHOICE_TIMEOUT:
			choiceButtons[question.getCorrectPosition()].setBackgroundColor(
					getActivity().getResources().getColor(R.color.incorrect_actual_bg));
			break;
		case GameCallbacks.CHOICE_INCORRECT:
			choiceButtons[choice].setBackgroundColor(
					getActivity().getResources().getColor(R.color.incorrect_choice_bg));
			choiceButtons[question.getCorrectPosition()].setBackgroundColor(
					getActivity().getResources().getColor(R.color.incorrect_actual_bg));
			/* TODO: Do we want to change BG col for other buttons
			for(int i = 0; i < numberOfChoices; i++) {
				if(i == choice) continue;
				choiceButtons[i].setBackgroundColor(
						getActivity().getResources().getColor(R.color.correct_other_bg));
			} */

			final int otherAnswersIndex =
					choice <= question.getCorrectPosition() ?
							choice : choice - 1;
			if(otherAnswersIndex >= 0 && otherAnswersIndex < question.getOtherAnswers().length)
				chosenContact = question.getOtherAnswers()[otherAnswersIndex];
			break;
		}
		
		float delay = (float)(System.nanoTime() - startTime) / (float)1000000000L;
		
		// TODO: Create a points gain when no timer
		int pointsGain = 0;
		if(timer != null) pointsGain = timer.getVisualPoints();
		// Convert points gain into actual score change
		int pointsDelta;
		switch(choiceType) {
		case GameCallbacks.CHOICE_CORRECT:
			pointsDelta = pointsGain;
			break;
		case GameCallbacks.CHOICE_INCORRECT:
			pointsDelta = -pointsGain / 5;
			break;
		default:
		case GameCallbacks.CHOICE_TIMEOUT:
			pointsDelta = 0;
			break;
		}
		
		Log.d(TAG, "Chosen contact is " + chosenContact);
		if(callbacks != null) callbacks.choiceMade(chosenContact, choiceType, delay, pointsDelta);
		else Log.e(TAG, "Callbacks are currently null!");
	}

	@Override
	public void onClick(View view) {
		int choice;
		switch(view.getId()) {
		default:
		case R.id.choice1: choice = 0; break;
		case R.id.choice2: choice = 1; break;
		case R.id.choice3: choice = 2; break;
		case R.id.choice4: choice = 3; break;
		case R.id.choice5: choice = 4; break;
		case R.id.choice6: choice = 5; break;
		case R.id.choice7: choice = 6; break;
		case R.id.choice8: choice = 7; break;
		}
		completeView(choice);
	}
	
	protected void onDataErrorPressed() {
		ArrayList<DataItem> dataItems = new ArrayList<DataItem>();
		DataItem questionItem = new DataItem(question.getContact(), question.getQuestionType());
		dataItems.add(questionItem);

		// Answers, in correct order
        int posThroughOthers = 0;
        int correctChoice = question.getCorrectPosition();
        int numberOfChoices = question.getNumberOfChoices();
        for(int i = 0; i < numberOfChoices; i++) {
        	if(i == correctChoice)
        		dataItems.add(new DataItem(
        				question.getContact(), question.getAnswerType()));
        	else {
        		dataItems.add(new DataItem(
        				question.getOtherAnswers()[posThroughOthers++],
        				question.getAnswerType()));
        	}
        }
        callbacks.dataErrorFound(dataItems);
	}

	@Override
	public void onTimerFinished(TimingView view) {
		// -1 indicates no choice was made - advance once user has seen correct
		// answer.
		if(descriptor.isHardTimerPerContact())
			completeView(-1);
	}
}
