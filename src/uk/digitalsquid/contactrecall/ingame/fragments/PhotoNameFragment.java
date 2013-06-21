package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.game.GameDescriptor.NamePart;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.Const;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class PhotoNameFragment extends Fragment {
	public static final String ARG_CONTACT = "contact";
	public static final String ARG_OTHER_NAMES = "othernames";
	public static final String ARG_NUMBER_CHOICES = "numchoices";
	
	private App app;
	private ImageView photo;
	private Button[] choiceButtons = new Button[8];
	private int correctChoice;
	private int numberOfChoices;
	
    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        app = (App) getActivity().getApplication();
        
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.photonameview, container, false);
        Bundle args = getArguments();
        
        // TODO: Customisable
        NamePart answerNamePart = NamePart.DISPLAY;
        
        Contact contact = args.getParcelable(ARG_CONTACT);
        int numberOfChoices = args.getInt(ARG_NUMBER_CHOICES);
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
        
        return rootView;
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	outState.putInt("correctChoice", correctChoice);
    	for(int i = 0; i < numberOfChoices; i++) {
    		outState.putString(String.format("choiceButtonText%d", i),
    				choiceButtons[i].getText().toString());
    	}
    }
}
