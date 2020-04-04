package uk.digitalsquid.spacegame.levels;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.StaticInfo;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Manages, finds and loads info for all levels within the game.
 * 
 * @author william
 * 
 */
public class LevelManager implements Constants {
	final DbStorage db;
	final Context context;
	final AssetManager am;

	public LevelManager(Context context)
	{
		db = new DbStorage(context);
		this.context = context;
		am = context.getAssets();
	}

	public void initialise()
	{
		try
		{
			privateInitialise();
		} catch (IOException e)
		{
			e.printStackTrace();
			Log.e(TAG, "Error finding levels: " + e.getMessage());
		} catch (NotFoundException e)
		{
			e.printStackTrace();
			Log.e(TAG, "Error loading level from resources: "
					+ e.getMessage());
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			Log.e(TAG, "IllegalArgumentException: " + e.getMessage());
		} catch (SAXException e)
		{
			e.printStackTrace();
			Log.e(TAG, "Error loading XML Data: " + e.getMessage());
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
			Log.e(TAG, "IllegalAccessException: " + e.getMessage());
		}
	}

	public static final String BUILTIN_PREFIX = "_def_";

	protected void privateInitialise() throws IOException, NotFoundException,
			IllegalArgumentException, SAXException, IllegalAccessException
	{
		LevelInfo lInfo = new LevelInfo();
		LevelSetInfo lSetInfo = new LevelSetInfo();
		
		String[] levelSets = am.list("lvl");
		
		for(String levelSet : levelSets) {
			Log.v(TAG, "Found levelset at " + levelSet);
			
			lSetInfo.filename = BUILTIN_PREFIX + levelSet;
			lSetInfo.author = "";
			lSetInfo.name = "";
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(am.open("lvl/" + levelSet + "/info.lvlset")));
				String line;
				while((line = br.readLine()) != null) {
					String[] lineparts = line.split(":");
					if(lineparts.length < 2) continue;
					if(lineparts[0].equalsIgnoreCase("setname"))
						lSetInfo.name = lineparts[1];
					if(lineparts[0].equalsIgnoreCase("authour"))
						lSetInfo.author = lineparts[1];
				}
			} catch(IOException e) {
				Log.i(TAG, "Error processing levelset in " + levelSet);
				break;
			}
			if(db.checkLevelSetNotExists(lSetInfo.filename))
			{
				Log.v(TAG, "Levelset " + lInfo.set
						+ " doesn't exist in DB, creating...");
				db.insertLevelSetInfo(lSetInfo);
			}
			
			// Done set, doing levels...
			
