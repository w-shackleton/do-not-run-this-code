package uk.digitalsquid.remme.ingame;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.ingame.views.ScoreBarView;
import uk.digitalsquid.remme.misc.Config;
import uk.digitalsquid.remme.misc.Utils;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Shows summary information about the game just played
 * @author william
 *
 */
public class ScoreFragment extends Fragment implements OnClickListener, Config {
	
	App app; Context context;
	ArrayList<QuestionFailData> failedContacts;
	
	private int score;
	/**
	 * The score that we would consider 'average'.
	 */
	private int expectedScore;
	
	private boolean firstRun = true;
	private boolean started = false;
	
	private ScoreBarView[] scoreBars;
	private TextView scoreView;
	private TextView qualitativeScoreView;
	private FrameLayout summaryFragmentFrame;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		expectedScore = args.getInt("expectedScore");
		score = args.getInt("score");
		Log.i(TAG, String.format("Expected score: %d, actual score: %d", expectedScore, score));
		ArrayList<QuestionFailData> data = args.getParcelableArrayList("failedContacts");
		Set<QuestionFailData> dataSet = new LinkedHashSet<QuestionFailData>(data);
		failedContacts = new ArrayList<QuestionFailData>(dataSet);
		
		if(savedInstanceState != null) {
			if(savedInstanceState.getBoolean("started", false))
				firstRun = false;
		}

		SummaryFragment summaryFragment = new SummaryFragment();
		summaryFragment.setArguments(args);
		
		getFragmentManager().beginTransaction()
				.add(R.id.summaryFragmentFrame, summaryFragment)
				.commit();
		
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
		qualitativeScoreView = (TextView) rootView.findViewById(R.id.qualitativeScore);
		qualitativeScoreView.setText("");
		qualitativeScoreView.setVisibility(View.INVISIBLE);
		summaryFragmentFrame = (FrameLayout) rootView.findViewById(R.id.summaryFragmentFrame);
		summaryFragmentFrame.setVisibility(View.INVISIBLE);
		
		scoreBars = new ScoreBarView[] {
				(ScoreBarView) rootView.findViewById(R.id.scoreBarLeft),
				(ScoreBarView) rootView.findViewById(R.id.scoreBarRight),
		};
		
		for(ScoreBarView scoreBar : scoreBars) {
			scoreBar.setExpectedScore(expectedScore);
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
	
	void setQualitativeScore(int score) {
		// Normalise score
		String[] texts = getResources().getStringArray(R.array.qualitativeScore);
		float scoreFactor = (float)score / (float)expectedScore;
		String text;
		if(scoreFactor < -0.5f)
			text = getResources().getString(R.string.qualitativeScoreLow);
		else if(scoreFactor > 1.5f)
			text = getResources().getString(R.string.qualitativeScoreHigh);
		else {
			int idx = Utils.minMax((int)((scoreFactor + 0.5f) / 2f * texts.length), 0, texts.length - 1);
			text = texts[idx];
		}
		qualitativeScoreView.setText(text);
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
				setCurrentScore(score);
				setQualitativeScore(score);
				qualitativeScoreView.setVisibility(View.VISIBLE);
				summaryFragmentFrame.setVisibility(View.VISIBLE);
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
		private static final int SHOW_MESSAGE = 2;
		private static final int SHOW_FAILURES = 3;
		
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
			safeSleep(500);
			publishProgress(SHOW_MESSAGE);
			safeSleep(500);
			publishProgress(SHOW_FAILURES);
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
				case SHOW_MESSAGE:
					qualitativeScoreView.setVisibility(View.VISIBLE);
					setQualitativeScore(score);
					break;
				case SHOW_FAILURES:
					summaryFragmentFrame.setVisibility(View.VISIBLE);
					break;
				}
			}
		}
	};
}