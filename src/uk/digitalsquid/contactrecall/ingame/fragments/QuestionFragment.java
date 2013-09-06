package uk.digitalsquid.contactrecall.ingame.fragments;

import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.PointsGainBar;
import uk.digitalsquid.contactrecall.ingame.TimingView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * The base for a displayed question.
 * @author william
 *
 */
public abstract class QuestionFragment extends Fragment
		implements OnClickListener, OnFinishedListener, Config {
	public static final String ARG_QUESTION = "question";
	public static final String ARG_DESCRIPTOR = "descriptor";
	
	protected transient GameCallbacks callbacks;
	
	protected Question question;
	protected GameDescriptor descriptor;

	protected PointsGainBar timer;

	long startTime;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
        question = args.getParcelable(ARG_QUESTION);
        descriptor = args.getParcelable(ARG_DESCRIPTOR);
	}
	
	/**
	 * This method must be called at the end of onCreateView in the parent type
	 * @param rootView
	 */
	protected void configureGlobalViewItems(View rootView) {
        // Assign data error button if it exists
        Button dataError = (Button) rootView.findViewById(R.id.data_error);
        if(dataError != null) dataError.setOnClickListener(this);
        
        startTime = System.nanoTime();

        timer = (PointsGainBar) rootView.findViewById(R.id.pointsGainBar);
        if(descriptor.hasTimerPerContact()) {
	        timer.setOnFinishedListener(this);
	        timer.setTotalTime(descriptor.getMaxTimePerContact());
        } else {
        	timer.setVisibility(View.GONE);
        	timer = null; // Done with timer now
        }
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
}
