package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.GameDescriptor.NamePart;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.TimerView;
import uk.digitalsquid.contactrecall.ingame.TimerView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * A view that shows a photo and a set of buttons representing names
 * @author william
 *
 */
public class PhotoNameView extends Fragment implements OnClickListener, OnFinishedListener, Config {
	public static final String ARG_QUESTION = "question";
	public static final String ARG_DESCRIPTOR = "descriptor";
	
	private transient GameCallbacks callbacks;
	
	private Question question;
	private GameDescriptor descriptor;
	
	private ImageView photo;
	private Button[] choiceButtons = new Button[8];
	private TimerView timer;
	
	long startTime;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		super.onCreateView(inflater, root, savedInstanceState);
		Bundle args = getArguments();
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.photonameview, root, false);
        
        
        question = args.getParcelable(ARG_QUESTION);
        descriptor = args.getParcelable(ARG_DESCRIPTOR);
        int correctChoice = question.getCorrectPosition();
        int numberOfChoices = question.getNumberOfChoices();
        Contact contact = question.getContact();
        NamePart answerNamePart = question.getNamePart();
        
        choiceButtons[0] = (Button) rootView.findViewById(R.id.choice1);
        choiceButtons[1] = (Button) rootView.findViewById(R.id.choice2);
        choiceButtons[2] = (Button) rootView.findViewById(R.id.choice3);
        choiceButtons[3] = (Button) rootView.findViewById(R.id.choice4);
        choiceButtons[4] = (Button) rootView.findViewById(R.id.choice5);
        choiceButtons[5] = (Button) rootView.findViewById(R.id.choice6);
        choiceButtons[6] = (Button) rootView.findViewById(R.id.choice7);
        choiceButtons[7] = (Button) rootView.findViewById(R.id.choice8);
        photo = (ImageView) rootView.findViewById(R.id.photo);
        
        for(int i = 0; i < numberOfChoices; i++) {
        	choiceButtons[i].setOnClickListener(this);
        }
        for(int i = numberOfChoices; i < choiceButtons.length; i++) {
        	choiceButtons[i].setVisibility(View.GONE);
        }
        
        // Configure question
        int posThroughOthers = 0;
        for(int i = 0; i < numberOfChoices; i++) {
        	if(i == correctChoice)
		        choiceButtons[i].setText(contact.getNamePart(answerNamePart));
        	else {
        		choiceButtons[i].setText(
        				question.getOtherAnswers()[posThroughOthers++].getNamePart(answerNamePart));
        	}
        }
        
        startTime = System.nanoTime();
        
        timer = (TimerView) rootView.findViewById(R.id.timerView);
        timer.setOnFinishedListener(this);
        timer.setTotalTime(descriptor.getMaxTimePerContact());
        
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
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		App app = (App) getActivity().getApplication();
		// TODO: Background this?
        Bitmap bmp = question.getContact().getPhoto(app.getPhotos());
        photo.setImageBitmap(bmp);
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
	
	private void completeView(int choice) {
		if(completedChoice != -2) return;
		if(timer != null) timer.cancel();
		completedChoice = choice;
		// Disable further button clicking
		// TODO: Check user can't cheat using Android multitouch features,
		// eg. press all four buttons at once.
		for(int i = 0; i < question.getNumberOfChoices(); i++) {
			choiceButtons[i].setEnabled(false);
		}
		
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
		}
		
		float delay = (float)(System.nanoTime() - startTime) / (float)1000000000L;
		
		Contact chosenContact = null;
		if(choice >= 0 && choice < question.getOtherAnswers().length)
			chosenContact = question.getOtherAnswers()[choice];
		// TODO: Chosen contact is being null here.
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
