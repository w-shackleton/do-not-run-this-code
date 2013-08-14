package uk.digitalsquid.contactrecall.ingame.fragments;

import java.util.ArrayList;
import java.util.Arrays;

import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.PairingLayout;
import uk.digitalsquid.contactrecall.ingame.PairingLayout.OnPairingsChangeListener;
import uk.digitalsquid.contactrecall.ingame.TimerView;
import uk.digitalsquid.contactrecall.ingame.TimerView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public abstract class PairingFragment<QView extends View, AView extends View>
		extends Fragment implements OnClickListener, OnFinishedListener,
		OnPairingsChangeListener, Config {
	public static final String ARG_QUESTION = "question";
	public static final String ARG_DESCRIPTOR = "descriptor";
	
	private transient GameCallbacks callbacks;
	
	protected Question question;
	private GameDescriptor descriptor;
	
	protected QView[] questionViews;
	protected AView[] choiceViews;
	protected PairingLayout pairingLayout;
	private TimerView timer;
	
	long startTime;

	private transient int[] currentPairings;
	
	protected abstract int getRootLayoutId();
	/**
	 * Returns the question views. Note that these views must have IDs
	 * question1 - question8
	 * @param rootView
	 * @return
	 */
	protected abstract QView[] getQuestionViews(View rootView);
	/**
	 * Returns the choice buttons. Note that these buttons must have IDs
	 * choice1 - choice8
	 * @param rootView
	 * @return
	 */
	protected abstract AView[] getChoiceViews(View rootView);
	
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
        
        pairingLayout = (PairingLayout) rootView.findViewById(R.id.pairingLayout);
        pairingLayout.setOnPairingsChangeListener(this);
        
        question = args.getParcelable(ARG_QUESTION);
        descriptor = args.getParcelable(ARG_DESCRIPTOR);
        int numberOfChoices = question.getNumberOfChoices();
        
        currentPairings = new int[question.getContactCount()];
        Arrays.fill(currentPairings, -1);
        
        questionViews = getQuestionViews(rootView);
        choiceViews = getChoiceViews(rootView);
        
        for(int i = numberOfChoices; i < questionViews.length; i++) {
        	questionViews[i].setVisibility(View.GONE);
        }
        for(int i = numberOfChoices; i < choiceViews.length; i++) {
        	choiceViews[i].setVisibility(View.GONE);
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
	
	/**
	 * Completes this question. If any values in pairings are still -1 then
	 * a timeout is assumed.
	 * @param pairings
	 */
	private void completeView(int[] pairings) {
		if(timer != null) timer.cancel();
		
		float delay = (float)(System.nanoTime() - startTime) / (float)1000000000L;
		
		int[] correctPairings = question.getCorrectPairings();
		if(correctPairings.length != pairings.length) {
			Log.e(TAG, "Malformed pairings detected");
		}
		// TODO: Do we want to store the contact they mistakenly thought was
		// this contact?
		ArrayList<Contact> correct = new ArrayList<Contact>(),
				timeout = new ArrayList<Contact>();
		ArrayList<Pair<Contact, Contact>> incorrect = new ArrayList<Pair<Contact,Contact>>();
		for(int i = 0; i < Math.min(pairings.length, correctPairings.length); i++) {
			if(pairings[i] == correctPairings[i])
				correct.add(question.getContact(i));
			else if(pairings[i] == -1)
				timeout.add(question.getContact(i));
			else {
				if(pairings[i] < question.getContacts().length)
					incorrect.add(new Pair<Contact, Contact>(
							question.getContact(i),
							question.getContact(pairings[i])));
			}
		}
		
		callbacks.pairingChoiceMade(correct, incorrect, timeout, delay);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.data_error:
			// Questions
			ArrayList<DataItem> dataItems = new ArrayList<DataItem>();
			for(Contact contact : question.getContacts()) {
				DataItem item = new DataItem(contact, question.getQuestionType());
				dataItems.add(item);
			}
			// Answers - in order
			for(int idx : question.getCorrectPairings()) {
				if(idx < question.getContactCount()) {
					Contact contact = question.getContact(idx);
					DataItem item = new DataItem(contact, question.getAnswerType());
					dataItems.add(item);
				}
			}
			callbacks.dataErrorFound(dataItems);
			return;
		}
	}
	
	@Override
	public void onTimerFinished(TimerView view) {
		// Complete with the pairings as far as the user got.
		completeView(currentPairings);
	}

	@Override
	public void onPairingsChanged(int[] pairings) {
		currentPairings = pairings;
	}

	@Override
	public void onPairingsCompleted(int[] pairings) {
		completeView(pairings);
	}
}
