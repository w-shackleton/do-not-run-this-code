package uk.digitalsquid.spacegame;

import uk.digitalsquid.spacegame.levels.LevelManager;
import android.app.Application;

/**
 * Stores globals
 * @author william
 *
 */
public class App extends Application {
	
	private LevelManager levelManager;

	public LevelManager getLevelManager() {
		if(levelManager == null) levelManager = new LevelManager(getApplicationContext());
		return levelManager;
	}
}
