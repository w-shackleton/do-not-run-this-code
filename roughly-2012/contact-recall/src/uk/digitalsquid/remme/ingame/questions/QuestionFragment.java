package uk.digitalsquid.remme.ingame.questions;

import uk.digitalsquid.remme.GameDescriptor;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.ingame.GameCallbacks;
import uk.digitalsquid.remme.ingame.views.PointsGainBar;
import uk.digitalsquid.remme.ingame.views.TimingView.OnFinishedListener;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.misc.Config;
import uk.digitalsquid.remme.misc.Const;
import uk.digitalsquid.remme.misc.Function;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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
	        final int maxPoints = question.getMaxPoints();
	        timer.setPointsGenerator(new Function<Integer, Float>() {
				@Override
				public Integer call(Float arg) {
					return (int) ((arg + 0.1f) * maxPoints);
				}
			});
        } else {
        	timer.setVisibility(View.INVISIBLE);
        	Log.d(TAG, "Timer invisible");
        	timer = null; // Done with timer now
        }
        
        // Whenever we have a badge, move it around in its parent
        View badge = rootView.findViewById(R.id.badge);
        if(badge != null) {
        	float pos = Const.RAND.nextFloat();
        	badge.setRotation(pos * 20 - 10);
        	// TODO: Translate badge as well?
        	// Can't get horizontal size from here atm
        }

        // The explainer (if it's present) goes between the question and the answer.
        int answerType = question.getAnswerType();
        TextView explainer = (TextView) rootView.findViewById(R.id.explainer);

        if(explainer != null) {
	        switch(answerType) {
	        case Question.FIELD_PHOTO:
	        	explainer.setText(R.string.explainer_photo);
	        	break;
	        case Question.FIELD_DISPLAY_NAME:
	        case Question.FIELD_FIRST_NAME:
	        	explainer.setText(R.string.explainer_name);
	        	break;
	        case Question.FIELD_LAST_NAME:
	        	explainer.setText(R.string.explainer_last_name);
	        	break;
	        case Question.FIELD_COMPANY:
	        case Question.FIELD_DEPARTMENT:
	        	explainer.setText(R.string.explainer_company);
	        	break;
	        case Question.FIELD_COMPANY_TITLE:
	        	explainer.setText(R.string.explainer_company_title);
	        	break;
		
	        case Question.FIELD_PHONE_HOME:
	        case Question.FIELD_PHONE_WORK:
	        case Question.FIELD_PHONE_MOBILE:
	        case Question.FIELD_PHONE_OTHER:
	        	explainer.setText(R.string.explainer_phone);
	        	break;
		
	        case Question.FIELD_EMAIL_HOME:
	        case Question.FIELD_EMAIL_WORK:
	        case Question.FIELD_EMAIL_MOBILE:
	        case Question.FIELD_EMAIL_OTHER:
	        	explainer.setText(R.string.explainer_email);
	        	break;
        	default:
        		explainer.setVisibility(View.GONE);
        		break;
	        }
        }
        
        TextView questionDescription =
        		(TextView) rootView.findViewById(R.id.description_question);
        TextView answerDescription =
        		(TextView) rootView.findViewById(R.id.description_answer);
        if(questionDescription != null)
        	questionDescription.setText(
        			Question.getFieldDescriptionId(question.getQuestionType()));
        if(answerDescription != null)
        	answerDescription.setText(
        			Question.getFieldDescriptionId(question.getAnswerType()));
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
