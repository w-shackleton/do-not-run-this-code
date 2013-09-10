package uk.digitalsquid.contactrecall.ingame.questions;

import java.util.ArrayList;
import java.util.Arrays;

import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.views.PairingLayout;
import uk.digitalsquid.contactrecall.ingame.views.PairingLayout.OnPairingsChangeListener;
import uk.digitalsquid.contactrecall.ingame.views.TimingView;
import uk.digitalsquid.contactrecall.ingame.views.TimingView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import uk.digitalsquid.contactrecall.misc.Config;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public abstract class PairingFragment<QView extends View, AView extends View>
		extends QuestionFragment implements OnClickListener, OnFinishedListener,
		OnPairingsChangeListener, Config {

	protected QView[] questionViews;
	protected AView[] choiceViews;
	protected PairingLayout pairingLayout;
	
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
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                getRootLayoutId(), root, false);
        
        pairingLayout = (PairingLayout) rootView.findViewById(R.id.pairingLayout);
        pairingLayout.setOnPairingsChangeListener(this);
        
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
        
        configureGlobalViewItems(rootView);
        
		return rootView;
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
	public void onTimerFinished(TimingView view) {
		// Complete with the pairings as far as the user got.
		if(descriptor.isHardTimerPerContact())
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
