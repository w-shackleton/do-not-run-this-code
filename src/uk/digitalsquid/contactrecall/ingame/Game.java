package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.game.GameInstance;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 *
 */
public class Game extends Activity {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	private App app;
	
	GameInstance gameInstance;
	GameDescriptor gameDesc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		if(savedInstanceState != null && savedInstanceState.getBoolean("gameStarted", false)) {
			gameDesc = savedInstanceState.getParcelable("gameDesc");
			gameInstance = app.getCurrentGame();
		} else {
			Bundle e = getIntent().getExtras();
			gameDesc = getIntent().getExtras().getParcelable(GAME_DESRIPTOR);
			if(gameDesc == null) {
				Toast.makeText(this, "Game settings not found!", Toast.LENGTH_LONG).show();
				finish();
			} else {
				gameInstance = gameDesc.createGameInstance(app);
			}
		}
	}
	
	protected void onSaveInstanceState(Bundle out) {
		out.putParcelable("gameDesc", gameDesc);
		out.putBoolean("gameStarted", true);
	}
}
