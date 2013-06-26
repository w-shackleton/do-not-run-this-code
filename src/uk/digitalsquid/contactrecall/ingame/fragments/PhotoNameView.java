package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.game.GameDescriptor.NamePart;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.Const;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
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
public class PhotoNameView implements OnClickListener {
	public static final String ARG_CONTACT = "contact";
	public static final String ARG_OTHER_NAMES = "othernames";
	public static final String ARG_NUMBER_CHOICES = "numchoices";
	
	private ImageView photo;
	private Button[] choiceButtons = new Button[8];
	private int correctChoice;
	private int numberOfChoices;
	
	private View rootView;
	
	public PhotoNameView(App app, Context context, ViewGroup root, Bundle args, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        rootView = LayoutInflater.from(context).inflate(
                R.layout.photonameview, root, false);
        
        // TODO: Customisable
        NamePart answerNamePart = NamePart.DISPLAY;
        
        Contact contact = args.getParcelable(ARG_CONTACT);
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
        
        for(int i = numberOfChoices; i < choiceButtons.length; i++) {
        	choiceButtons[i].setVisibility(View.GONE);
        	choiceButtons[i].setOnClickListener(this);
        }
        
        // Configure game - either restore state or create anew.
        
        if(savedInstanceState != null) {
        	correctChoice = savedInstanceState.getInt("correctChoice");
        	for(int i = 0; i < numberOfChoices; i++) {
        		String text = savedInstanceState.getString(
        				String.format("choiceButtonText%d", i));
        		if(text != null) choiceButtons[i].setText(text);
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
        
        // Show photo
        Bitmap bmp = contact.getPhoto(app.getPhotos());
        photo.setImageBitmap(bmp);
	}
	
    public void onSaveInstanceState(Bundle outState) {
    	outState.putInt("correctChoice", correctChoice);
    	for(int i = 0; i < numberOfChoices; i++) {
    		outState.putString(String.format("choiceButtonText%d", i),
    				choiceButtons[i].getText().toString());
    	}
    }

	public View getRootView() {
		return rootView;
	}

	@Override
	public void onClick(View view) {
		int choice;
		switch(view.getId()) {
		default:
		case R.id.choice1: choice = 0;
		case R.id.choice2: choice = 1;
		case R.id.choice3: choice = 2;
		case R.id.choice4: choice = 3;
		case R.id.choice5: choice = 4;
		case R.id.choice6: choice = 5;
		case R.id.choice7: choice = 6;
		case R.id.choice8: choice = 7;
		}
		// Disable further button clicking
		// TODO: Check user can't cheat using Android multitouch features
		for(int i = 0; i < numberOfChoices; i++) {
			choiceButtons[i].setEnabled(false);
		}
	}
}
