package uk.digitalsquid.contactrecall.ingame;

import uk.digitalsquid.contactrecall.App;
import uk.digitalsquid.contactrecall.R;
import uk.digitalsquid.contactrecall.game.GameDescriptor;
import uk.digitalsquid.contactrecall.game.GameInstance;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * The actual game itself.
 * @author william
 *
 */
public class Game extends Activity implements GameCallbacks, OnClickListener {
	
	public static final String GAME_DESRIPTOR = "uk.digitalsquid.contactrecall.gameInstance";
	
	private App app;
	
	GameInstance gameInstance;
	GameDescriptor gameDesc;
	
	private GameView view;
	
	private ViewGroup pauseMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);
		view = (GameView) findViewById(R.id.gameView);
		view.setGameCallbacks(this);
		pauseMenu = (ViewGroup) findViewById(R.id.pauseGameView);
		pauseMenu.setVisibility(View.GONE);
		
		findViewById(R.id.resume).setOnClickListener(this);
		findViewById(R.id.leave).setOnClickListener(this);
		
		app = (App) getApplication();
		if(savedInstanceState != null && savedInstanceState.getBoolean("gameStarted", false)) {
			gameDesc = savedInstanceState.getParcelable("gameDesc");
			gameInstance = app.getCurrentGame();
			if(gameInstance == null) {
				gameInstance = gameDesc.createGameInstance(app);
				app.setCurrentGame(gameInstance);
			}
		} else {
			gameDesc = getIntent().getExtras().getParcelable(GAME_DESRIPTOR);
			if(gameDesc == null) {
				Toast.makeText(this, "Game settings not found!", Toast.LENGTH_LONG).show();
				finish();
			} else {
				gameInstance = gameDesc.createGameInstance(app);
				app.setCurrentGame(gameInstance);
			}
		}
		
		view.setGame(gameInstance);
		
		// If this is the first time playing
		if(savedInstanceState == null || !savedInstanceState.getBoolean("gameStarted", false)) {
			// Start immediately
			view.resume();
		}
		
		if(savedInstanceState != null) view.restoreState(savedInstanceState);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		view.pause();
		view.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		view.onResume();
	}
	
	protected void onSaveInstanceState(Bundle out) {
		out.putParcelable("gameDesc", gameDesc);
		out.putBoolean("gameStarted", true);
		view.saveState(out);
	}

	@Override
	public void onGamePaused() {
		pauseMenu.setVisibility(View.VISIBLE);
	}

	@Override
	public void onGameResumed() {
		pauseMenu.setVisibility(View.GONE);
	}

	@Override
	public void onGameCancelled() {
		finish();
	}
	
	@Override
	public void onBackPressed() {
		if(view.isRunning())
			view.pause();
		else
			view.resume();
	}

	/**
	 * Button presses.
	 */
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.resume:
			view.resume();
			break;
		case R.id.leave:
			view.cancelGame();
			break;
		}
	}
}
