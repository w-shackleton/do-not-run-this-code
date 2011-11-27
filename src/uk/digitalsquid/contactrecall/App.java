package uk.digitalsquid.contactrecall;

import uk.digitalsquid.contactrecall.game.GameInstance;
import uk.digitalsquid.contactrecall.mgr.ContactManager;
import uk.digitalsquid.contactrecall.mgr.GameDataManager;
import uk.digitalsquid.contactrecall.mgr.GroupManager;
import uk.digitalsquid.contactrecall.mgr.PhotoManager;
import uk.digitalsquid.contactrecall.mgr.db.DB;
import android.app.Application;

/**
 * Base {@link Application}, which holds instances to various managers.
 * @author william
 *
 */
public class App extends Application {
	private DB db;
	
	private GroupManager groups;
	private ContactManager contacts;
	private PhotoManager photos;
	private GameDataManager game;
	
	private GameInstance currentGame;

	public DB getDb() {
		if(db == null) db = new DB(this);
		return db;
	}

	public GroupManager getGroups() {
		if(groups == null) groups = new GroupManager(this, getDb());
		return groups;
	}

	public ContactManager getContacts() {
		if(contacts == null) contacts = new ContactManager(getApplicationContext(), this);
		return contacts;
	}

	public PhotoManager getPhotos() {
		if(photos == null) photos = new PhotoManager(getApplicationContext(), getDb());
		return photos;
	}

	public GameDataManager getGame() {
		if(game == null) game = new GameDataManager(getApplicationContext(), this);
		return game;
	}
	
	/**
	 * Returns <code>true</code> when there is a current game stored here. Doesn't actually indicate that the game is being played however.
	 * @return
	 */
	public boolean isGameActive() {
		return currentGame != null;
	}

	public GameInstance getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(GameInstance currentGame) {
		this.currentGame = currentGame;
	}
}
