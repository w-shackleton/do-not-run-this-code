package uk.digitalsquid.spacegame.levels;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.content.ContentValues;
import android.content.Context;
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
public class LevelManager
{
	DbStorage db;
	Context context;

	public LevelManager(Context context)
	{
		db = new DbStorage(context);
		this.context = context;
	}

	public void initialise()
	{
		try
		{
			privateInitialise();
		} catch (IOException e)
		{
			e.printStackTrace();
			Log.e("SpaceGame", "Error finding levels: " + e.getMessage());
		} catch (NotFoundException e)
		{
			e.printStackTrace();
			Log.e("SpaceGame", "Error loading level from resources: "
					+ e.getMessage());
		} catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			Log.e("SpaceGame", "IllegalArgumentException: " + e.getMessage());
		} catch (SAXException e)
		{
			e.printStackTrace();
			Log.e("SpaceGame", "Error loading XML Data: " + e.getMessage());
		} catch (IllegalAccessException e)
		{
			e.printStackTrace();
			Log.e("SpaceGame", "IllegalAccessException: " + e.getMessage());
		}
	}

	public static final String BUILTIN_PREFIX = "_sd_";

	protected void privateInitialise() throws IOException, NotFoundException,
			IllegalArgumentException, SAXException, IllegalAccessException
	{
		Field[] fields = R.raw.class.getFields();

		String resFilename;

		LevelInfo lInfo = new LevelInfo();
		LevelSetInfo lSetInfo = new LevelSetInfo();

		for (Field field : fields)
		{
			if(field.getName().startsWith("lvl_"))
			{
				resFilename = field.getName();
				lInfo.set = BUILTIN_PREFIX
						+ resFilename.substring(resFilename.indexOf('_') + 1,
								resFilename.lastIndexOf('_'));
				lInfo.filename = resFilename.substring(resFilename
						.lastIndexOf('_') + 1);
				Log.d("SpaceGame", "Checking level '" + lInfo.filename
						+ "' from set '" + lInfo.set + "'.");
				if(db.CheckLevelExists(lInfo.filename, lInfo.set))
				{
					Log.v("SpaceGame", "Level " + lInfo.filename
							+ " doesn't exist in DB, creating...");
					SaxInfoLoader.LevelInfo info = SaxInfoLoader
							.parse(CompuFuncs.decodeIStream(context
									.getResources().openRawResource(
											field.getInt(null))));
					lInfo.author = info.getAuthor();
					lInfo.name = info.getName();
					db.InsertLevelInfo(lInfo);
				}
				if(db.CheckLevelSetExists(lInfo.set))
				{
					Log.v("SpaceGame", "Levelset " + lInfo.filename
							+ " doesn't exist in DB, creating...");
					lSetInfo.name = lInfo.set;
					lSetInfo.filename = lInfo.set;
					lSetInfo.author = "Will";
					db.InsertLevelSetInfo(lSetInfo);
				}
			}
		}

		Log.i("SpaceGame",
				"Checking for items in database which aren't available...");
		db.CheckDatabaseValidity();

		db.getWritableDatabase().close();
		Log.i("SpaceGame", "Finished updating database");
	}

	public ArrayList<LevelExtendedInfo> GetLevelsFromSet(String set)
	{
		return db.GetLevelsFromSet(set);
	}

	public InputStream GetLevelIStream(LevelExtendedInfo info)
	{
		if(info.set.startsWith(BUILTIN_PREFIX))
		{
			try
			{
				return context.getResources().openRawResource(
						R.raw.class.getField(
								"lvl_" + info.set.replaceFirst(BUILTIN_PREFIX, "") + "_"
										+ info.filename).getInt(null));
			} catch (NotFoundException e)
			{
				e.printStackTrace();
				Log.e("SpaceGame", "Resource not found: "
						+ e.getLocalizedMessage());
			} catch (IllegalArgumentException e)
			{
				e.printStackTrace();
				Log.e("SpaceGame", "Illegal Argument: "
						+ e.getLocalizedMessage());
			} catch (SecurityException e)
			{
				e.printStackTrace();
				Log.e("SpaceGame", "Error finding item in Resources: "
						+ e.getLocalizedMessage());
			} catch (IllegalAccessException e)
			{
				e.printStackTrace();
				Log.e("SpaceGame", "Error finding item in Resources: "
						+ e.getLocalizedMessage());
			} catch (NoSuchFieldException e)
			{
				e.printStackTrace();
				Log.e("SpaceGame", "Error finding item in Resources: "
						+ e.getLocalizedMessage());
			}
		}
		return null;
	}
	
	public void ResetDB()
	{
		db.ResetDB();
	}

	private class DbStorage extends SQLiteOpenHelper
	{
		private static final String KEY_ID = "id";
		private static final String KEY_FROMSET = "fromset";
		private static final String KEY_NAME = "name";
		private static final String KEY_AUTHOR = "author";
		private static final String KEY_FILENAME = "file";
		private static final String KEY_TIME = "time";

		private static final int DB_VERSION = 1;
		private static final String DB_LEVELS_NAME = "levels";
		private static final String DB_LEVELS_CREATE = "CREATE TABLE "
				+ DB_LEVELS_NAME + " (" + KEY_ID
				+ " INTEGER PRIMARY KEY NOT NULL, " + KEY_FROMSET + " TEXT, "
				+ KEY_NAME + " TEXT, " + KEY_AUTHOR + " TEXT, " + KEY_FILENAME
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
				Log.e("SpaceGame", "SQL Error: " + e.getMessage());
			}
			Log.i("SpaceGame", "Created database");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{

		}

		private boolean CheckLevelSetExists(String levelset)
		{
			Cursor c = getWritableDatabase().query(DB_SETS_NAME,
					new String[] { KEY_NAME }, KEY_NAME + " = ?",
					new String[] { levelset }, null, null, null);
			boolean ret = c.getCount() == 0;
			c.close();
			return ret;
		}

		private boolean CheckLevelExists(String levelfilename, String levelset)
		{
			Cursor c = getWritableDatabase().query(DB_LEVELS_NAME,
					new String[] { KEY_NAME },
					"(" + KEY_FILENAME + " = ?) AND (" + KEY_FROMSET + " = ?)",
					new String[] { levelfilename, levelset }, null, null, null);
			boolean ret = c.getCount() == 0;
			c.close();
			return ret;
		}

		private void InsertLevelInfo(LevelInfo info)
		{
			ContentValues vals = new ContentValues();
			vals.put(KEY_FROMSET, info.set);
			vals.put(KEY_NAME, info.name);
			vals.put(KEY_AUTHOR, info.author);
			vals.put(KEY_FILENAME, info.filename);
			getWritableDatabase().insert(DB_LEVELS_NAME, "", vals);
		}

		private void InsertLevelSetInfo(LevelSetInfo info)
		{
			ContentValues vals = new ContentValues();
			vals.put(KEY_NAME, info.name);
			vals.put(KEY_AUTHOR, info.author);
			vals.put(KEY_FILENAME, info.filename);
			getWritableDatabase().insert(DB_SETS_NAME, "", vals);
		}

		private void CheckDatabaseValidity()
		{
			Cursor levels = getWritableDatabase().query(DB_LEVELS_NAME,
					new String[] { KEY_FILENAME, KEY_FROMSET }, null, null,
					null, null, null);
			while (levels.moveToNext())
			{
				if(levels.getString(levels.getColumnIndex(KEY_FROMSET))
						.startsWith(LevelManager.BUILTIN_PREFIX))
				{
					String fromset = levels.getString(
							levels.getColumnIndex(KEY_FROMSET)).replaceFirst(
							LevelManager.BUILTIN_PREFIX, "");
					String filename = levels.getString(levels
							.getColumnIndex(KEY_FILENAME));
					// Log.v("SpaceGame", "Level found in set " + fromset +
					// ", filename: " + filename);
					try
					{
						R.raw.class.getField("lvl_" + fromset + "_" + filename);
					} catch (SecurityException e)
					{
						e.printStackTrace();
					} catch (NoSuchFieldException e)
					{
						// Doesn't exist
						Log.i("SpaceGame", "Level " + fromset + "." + filename
								+ " no longer exists, deleting from database");
						DeleteLevel(filename, fromset);
						Log.v("SpaceGame", "Deleted!");
					}
				}
			}
			levels.close();
		}

		private void DeleteLevel(String filename, String set)
		{
			getWritableDatabase()
					.delete(
							DB_LEVELS_NAME,
							"(" + KEY_FILENAME + " = ?) AND " + "("
									+ KEY_FROMSET + " = ?)",
							new String[] { filename,
									LevelManager.BUILTIN_PREFIX + set });
		}

		@SuppressWarnings("unused")
		private void DeleteLevelSet(String set)
		{
			getWritableDatabase().delete(DB_SETS_NAME, KEY_FILENAME + " = ?",
					new String[] { set });
		}

		private ArrayList<LevelExtendedInfo> GetLevelsFromSet(String set)
		{
			ArrayList<LevelExtendedInfo> items = new ArrayList<LevelExtendedInfo>();

			try
			{
				Cursor c = getReadableDatabase().query(
						DB_LEVELS_NAME,
						new String[] { KEY_FROMSET, KEY_NAME, KEY_AUTHOR,
								KEY_FILENAME, KEY_TIME }, KEY_FROMSET + " = ?",
						new String[] { set }, null, null, KEY_FILENAME);
				int idFromset = c.getColumnIndex(KEY_FROMSET);
				int idName = c.getColumnIndex(KEY_NAME);
				int idAuthor = c.getColumnIndex(KEY_AUTHOR);
				int idFilename = c.getColumnIndex(KEY_FILENAME);
				int idTime = c.getColumnIndex(KEY_TIME);
				while (c.moveToNext())
				{
					items.add(new LevelExtendedInfo(c.getString(idName), c
							.getString(idFromset), c.getString(idAuthor), c
							.getString(idFilename), c.getInt(idTime)));
				}
				c.close();
			} catch (SQLiteException e)
			{
				Log.e("SpaceGame", "SQLite Database Error: "
						+ e.getLocalizedMessage());
			}

			return items;
		}
		
		protected void ResetDB()
		{
			Log.i("SpaceGame", "Deleting all data from DB...");
			getWritableDatabase().delete(DB_LEVELS_NAME, null, null);
			getWritableDatabase().delete(DB_SETS_NAME, null, null);
			getWritableDatabase().close();
		}
	}

	protected static class LevelBaseInfo
	{
		public String name, set = null;

		public LevelBaseInfo(String name, String set)
		{
			this.name = name;
			this.set = set;
		}
	}

	protected static class LevelInfo extends LevelBaseInfo
	{
		public String author, filename;

		public LevelInfo(String name, String set, String author, String filename)
		{
			super(name, set);
			this.author = author;
			this.filename = filename;
		}

		public LevelInfo()
		{
			super(null, null);
		}
	}

	public static final class LevelExtendedInfo extends LevelInfo
	{
		public int time;

		public LevelExtendedInfo(String name, String set, String author,
				String filename, int time)
		{
			super(name, set, author, filename);
			this.time = time;
		}
	}

	protected static final class LevelSetInfo
	{
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
