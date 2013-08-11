package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.PairingLayout;
import uk.digitalsquid.contactrecall.ingame.TimerView;
import uk.digitalsquid.contactrecall.ingame.TimerView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Question;
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

public abstract class PairingFragment<QView extends View, AView extends View>
		extends Fragment implements OnClickListener, OnFinishedListener, Config {
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
        
        question = args.getParcelable(ARG_QUESTION);
        descriptor = args.getParcelable(ARG_DESCRIPTOR);
        int numberOfChoices = question.getNumberOfChoices();
        
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
	
	/*
	private void completeView() {
		if(timer != null) timer.cancel();
		
		float delay = (float)(System.nanoTime() - startTime) / (float)1000000000L;
		
		Log.d(TAG, "Chosen contact is " + chosenContact);
		if(callbacks != null) callbacks.choiceMade(chosenContact, choice == question.getCorrectPosition(),
				choice == -1, delay);
		else Log.e(TAG, "Callbacks are currently null!");
	}
	*/

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
		case R.id.data_error:
			// TODO: Show data error screen
			return;
		}
	}

	@Override
	public void onTimerFinished(TimerView view) {
		// -1 indicates no choice was made - advance once user has seen correct
		// answer.
		// TODO: Uncomment
		// completeView(-1);
	}
}
