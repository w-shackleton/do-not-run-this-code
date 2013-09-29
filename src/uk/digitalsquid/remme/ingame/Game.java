package uk.digitalsquid.remme.ingame;

import java.util.ArrayList;

import uk.digitalsquid.remme.GameDescriptor;
import uk.digitalsquid.remme.R;
import uk.digitalsquid.remme.mgr.details.Contact;
import uk.digitalsquid.remme.mgr.details.DataItem;
import uk.digitalsquid.remme.misc.Config;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 * TODO: Currently, the view hierarchy is too complicated.
 * Container contains the pager, pause and summary screens.
 * The pager contains the cards. It will probably work also
 * if the container contains all these things. Might be worth doing.
 */
public class Game extends Activity implements GameCallbacks, Config {
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.remme.gameInstance";
	
	GameDescriptor gameDescriptor;
	
	private boolean gamePaused = false;
	private boolean gameRunning = true;
	
	private GestureDetector gestureDetector;
	
	/**
	 * This class implements {@link GameCallbacks} -
	 * this implementation proxies to the given callbacks.
	 */
	GameCallbacks callbacks;
	
	View pauseLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.game);
		
		gestureDetector = new GestureDetector(this, new DiscardGestureListener());
		
		if(savedInstanceState == null) {
			GameFragment gameFragment = new GameFragment();
			callbacks = gameFragment;
			
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
					add(R.id.container, gameFragment, "gameFragment").
					commit();
		} else {
			
			Fragment fragment = getFragmentManager().findFragmentByTag("gameFragment");
			if(fragment instanceof GameFragment)
				callbacks = (GameCallbacks) fragment;
			
			gamePaused = savedInstanceState.getBoolean("gamePaused");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("gamePaused", gamePaused);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	void resumeGame() {
		if(isGamePaused()) {
			// Let handler pop pause screen from stack
			setGamePaused(false); // TODO: Move this to a point where the game is
			// fully visible
			super.onBackPressed();
		}
	}
	
	@Override
	public void onBackPressed() {
		if(getFragmentManager().getBackStackEntryCount() > 0) {
			// Let handler pop pause screen from stack
			if(getFragmentManager().getBackStackEntryCount() == 1)
				setGamePaused(false); // TODO: Move this to a point where the game is
			// fully visible
			super.onBackPressed();
		} else if(isGameRunning()) {
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.setCustomAnimations(
					R.animator.pause_flip_in,
					R.animator.pause_flip_out,
					R.animator.pause_pop_flip_in,
					R.animator.pause_pop_flip_out);
			transaction.replace(R.id.container, new GamePauseFragment());
			transaction.addToBackStack(null);
			transaction.commit();
			setGamePaused(true);
		} else finish();
	}
	
	@Override
	public void pauseGame() {
		if(getFragmentManager().getBackStackEntryCount() == 0 &&
				isGameRunning())
			onBackPressed();
	}
	
	@Override
	public void choiceMade(Contact choice, int choiceType, float timeTaken, int pointsGain) {
		callbacks.choiceMade(choice, choiceType, timeTaken, pointsGain);
	}
	@Override
	public void pairingChoiceMade(ArrayList<Contact> correct,
			ArrayList<Pair<Contact, Contact>> incorrect, ArrayList<Contact> timeout,
			float timeTaken, int pointsGain) {
		callbacks.pairingChoiceMade(correct, incorrect, timeout, timeTaken, pointsGain);
	}

	boolean isGamePaused() {
		return gamePaused;
	}

	@Override
	public void setGamePaused(boolean gamePaused) {
		this.gamePaused = gamePaused;
		callbacks.setGamePaused(gamePaused);
	}

	boolean isGameRunning() {
		return gameRunning;
	}

	void setGameFinished() {
		this.gameRunning = false;
	}

	/**
	 * Show data error screen
	 */
	@Override
	public void dataErrorFound(ArrayList<DataItem> possibleErrors) {
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		DataErrorFragment fragment = new DataErrorFragment();
		Bundle args = new Bundle();
		args.putParcelableArrayList("possibleErrors", possibleErrors);
		fragment.setArguments(args);
		// TODO: Different animations
		transaction.setCustomAnimations(
				R.animator.pause_flip_in,
				R.animator.pause_flip_out,
				R.animator.pause_pop_flip_in,
				R.animator.pause_pop_flip_out);
		transaction.replace(R.id.container, fragment);
		// User can press back to get back
		transaction.addToBackStack(null);
		transaction.commit();
		setGamePaused(true);
	}
	
	private class DiscardGestureListener
			extends GestureDetector.SimpleOnGestureListener implements Config {
		
		@Override
		public boolean onDown(MotionEvent event) {
			return true;
		}
	
		private static final int MAX_OFF_PATH = 250;
		private static final int MIN_DISTANCE = 120;
		private static final int THRESHOLD_VELOCITY = 200;
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(Math.abs(e1.getX() - e2.getX()) > MAX_OFF_PATH)
				return false;
			if(Math.abs(velocityY) < THRESHOLD_VELOCITY)
				return false;
			if(e1.getY() - e2.getY() > MIN_DISTANCE) {
				// Swipe up happened
				Log.i(TAG, "User swiped up on question");
				Log.d(TAG, String.format("   - %f off path, %f velocity, dist %f",
						Math.abs(e1.getX() - e2.getX()),
						velocityY,
						e1.getY() - e2.getY()));
				
				// No net points change for discarding
				choiceMade(null, CHOICE_DISCARD, 0, 0);
						
				return true;
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}
