package uk.digitalsquid.remme.mgr.db;

import uk.digitalsquid.remme.App;
import uk.digitalsquid.remme.misc.Config;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * DB access methods. Methods are split up into separate classes to make codebase easier to manage.
 * @author william
 *
 */
public final class DB extends SQLiteOpenHelper {
	static final int DB_VERSION = 1;
	static final String DB_NAME = "db";
	
	public final DBGroup groups;
	public final DBProgress progress;
	public final DBHidden hidden;

	public DB(App app) {
		super(app.getApplicationContext(), DB_NAME, null, DB_VERSION);
		groups = new DBGroup();
		progress = new DBProgress(app);
		hidden = new DBHidden(app);
		groups.setDb(getWritableDatabase());
		progress.setDb(getWritableDatabase());
		hidden.setDb(getWritableDatabase());
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		groups.onCreate(db);
		progress.onCreate(db);
		hidden.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
	
	/**
	 * Base class for DB managing classes.
	 * @author william
	 *
	 */
	public static abstract class DBSubclass implements Config {
		protected SQLiteDatabase db;
		
		public void setDb(SQLiteDatabase db) {
			this.db = db;
		}
		
		public DBSubclass() {
		}
		
		/**
		 * Creates this subclass's tables. Use the given DB, rather than the stored one, which won't have been set yet.
		 * @param db
		 */
		abstract void onCreate(SQLiteDatabase db);
	}
}