			String[] levelFileNames = am.list("lvl/" + levelSet);
			for(String levelFileName : levelFileNames) {
				if(!levelFileName.endsWith(".slv")) continue;
				lInfo.set = lSetInfo.filename;
				lInfo.filename = levelFileName.substring(0, levelFileName.lastIndexOf("."));
				
				// Split levelname
				try {
					lInfo.fileNumber = Integer.valueOf(lInfo.filename.substring(lInfo.filename.lastIndexOf(" ") + 1));
				} catch (NumberFormatException e) {
					continue;
				}
				lInfo.filename = lInfo.filename.substring(0, lInfo.filename.lastIndexOf(" "));
				
				Log.i(TAG, "Checking level " + lInfo.filename + " in level set " + lInfo.set);
				
				if(db.checkLevelNotExists(lInfo.filename, lInfo.fileNumber, lInfo.set))
				{
					Log.v(TAG, "Level " + lInfo.filename
							+ " doesn't exist in DB, creating...");
					SaxInfoLoader.LevelInfo info = SaxInfoLoader
							.parse(CompuFuncs.decodeIStream(am.open("lvl/" + levelSet + "/" + lInfo.filename + " " + lInfo.fileNumber + ".slv")));
					
					lInfo.author = info.getAuthor();
					lInfo.name = info.getName();
					db.insertLevelInfo(lInfo);
				}
			}
		}

		Log.i(TAG,
				"Checking for items in database which aren't available...");
		db.checkDatabaseValidity(am);

		db.getWritableDatabase().close();
		Log.i(TAG, "Finished updating database");
	}
	
	void displayFiles (AssetManager mgr, String path) {
	    try {
	        String list[] = mgr.list(path);
	        if (list != null && list.length != 0)
	        {
	            for (int i=0; i<list.length; ++i)
                {
                    Log.v(TAG, path + "/" + list[i]);
                    displayFiles(mgr, path + "/" + list[i]);
                }
            }
	        else {
		    	Log.v(TAG, path + " is a file");
	        }
	    } catch (IOException e) {
	    }

	}

	public ArrayList<LevelExtendedInfo> getLevelsFromSet(String set)
	{
		return db.getLevelsFromSet(set);
	}

	public InputStream getLevelIStream(LevelInfo info) throws IOException {
		if(info.set.startsWith(BUILTIN_PREFIX)) {
			String setFilePath = info.set.replace(BUILTIN_PREFIX, ""); // Remove it
			Log.v(TAG, "Opening level at path " + "lvl/" + setFilePath + "/" + info.filename + " " + info.fileNumber + ".slv");
			return am.open("lvl/" + setFilePath + "/" + info.filename + " " + info.fileNumber + ".slv");
		}
		return null;
	}
	
	public void setLevelTime(LevelExtendedInfo level, int milliTime) {
		db.setLevelTime(level, milliTime);
	}
	
	public void resetDB() {
		db.resetDB();
	}

	private class DbStorage extends SQLiteOpenHelper
	{
		private static final String KEY_ID = "id";
		private static final String KEY_FROMSET = "fromset";
		private static final String KEY_NAME = "name";
		private static final String KEY_LEVEL_NUMBER = "number";
		private static final String KEY_AUTHOR = "author";
		/**
		 * Foldername of thing
		 */
		private static final String KEY_FILENAME = "file";
		/**
		 * Time to complete in millis
		 */
		private static final String KEY_TIME = "time";

		private static final int DB_VERSION = 1;
		private static final String DB_LEVELS_NAME = "levels";
		private static final String DB_LEVELS_CREATE = "CREATE TABLE "
				+ DB_LEVELS_NAME + " (" + KEY_ID
				+ " INTEGER PRIMARY KEY NOT NULL, " + KEY_FROMSET + " TEXT, "
				+ KEY_NAME + " TEXT, " + KEY_LEVEL_NUMBER + " INTEGER, " + KEY_AUTHOR + " TEXT, " + KEY_FILENAME
				+ " TEXT NOT NULL, " + KEY_TIME + " INTEGER DEFAULT -1);";
		private static final String DB_SETS_NAME = "sets";
		private static final String DB_SETS_CREATE = "CREATE TABLE "
				+ DB_SETS_NAME + " (" + KEY_NAME + " TEXT, " + KEY_AUTHOR
				+ " TEXT, " + KEY_FILENAME + " TEXT);";

		public DbStorage(Context context)
		{
			super(context, DB_LEVELS_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL(DB_LEVELS_CREATE);
				db.execSQL(DB_SETS_CREATE);
			} catch (SQLException e)
			{
				Log.e(TAG, "SQL Error: " + e.getMessage());
			}
			Log.i(TAG, "Created database");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
		}

		private boolean checkLevelSetNotExists(String levelsetName)
		{
			Cursor c = getReadableDatabase().query(DB_SETS_NAME,
					new String[] { KEY_FILENAME }, KEY_FILENAME + " = ?",
					new String[] { levelsetName }, null, null, null);
			boolean ret = c.getCount() == 0;
			c.close();
			return ret;
		}

		private boolean checkLevelNotExists(String levelfilename, int levelNumber, String levelset)
		{
			Cursor c = getReadableDatabase().query(DB_LEVELS_NAME,
					new String[] { KEY_NAME },
					"(" + KEY_FILENAME + " = ?) AND (" + KEY_FROMSET + " = ?) AND (" + KEY_LEVEL_NUMBER + " = ?)",
					new String[] { levelfilename, levelset, "" + levelNumber }, null, null, null);
			boolean ret = c.getCount() == 0;
			c.close();
			return ret;
		}

		private void insertLevelInfo(LevelInfo info)
		{
			ContentValues vals = new ContentValues();
			vals.put(KEY_FROMSET, info.set);
			vals.put(KEY_NAME, info.name);
			vals.put(KEY_LEVEL_NUMBER, info.fileNumber);
			vals.put(KEY_AUTHOR, info.author);
			vals.put(KEY_FILENAME, info.filename);
			getWritableDatabase().insert(DB_LEVELS_NAME, "", vals);
		}

		private void insertLevelSetInfo(LevelSetInfo info)
		{
			ContentValues vals = new ContentValues();
			vals.put(KEY_NAME, info.name);
			vals.put(KEY_AUTHOR, info.author);
			vals.put(KEY_FILENAME, info.filename);
			getWritableDatabase().insert(DB_SETS_NAME, "", vals);
		}

		private void checkDatabaseValidity(AssetManager am) throws IOException
		{
			Cursor levels = getReadableDatabase().query(DB_LEVELS_NAME,
					new String[] { KEY_FILENAME, KEY_FROMSET, KEY_LEVEL_NUMBER }, null, null,
					null, null, null);
			while (levels.moveToNext())
			{
				String set = levels.getString(levels.getColumnIndex(KEY_FROMSET));
				String filename = levels.getString(levels.getColumnIndex(KEY_FILENAME));
				int fileNumber = levels.getInt(levels.getColumnIndex(KEY_LEVEL_NUMBER));
				
				if(set.startsWith(BUILTIN_PREFIX)) {
					// Only this currently supported
					String properSet = set.replaceAll(BUILTIN_PREFIX, "");
					try {
						am.open("lvl/" + properSet + "/" + filename + " " + fileNumber + ".slv").close();
					} catch(IOException e) {
						deleteLevel(filename, fileNumber, set);
					}
				}
			}
			levels.close();
			
			// Level sets
			Cursor levelSets = getWritableDatabase().query(DB_SETS_NAME,
					new String[] { KEY_FILENAME }, null, null,
					null, null, null);
			while (levelSets.moveToNext())
			{
				String foldername = levelSets.getString(levelSets.getColumnIndex(KEY_FILENAME));
				String[] files = am.list("lvl/" + foldername.replace(BUILTIN_PREFIX, ""));
				boolean isEmpty = true;
				for(String file : files) {
					if(file.endsWith(".slv")) isEmpty = false;
				}
				if(isEmpty) {
					Log.i(TAG, "Deleting level set " + foldername);
					deleteLevelSet(foldername);
				}
			}
			levelSets.close();
		}

		private void deleteLevel(String filename, int fileNumber, String set)
		{
			getWritableDatabase()
					.delete(
							DB_LEVELS_NAME,
							"(" + KEY_FILENAME + " = ?) AND " + "("
									+ KEY_FROMSET + " = ?) AND (" + KEY_LEVEL_NUMBER + " = ?)",
							new String[] { filename,
									set, "" + fileNumber});
		}

		private void deleteLevelSet(String set)
		{
			getWritableDatabase().delete(DB_SETS_NAME, KEY_FILENAME + " = ?",
					new String[] { set });
		}

		private ArrayList<LevelExtendedInfo> getLevelsFromSet(String set)
		{
			ArrayList<LevelExtendedInfo> items = new ArrayList<LevelExtendedInfo>();

			Cursor c = null;
			try
			{
				c = getReadableDatabase().query(
						DB_LEVELS_NAME,
						new String[] { KEY_FROMSET, KEY_NAME, KEY_LEVEL_NUMBER, KEY_AUTHOR,
								KEY_FILENAME, KEY_TIME }, KEY_FROMSET + " = ?",
						new String[] { set }, null, null, KEY_FILENAME);
				int idFromset = c.getColumnIndex(KEY_FROMSET);
				int idName = c.getColumnIndex(KEY_NAME);
				int idFileNumber = c.getColumnIndex(KEY_LEVEL_NUMBER);
				int idAuthor = c.getColumnIndex(KEY_AUTHOR);
				int idFilename = c.getColumnIndex(KEY_FILENAME);
				int idTime = c.getColumnIndex(KEY_TIME);
				
				String prevFileName = "\\\42/3gremkjrif3jvf I lost the game"; // Random string
				boolean playable = true;
				while (c.moveToNext())
				{
					String fileName = c.getString(idFilename);
					boolean completed = c.getInt(idTime) >= 0;
					if(!prevFileName.equals(fileName)) {
						playable = true;
						prevFileName = fileName;
					}
					items.add(new LevelExtendedInfo(c.getString(idName), c.getInt(idFileNumber), c
							.getString(idFromset), c.getString(idAuthor),
							fileName, c.getInt(idTime), completed, playable));
					playable = completed;
				}
			} catch (SQLiteException e)
			{
				Log.e(TAG, "SQLite Database Error: "
						+ e.getLocalizedMessage());
			} finally {
				if(c != null) c.close();
			}

			return items;
		}
		
		protected void setLevelTime(LevelExtendedInfo level, int milliTime) {
			ContentValues vals = new ContentValues();
			vals.put(KEY_TIME, milliTime);
			getWritableDatabase().update(DB_LEVELS_NAME, vals, 
						"(" + KEY_FILENAME + " = ?) AND " + "("
							+ KEY_FROMSET + " = ?) AND (" + KEY_LEVEL_NUMBER + " = ?)",
						new String[] {level.filename, level.set, "" + level.fileNumber});
		}
		
		protected void resetDB()
		{
			Log.i(TAG, "Deleting all data from DB...");
			getWritableDatabase().delete(DB_LEVELS_NAME, null, null);
			getWritableDatabase().delete(DB_SETS_NAME, null, null);
			getWritableDatabase().close();
		}
	}

	protected static class LevelBaseInfo implements Serializable {
		private static final long serialVersionUID = 6665358257178911099L;
		
		public String name, set = null;
		public int fileNumber;

		public LevelBaseInfo(String name, int fileNumber, String set)
		{
			this.name = name;
			this.fileNumber = fileNumber;
			this.set = set;
		}
	}

	public static class LevelInfo extends LevelBaseInfo {
		private static final long serialVersionUID = 3664640337409898860L;
		
		public String author, filename;

		public LevelInfo(String name, int fileNumber, String set, String author, String filename)
		{
			super(name, fileNumber, set);
			this.author = author;
			this.filename = filename;
		}

		public LevelInfo()
		{
			super(null, 0, null);
		}
	}

	public static final class LevelExtendedInfo extends LevelInfo {
		private static final long serialVersionUID = -866101939292856696L;
		
		public int time;
		private boolean completed;

		private boolean playable;

		public LevelExtendedInfo(String name, int fileNumber, String set, String author,
				String filename, int time, boolean completed, boolean playable)
		{
			super(name, fileNumber, set, author, filename);
			this.time = time;
			this.setCompleted(completed);
			this.setPlayable(playable);
		}

		public void setPlayable(boolean playable) {
			this.playable = playable;
		}

		public boolean isPlayable() {
			return playable || StaticInfo.DEBUG; // Play all when debugging
		}

		public void setCompleted(boolean completed) {
			this.completed = completed;
		}

		public boolean isCompleted() {
			return completed;
		}
	}

	protected static final class LevelSetInfo implements Serializable {
		private static final long serialVersionUID = 7551210763843853987L;
		
		public String name, author, filename;

		public LevelSetInfo(String name, String author, String filename)
		{
			this.name = name;
			this.author = author;
			this.filename = filename;
		}

		public LevelSetInfo()
		{

		}
	}
}
