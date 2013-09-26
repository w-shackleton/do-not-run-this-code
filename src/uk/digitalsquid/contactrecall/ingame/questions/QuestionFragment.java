package uk.digitalsquid.contactrecall.ingame.questions;

import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.GameCallbacks;
import uk.digitalsquid.contactrecall.ingame.views.PointsGainBar;
import uk.digitalsquid.contactrecall.ingame.views.TimingView.OnFinishedListener;
import uk.digitalsquid.contactrecall.mgr.Question;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

/**
 * The base for a displayed question.
 * @author william
 *
 */
public abstract class QuestionFragment extends Fragment
		implements OnFinishedListener, Config {
	public static final String ARG_QUESTION = "question";
	public static final String ARG_DESCRIPTOR = "descriptor";
	
	protected transient GameCallbacks callbacks;
	
	protected Question question;
	protected GameDescriptor descriptor;

	protected PointsGainBar timer;
	
	private boolean startTimerImmediately = true;

	long startTime;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
        question = args.getParcelable(ARG_QUESTION);
        descriptor = args.getParcelable(ARG_DESCRIPTOR);
		setHasOptionsMenu(true);
	}
	
	/**
	 * This method must be called at the end of onCreateView in the parent type
	 * @param rootView
	 */
	protected void configureGlobalViewItems(View rootView) {
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.game_fragment_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case R.id.pause:
			callbacks.pauseGame();
			return true;
		case R.id.data_error:
			onDataErrorPressed();
			return true;
		}
		return false;
	}

	@Override
	public void onStart() {
		super.onStart();
        if(timer != null && isStartTimerImmediately()) timer.start();
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
	
	protected abstract void onDataErrorPressed();
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof GameCallbacks)
			callbacks = (GameCallbacks) activity;
		else
			Log.e(TAG, "onAttach - activity doesn't implement callbacks");
	}

	protected boolean isStartTimerImmediately() {
		return startTimerImmediately;
	}

	protected void setStartTimerImmediately(boolean startTimerImmediately) {
		this.startTimerImmediately = startTimerImmediately;
	}
}
