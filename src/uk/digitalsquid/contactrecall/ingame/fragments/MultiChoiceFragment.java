package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.TimerView;
import uk.digitalsquid.contactrecall.ingame.TimerView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
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
public abstract class MultiChoiceFragment<QView extends View, AButton extends Button> extends Fragment implements OnClickListener, OnFinishedListener, Config {
	public static final String ARG_QUESTION = "question";
	public static final String ARG_DESCRIPTOR = "descriptor";
	
	private transient GameCallbacks callbacks;
	
	protected Question question;
	private GameDescriptor descriptor;
	
	protected QView questionView;
	protected AButton[] choiceButtons;
	private TimerView timer;
	
	long startTime;
	
	protected abstract int getRootLayoutId();
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
		Bundle args = getArguments();
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                getRootLayoutId(), root, false);
        
        // Assign data error button if it exists
        Button dataError = (Button) rootView.findViewById(R.id.data_error);
        if(dataError != null) dataError.setOnClickListener(this);
        
        question = args.getParcelable(ARG_QUESTION);
        descriptor = args.getParcelable(ARG_DESCRIPTOR);
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
        
        timer = (TimerView) rootView.findViewById(R.id.timerView);
        if(descriptor.hasTimerPerContact()) {
	        timer.setOnFinishedListener(this);
	        timer.setTotalTime(descriptor.getMaxTimePerContact());
	        timer.setTextAsCountdown();
        } else {
        	timer.setVisibility(View.GONE);
        	timer = null; // Done with timer now
        }
        
		return rootView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
        if(timer != null) timer.start();
	}
	@Override
	public void onStop() {
		super.onStop();
		if(timer != null) timer.cancel();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(timer != null) timer.setOnFinishedListener(null);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof GameCallbacks)
			callbacks = (GameCallbacks) activity;
		else
			Log.e(TAG, "onAttach - activity doesn't implement callbacks");
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
		
		Contact chosenContact = null;
		
		// Set button styles accordingly
		if(choice == question.getCorrectPosition()) {
			choiceButtons[choice].setBackgroundColor(
					getActivity().getResources().getColor(R.color.correct_actual_bg));
			/* TODO: Do we want to change BG col for other buttons
			for(int i = 0; i < numberOfChoices; i++) {
				if(i == choice) continue;
				choiceButtons[i].setBackgroundColor(context
						getActivity().getResources().getColor(R.color.correct_other_bg));
			} */
			chosenContact = question.getContact();
		} else if(choice == -1) {
			choiceButtons[question.getCorrectPosition()].setBackgroundColor(
					getActivity().getResources().getColor(R.color.incorrect_actual_bg));
		} else {
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
		}
		
		float delay = (float)(System.nanoTime() - startTime) / (float)1000000000L;
		
		Log.d(TAG, "Chosen contact is " + chosenContact);
		if(callbacks != null) callbacks.choiceMade(chosenContact, choice == question.getCorrectPosition(),
				choice == -1, delay);
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
		
		case R.id.data_error:
			// TODO: Show data error screen
			return;
		}
		completeView(choice);
	}

	@Override
	public void onTimerFinished(TimerView view) {
		// -1 indicates no choice was made - advance once user has seen correct
		// answer.
		completeView(-1);
	}
}
