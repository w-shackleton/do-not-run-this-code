package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.ingame.games.GameAdapter;
import uk.digitalsquid.contactrecall.ingame.games.PhotoNameGame;
import uk.digitalsquid.contactrecall.misc.Config;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ViewAnimator;

/**
 * The actual game itself.
 * @author william
 *
 */
public class Game extends FragmentActivity implements OnClickListener, Config {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	App app;
	GameAdapter pagerAdapter;
	GameDescriptor gameDescriptor;
	ViewPager viewPager;
	
	ViewAnimator pauseSelectionAnimator;
	
	View pauseLayout;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App)getApplication();
		
		setContentView(R.layout.game);
		
		pauseSelectionAnimator = (ViewAnimator) findViewById(R.id.pauseSelectionAnimator);
		pauseSelectionAnimator.setInAnimation(this, R.animator.pause_flip_in);
		pauseSelectionAnimator.setOutAnimation(this, R.animator.pause_flip_out);
		
		pauseLayout = findViewById(R.id.pausedScreen);
		pauseLayout.setVisibility(View.INVISIBLE);
		
		// Buttons
		findViewById(R.id.resume).setOnClickListener(this);
		findViewById(R.id.leave).setOnClickListener(this);
		
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
		
		pagerAdapter = getGameAdapter(gameDescriptor);
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(pagerAdapter);
	}
	
	/**
	 * Generates a {@link GameAdapter}
	 * @param descriptor
	 * @return
	 */
	GameAdapter getGameAdapter(GameDescriptor descriptor) {
		switch(descriptor.getType()) {
		case GameDescriptor.GAME_PHOTO_TO_NAME:
			return new PhotoNameGame(getSupportFragmentManager(), app, descriptor);
		default:
			return null;
		}
	}
	
	@Override
	public void onBackPressed() {
		// Don't call super, which would finish() the activity
		pauseSelectionAnimator.showNext();
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.resume:
			pauseSelectionAnimator.setDisplayedChild(0);
			break;
		case R.id.leave:
			// TODO: perform game status save and cleanup here!
			finish();
			break;
		}
	}
}
