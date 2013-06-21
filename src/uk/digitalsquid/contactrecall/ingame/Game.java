package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.ingame.games.GameAdapter;
import uk.digitalsquid.contactrecall.ingame.games.PhotoNameGame;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterViewFlipper;
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 *
 */
public class Game extends Activity implements Config {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	GameDescriptor gameDescriptor;
	
	boolean gamePaused = false;
	
	View pauseLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.game);
		
		if(savedInstanceState == null) {
			GameFragment gameFragment = new GameFragment();
			
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
	
	public static class GameFragment extends Fragment {
		App app;
		
		GameDescriptor gameDescriptor;
		GameAdapter gameAdapter;
		AdapterViewFlipper viewFlipper;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Bundle args = getArguments();
			if(args == null) return;
			gameDescriptor = args.getParcelable("gameDescriptor");
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.game_fragment, container, false);
			
			viewFlipper = (AdapterViewFlipper) rootView.findViewById(R.id.pager);
			
			return rootView;
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			app = (App)getActivity().getApplication();
			
			gameAdapter = getGameAdapter(gameDescriptor);
			viewFlipper.setAdapter(gameAdapter);
		}
		
		/**
		 * Generates a {@link GameAdapter}
		 * @param descriptor
		 * @return
		 */
		GameAdapter getGameAdapter(GameDescriptor descriptor) {
			switch(descriptor.getType()) {
			case GameDescriptor.GAME_PHOTO_TO_NAME:
				return new PhotoNameGame(getActivity(), app, descriptor);
			default:
				return null;
			}
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
}
