package uk.digitalsquid.contactrecall.ingame;

import java.util.ArrayList;

import uk.digitalsquid.contactrecall.GameDescriptor;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.mgr.details.Contact;
import uk.digitalsquid.contactrecall.mgr.details.DataItem;
import uk.digitalsquid.contactrecall.misc.Config;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	GameDescriptor gameDescriptor;
	
	private boolean gamePaused = false;
	private boolean gameRunning = true;
	
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
			
			gamePaused = savedInstanceState.getBoolean("gamePaused");
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("gamePaused", gamePaused);
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
	public void choiceMade(Contact choice, boolean correct, boolean timeout, float timeTaken) {
		callbacks.choiceMade(choice, correct, timeout, timeTaken);
	}
	@Override
	public void pairingChoiceMade(ArrayList<Contact> correct,
			ArrayList<Pair<Contact, Contact>> incorrect, ArrayList<Contact> timeout,
			float timeTaken) {
		callbacks.pairingChoiceMade(correct, incorrect, timeout, timeTaken);
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
}
