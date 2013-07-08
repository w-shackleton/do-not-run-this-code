package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.ingame.games.GameAdapter;
import uk.digitalsquid.contactrecall.ingame.games.PhotoNameGame;
import uk.digitalsquid.contactrecall.mgr.Contact;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 *
 */
public class Game extends Activity implements GameCallbacks, Config {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	GameDescriptor gameDescriptor;
	
	boolean gamePaused = false;
	
	/**
	 * This class implements {@link GameCallbacks} -
	 * this implementation proxies to the given callbacks.
	 */
	GameCallbacks callbacks;
	
	View pauseLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.game);
		
		if(savedInstanceState == null) {
			GameFragment gameFragment = new GameFragment();
			callbacks = gameFragment;
			
			// TODO: Callbacks won't be set when there is a savedInstanceState
			
			Bundle fragmentArgs = new Bundle();
			
			// Get game descriptor
			try {
				gameDescriptor = getIntent().getParcelableExtra(GAME_DESRIPTOR);
			} catch(Exception e) { // Who knows
				Log.e(TAG, "Failed to get game descriptor", e);
			}
			if(gameDescriptor == null) {
				Toast.makeText(this,  "Failed to load game", Toast.LENGTH_LONG).show();
				finish();
				return;
			}
			
			fragmentArgs.putParcelable("gameDescriptor", gameDescriptor);
			
			gameFragment.setArguments(fragmentArgs);
			
			getFragmentManager().
					beginTransaction().
					add(R.id.container, gameFragment).
					commit();
		} else {
			GameFragment gameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.container);
			callbacks = gameFragment;
		}
	}
	
	private void resumeGame() {
		if(gamePaused) {
			// Let handler pop pause screen from stack
			gamePaused = false; // TODO: Move this to a point where the game is
			// fully visible
			super.onBackPressed();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(gamePaused) {
			// Let handler pop pause screen from stack
			gamePaused = false; // TODO: Move this to a point where the game is
			// fully visible
			super.onBackPressed();
		} else {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(
					R.animator.pause_flip_in,
					R.animator.pause_flip_out,
					R.animator.pause_pop_flip_in,
					R.animator.pause_pop_flip_out);
			transaction.replace(R.id.container, new GamePauseFragment());
			transaction.addToBackStack(null);
			transaction.commit();
			gamePaused = true;
		}
	}
	
	public static class GameFragment extends Fragment implements GameCallbacks {
		App app;
		
		GameDescriptor gameDescriptor;
		GameAdapter gameAdapter;
		FrameLayout frameLayout;
		Handler handler = new Handler();
		
		int position = 0;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			if(args == null) return;
			gameDescriptor = args.getParcelable("gameDescriptor");
			
			if(savedInstanceState != null)
				position = savedInstanceState.getInt("position");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			View rootView = inflater.inflate(R.layout.game_fragment, container, false);
			
			frameLayout = (FrameLayout) rootView.findViewById(R.id.pager);
			
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
			
			getFragmentManager().beginTransaction().
				replace(R.id.pager, gameAdapter.getFragment(position)).commit();
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
				switch(descriptor.getType()) {
				case GameDescriptor.GAME_PHOTO_TO_NAME:
					return new PhotoNameGame(getActivity(), app, descriptor, this);
				default:
					return null;
				}
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
			}
		}

		@Override
		public void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken) {
			position++;
			if(position == gameAdapter.getCount()) {
				// TODO: End-case
			} else {
				// Run transition to next card after the given delay.
				// The position has already been updated so we shouldn't have
				// to worry about view rotation etc.
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						getFragmentManager().beginTransaction().
							setCustomAnimations(R.animator.next_card_in, R.animator.next_card_out).
							replace(R.id.pager, gameAdapter.getFragment(position)).
							commit();
					}
				}, getResources().getInteger(
						correct ?
								R.integer.page_correct_wait_time :
								R.integer.page_incorrect_wait_time));
			}
			
			// TODO: Background this?
			if(timeout) app.getDb().progress.addTimeout(
					gameAdapter.getItem(position-1).getContact(), timeTaken);
			else if(correct) app.getDb().progress.addSuccess(
					gameAdapter.getItem(position-1).getContact(), timeTaken);
			else app.getDb().progress.addFail(
					gameAdapter.getItem(position-1).getContact(), choice, timeTaken);
		}
	}
	
	public static class GamePauseFragment extends Fragment implements OnClickListener {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.game_pause_fragment, container, false);
			
			// Buttons
			rootView.findViewById(R.id.resume).setOnClickListener(this);
			rootView.findViewById(R.id.leave).setOnClickListener(this);
			
			return rootView;
		}

		@Override
		public void onClick(View v) {
			Game activity = (Game)getActivity();
			switch(v.getId()) {
			case R.id.resume:
				if(activity != null) activity.resumeGame();
				break;
			case R.id.leave:
				// TODO: perform game status save and cleanup here!
				if(activity != null) activity.finish();
				break;
			}
		}
	}

	@Override
	public void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken) {
		callbacks.choiceMade(choice, correct, timeout, timeTaken);
	}
}
