package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor.NamePart;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import uk.digitalsquid.contactrecall.misc.Const;
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
public class PhotoNameView extends Fragment implements OnClickListener, Config {
	public static final String ARG_CONTACT = "contact";
	public static final String ARG_OTHER_NAMES = "othernames";
	public static final String ARG_NUMBER_CHOICES = "numchoices";
	
	private transient GameCallbacks callbacks;
	
	private ImageView photo;
	private Contact contact;
	private Button[] choiceButtons = new Button[8];
	private int correctChoice;
	private int numberOfChoices;
	
	public PhotoNameView() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle savedInstanceState) {
		super.onCreateView(inflater, root, savedInstanceState);
		Bundle args = getArguments();
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.photonameview, root, false);
        
        // TODO: Customisable
        NamePart answerNamePart = NamePart.DISPLAY;
        
        contact = args.getParcelable(ARG_CONTACT);
        numberOfChoices = args.getInt(ARG_NUMBER_CHOICES);
        if(numberOfChoices == 0) numberOfChoices = 4;
        Contact[] otherAnswers = (Contact[]) args.getParcelableArray(ARG_OTHER_NAMES);
        
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
        
        // Configure game - either restore state or create anew.
        
        if(savedInstanceState != null) {
        	correctChoice = savedInstanceState.getInt("correctChoice");
        	for(int i = 0; i < numberOfChoices; i++) {
        		String text = savedInstanceState.getString(
        				String.format("choiceButtonText%d", i));
        		if(text != null) choiceButtons[i].setText(text);
        	}
        	
        	// TODO: Not recovering completed state
        	if(savedInstanceState.getInt("completedChoice", -1) != -1) {
        		completeView(savedInstanceState.getInt("completedChoice", -1));
        	}
        } else {
	        correctChoice = Const.RAND.nextInt(numberOfChoices);
	        String correctText = contact.getNamePart(answerNamePart);
	        for(int i = 0; i < numberOfChoices; i++) {
	        	if(i == correctChoice)
			        choiceButtons[i].setText(correctText);
	        	else {
	        		String name = "";
	        		for(int j = 0; j < 10; j++) { // Attempt to find a different name
	        			name = otherAnswers[Const.RAND.nextInt(otherAnswers.length)].getNamePart(answerNamePart);
	        			if(!name.equalsIgnoreCase(correctText)) break;
	        		}
	        		choiceButtons[i].setText(name);
	        	}
	        }
        }
		return rootView;
	}
	
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
        // Show photo
		App app = (App) getActivity().getApplication();
        Bitmap bmp = contact.getPhoto(app.getPhotos());
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
	
    public void onSaveInstanceState(Bundle outState) {
    	outState.putInt("correctChoice", correctChoice);
    	for(int i = 0; i < numberOfChoices; i++) {
    		outState.putString(String.format("choiceButtonText%d", i),
    				choiceButtons[i].getText().toString());
    	}
    	outState.putInt("completedChoice", completedChoice);
    }
	
	int completedChoice = -1;
	
	private void completeView(int choice) {
		completedChoice = choice;
		// Disable further button clicking
		// TODO: Check user can't cheat using Android multitouch features,
		// eg. press all four buttons at once.
		for(int i = 0; i < numberOfChoices; i++) {
			choiceButtons[i].setEnabled(false);
		}
		
		// Set button styles accordingly
		if(choice == correctChoice) {
			choiceButtons[choice].setBackgroundColor(
					getActivity().getResources().getColor(R.color.correct_actual_bg));
			/* TODO: Do we want to change BG col for other buttons
			for(int i = 0; i < numberOfChoices; i++) {
				if(i == choice) continue;
				choiceButtons[i].setBackgroundColor(context
						getActivity().getResources().getColor(R.color.correct_other_bg));
			} */
		} else {
			choiceButtons[choice].setBackgroundColor(
					getActivity().getResources().getColor(R.color.incorrect_choice_bg));
			choiceButtons[correctChoice].setBackgroundColor(
					getActivity().getResources().getColor(R.color.incorrect_actual_bg));
			/* TODO: Do we want to change BG col for other buttons
			for(int i = 0; i < numberOfChoices; i++) {
				if(i == choice) continue;
				choiceButtons[i].setBackgroundColor(
						getActivity().getResources().getColor(R.color.correct_other_bg));
			} */
		}
		if(callbacks != null) callbacks.choiceMade(choice, choice == correctChoice);
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
}
