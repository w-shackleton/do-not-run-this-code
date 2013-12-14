package uk.digitalsquid.remme.ingame;

import java.util.ArrayList;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.GameDescriptor;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.ingame.views.TimerView;
import uk.digitalsquid.remme.ingame.views.TimingView;
import uk.digitalsquid.remme.ingame.views.TimingView.OnFinishedListener;
import uk.digitalsquid.remme.mgr.Question;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.mgr.details.DataItem;
import uk.digitalsquid.remme.misc.Config;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GameFragment extends Fragment implements GameCallbacks, OnFinishedListener, Config {
	App app;
	
	GameDescriptor gameDescriptor;
	GameAdapter gameAdapter;
	FrameLayout frameLayout;
	Handler handler = new Handler();
	
	private TimerView timer;
	private TextView correctCountView, incorrectCountView, scoreView;
	private int correctCount = 0, incorrectCount = 0, discardCount = 0;
	private int score = 0;
	
	private boolean gamePaused;
	private boolean gameFinished = false;
	
	int position = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args == null) return;
		gameDescriptor = args.getParcelable("gameDescriptor");
		
		if(savedInstanceState != null) {
			position = savedInstanceState.getInt("position");
			correctCount = savedInstanceState.getInt("correctCount", 0);
			incorrectCount = savedInstanceState.getInt("incorrectCount", 0);
			discardCount = savedInstanceState.getInt("discardCount", 0);
			score = savedInstanceState.getInt("score", 0);
			setGamePaused(savedInstanceState.getBoolean("gamePaused"));
			setGameFinished(savedInstanceState.getBoolean("gameFinished"));
			failedContacts = savedInstanceState.getParcelableArrayList("failedContacts");
		} else {
			failedContacts = new ArrayList<QuestionFailData>();
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.game_fragment, container, false);
		
		rootView.setBackgroundResource(R.drawable.game_bg);
		
		frameLayout = (FrameLayout) rootView.findViewById(R.id.pager);
		
		timer = (TimerView)rootView.findViewById(R.id.timer);

		correctCountView = (TextView)rootView.findViewById(R.id.correctCount);
		incorrectCountView = (TextView)rootView.findViewById(R.id.incorrectCount);
		scoreView = (TextView)rootView.findViewById(R.id.score);
		correctCountView.setText(String.format("%d", correctCount));
		incorrectCountView.setText(String.format("%d", incorrectCount));
		scoreView.setText(String.format("%d", score));

        if(gameDescriptor.hasTimer()) {
	        timer.setOnFinishedListener(this);
	        timer.setTotalTime(gameDescriptor.getMaxTime());
	        timer.setTextAsCountdown();
        } else {
        	timer.setVisibility(View.GONE);
        	timer = null; // Done with timer now
        }
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		app = (App)getActivity().getApplication();
		
		if(gameAdapter == null)
			gameAdapter = getGameAdapter(gameDescriptor, savedInstanceState);
		else // Otherwise, replace context and app, to reduce dead objects
			gameAdapter.init(app, getActivity(), this);
		
		if(gameAdapter.getCount() == 0) {
			Toast.makeText(getActivity(), "No questions to ask!", Toast.LENGTH_LONG).show();
			getActivity().finish();
			return;
		}
		
		Fragment f = gameAdapter.getFragment(position);
		if(f == null) {
			Log.e(Game.TAG, "Null question fragment! Moving on to second question");
			Toast.makeText(getActivity(), "No questions to ask! Please select / add more contacts", Toast.LENGTH_LONG).show();
			getActivity().finish();
			return;
		}
		getFragmentManager().beginTransaction().
			replace(R.id.pager, f).commit();
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if(timer != null) timer.start();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	/**
	 * Generates a {@link GameAdapter}
	 * @param descriptor
	 * @return
	 */
	GameAdapter getGameAdapter(GameDescriptor descriptor, Bundle savedInstanceState) {
		if(savedInstanceState == null) {
			return new GameAdapter(getActivity(), app, descriptor, this);
		} else {
			GameAdapter gameAdapter = savedInstanceState.getParcelable("gameAdapter");
			if(gameAdapter == null) return getGameAdapter(descriptor, null);
			gameAdapter.init(app, getActivity(), this);
			return gameAdapter;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(gameAdapter != null) {
			outState.putParcelable("gameAdapter", gameAdapter);
			outState.putInt("position", position);
			outState.putInt("correctCount", correctCount);
			outState.putInt("incorrectCount", incorrectCount);
			outState.putInt("discardCount", discardCount);
			outState.putInt("score", score);
			outState.putBoolean("gamePaused", gamePaused);
			outState.putBoolean("gameFinished", isGameFinished());
			outState.putParcelableArrayList("failedContacts", failedContacts);
		}
	}

	@Override
	public void choiceMade(Contact choice, int choiceType, float timeTaken, int pointsGain) {
		if(isGamePaused()) return;
		position++;
		postAdvanceQuestion(choiceType);
		
		score += pointsGain;
		
		// Already generated, just getting question
		Question question = gameAdapter.getItem(position-1);
		if(question == null) {
			Log.w(TAG, "choiceMade came across a null question");
			return;
		}
		Contact contact = question.getContact();
		// Add to persistent DB
		switch(choiceType) {
		case CHOICE_TIMEOUT:
			app.getDb().progress.addTimeout(contact, timeTaken);
			break;
		case CHOICE_CORRECT:
			app.getDb().progress.addSuccess(contact, timeTaken);
			correctCount++;
			break;
		case CHOICE_INCORRECT:
			app.getDb().progress.addFail(contact, choice, timeTaken);
			incorrectCount++;
			break;
		case CHOICE_DISCARD:
			app.getDb().progress.addDiscard(contact, timeTaken);
			discardCount++;
			break;
		}
		// Add to local data
		QuestionFailData data;
		switch(choiceType) {
		case CHOICE_DISCARD:
		case CHOICE_TIMEOUT:
			data = new QuestionFailData();
			data.question = question;
			data.contact = question.getContact();
			failedContacts.add(data);
			break;
		case CHOICE_CORRECT:
			break;
		case CHOICE_INCORRECT:
			data = new QuestionFailData();
			data.question = question;
			data.contact = question.getContact();
			data.incorrectChoice = choice;
			failedContacts.add(data);
			break;
		}
	}

	@Override
	public void pairingChoiceMade(ArrayList<Contact> correct,
			ArrayList<Pair<Contact, Contact>> incorrect, ArrayList<Contact> timeout,
			float timeTaken, int pointsGain) {
		if(isGamePaused()) return;
		position++;
		// true if all is fully correct
		Log.v(TAG, String.format("Pairing answered: (c:%d,i:%d,t:%d)", correct.size(),
				incorrect.size(), timeout.size()));
		boolean allCorrect = incorrect.size() == 0 && timeout.size() == 0;
		postAdvanceQuestion(allCorrect ? CHOICE_CORRECT : CHOICE_INCORRECT);
		if(allCorrect) correctCount++;
		else incorrectCount++;
		
		score += pointsGain;
		
		// Already generated, just getting question
		Question question = gameAdapter.getItem(position-1);
		// Add each type of result to persistent DB
		for(Contact contact : correct) {
			app.getDb().progress.addSuccess(contact, timeTaken);
		}
		for(Contact contact : timeout) {
			app.getDb().progress.addTimeout(contact, timeTaken);

			QuestionFailData data = new QuestionFailData();
			data.question = question;
			data.contact = contact;
			failedContacts.add(data);
		}
		for(Pair<Contact, Contact> pair : incorrect) {
			app.getDb().progress.addFail(pair.first, pair.second, timeTaken);

			QuestionFailData data = new QuestionFailData();
			data.question = question;
			data.contact = pair.first;
			data.incorrectChoice = pair.second;
			failedContacts.add(data);
		}
	}
	
	/**
	 * Changes the view to the next question. Does not change any game data.
	 * @param correct
	 */
	private void postAdvanceQuestion(final int choiceType) {
		if(gameFinished) return;
		int waitTimeId, animInTmp, animOutTmp;
		switch(choiceType) {
		case CHOICE_CORRECT: default:
			waitTimeId = R.integer.page_correct_wait_time;
			animInTmp = R.animator.next_card_in;
			animOutTmp = R.animator.next_card_out;
			break;
		case CHOICE_INCORRECT:
		case CHOICE_TIMEOUT:
			waitTimeId = R.integer.page_incorrect_wait_time;
			animInTmp = R.animator.next_card_in;
			animOutTmp = R.animator.next_card_out_incorrect;
			break;
		case CHOICE_DISCARD:
			waitTimeId = R.integer.page_discard_wait_time;
			animInTmp = R.animator.discard_card_in;
			animOutTmp = R.animator.discard_card_out;
			break;
		}
		final int animIn = animInTmp, animOut = animOutTmp;
		if(position >= gameAdapter.getCount()) {
			gameFinished = true;
			// Run transition to score card.
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					ScoreFragment fragment = new ScoreFragment();
					Bundle args = new Bundle();
					args.putParcelableArrayList("failedContacts", failedContacts);
					args.putInt("score", score);
					args.putInt("expectedScore", gameAdapter.getExpectedScore());
					fragment.setArguments(args);
					if(getFragmentManager() != null)
						getFragmentManager().beginTransaction().
							setCustomAnimations(R.animator.summary_card_in, animOut).
							replace(R.id.container, fragment).
							commit();
					Game activity = (Game) getActivity();
					if(activity != null) {
						activity.setGameFinished();
					}
				}
			}, getResources().getInteger(waitTimeId));
		} else {
			// Run transition to next card after the given delay.
			// The position has already been updated so we shouldn't have
			// to worry about view rotation etc., in which case
			// the next card would be shown immediately
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					Fragment f = gameAdapter.getFragment(position);
					if(f == null) {
						Log.e(Game.TAG, "Null question fragment! Moving on to next");
						position++;
						postAdvanceQuestion(choiceType);
						return;
					}
					
					// Update scores
					correctCountView.setText(String.format("%d", correctCount));
					incorrectCountView.setText(String.format("%d", incorrectCount));
					scoreView.setText(String.format("%d", score));

					if(getFragmentManager() != null)
						getFragmentManager().beginTransaction().
							setCustomAnimations(animIn, animOut).
							replace(R.id.pager, f).
							commit();
				}
			}, getResources().getInteger(waitTimeId));
		}
	}

	boolean isGamePaused() {
		return gamePaused;
	}

	@Override
	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
	}
	
	private ArrayList<QuestionFailData> failedContacts;
	
	@Override
	public void dataErrorFound(ArrayList<DataItem> possibleErrors) {
		Log.e(Game.TAG, "dataErrorFound called inside fragment");
	}

	@Override
	public void onTimerFinished(TimingView view) {
		position = gameAdapter.getCount();
		postAdvanceQuestion(CHOICE_DISCARD);
	}

	@Override
	public void pauseGame() { }

	public boolean isGameFinished() {
		return gameFinished;
	}

	public void setGameFinished(boolean gameFinished) {
		this.gameFinished = gameFinished;
	}
}