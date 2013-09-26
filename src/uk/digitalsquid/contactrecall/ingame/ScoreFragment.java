package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.views.ScoreBarView;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * Shows summary information about the game just played
 * @author william
 *
 */
public class ScoreFragment extends Fragment implements OnClickListener {
	
	App app; Context context;
	ArrayList<QuestionFailData> failedContacts;
	
	private int score;
	
	private boolean firstRun = true;
	private boolean started = false;
	
	private ScoreBarView[] scoreBars;
	private TextView scoreView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		score = args.getInt("score");
		ArrayList<QuestionFailData> data = args.getParcelableArrayList("failedContacts");
		Set<QuestionFailData> dataSet = new LinkedHashSet<QuestionFailData>(data);
		failedContacts = new ArrayList<QuestionFailData>(dataSet);
		
		if(savedInstanceState != null) {
			if(savedInstanceState.getBoolean("started", false))
				firstRun = false;
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		app = (App) activity.getApplication();
		context = activity.getBaseContext();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.score_fragment, container, false);
		
		scoreView = (TextView) rootView.findViewById(R.id.score);
		scoreView.setText("");
		
		scoreBars = new ScoreBarView[] {
				(ScoreBarView) rootView.findViewById(R.id.scoreBarLeft),
				(ScoreBarView) rootView.findViewById(R.id.scoreBarRight),
		};
		
		for(ScoreBarView scoreBar : scoreBars) {
			// TODO: Calculate expected score
			scoreBar.setExpectedScore(4000);
			scoreBar.setScore(score);
		}
		
		return rootView;
	}
	
	private int currentScore;
	
	public void setCurrentScore(int score) {
		scoreView.setText(String.format(Locale.getDefault(), "%d", score));
	}
	
	public int getCurrentScore() {
		return currentScore;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(!started) {
			started = true;
			if(firstRun) {
				animatorTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				for(ScoreBarView scoreBar : scoreBars) {
					scoreBar.showImmediateScore();
				}
			}
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.game_summary);
	}
	
	@Override
	public void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);
		out.putBoolean("started", true);
	}

	@Override
	public void onClick(View v) {
	}
	
	private AsyncTask<Void, Integer, Void> animatorTask = new AsyncTask<Void, Integer, Void>() {
		
		private static final int START_SCORE = 1;
		
		private void safeSleep(long millis) {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) { }
		}

		@Override
		protected Void doInBackground(Void... params) {
			safeSleep(1000);
			publishProgress(START_SCORE);
			safeSleep(2000);
			safeSleep(1000);
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			for(int state : values) {
				switch(state) {
				case START_SCORE:
					for(ScoreBarView scoreBar : scoreBars) {
						scoreBar.start(2000);
						ObjectAnimator scoreAnim = ObjectAnimator.ofInt(
								ScoreFragment.this, "currentScore", 0, score);
						scoreAnim.setInterpolator(new AccelerateDecelerateInterpolator());
						scoreAnim.setDuration(2000);
						scoreAnim.start();
					}
					break;
				}
			}
		}
	};
}