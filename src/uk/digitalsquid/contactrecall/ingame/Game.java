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
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 *
 */
public class Game extends FragmentActivity implements Config {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	App app;
	GameAdapter pagerAdapter;
	GameDescriptor gameDescriptor;
	ViewPager viewPager;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App)getApplication();
		
		setContentView(R.layout.game);
		
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
			return new PhotoNameGame(getSupportFragmentManager(), app);
		default:
			return null;
		}
	}
}
