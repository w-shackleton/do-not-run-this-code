package uk.digitalsquid.spacegame.levels;

import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.spaceitem.items.BlackHole;
import uk.digitalsquid.spacegame.spaceitem.items.BlockDef;
import uk.digitalsquid.spacegame.spaceitem.items.GravityField;
import uk.digitalsquid.spacegame.spaceitem.items.InfoBox;
import uk.digitalsquid.spacegame.spaceitem.items.Planet;
import uk.digitalsquid.spacegame.spaceitem.items.Star;
import uk.digitalsquid.spacegame.spaceitem.items.Wall;
import uk.digitalsquid.spacegamelib.SimulationContext;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.sax.TextElementListener;
import android.util.Xml;

/**
 * Loads a game from XML
 * @author william
 *
 */
public class SaxLoader
{
	public static final float LOAD_SCALE = 0.1f;
	
	private static final String ROOT = "level";
	
	private static final String NAME = "name";
	private static final String START = "start";
	private static final String STARTSPEED = "startspeed";
	private static final String BOUNDS = "bounds";
	private static final String PORTAL = "portal";
	private static final String STARS = "stars";

	private static final String COORD_X = "x";
	private static final String COORD_Y = "y";
	
	private static final String ITEMS = "items";
	
	private static final String ITEMS_BH = "blackhole";
	private static final String ITEMS_GRAV = "gravity";
	private static final String ITEMS_INFOBOX = "infobox";
	private static final String ITEMS_PLANET = "planet";
	// private static final String ITEMS_SPRING = "spring";
	// private static final String ITEMS_TELEPORTER = "teleporter";
	private static final String ITEMS_WALL = "wall";
	private static final String ITEMS_STAR = "star";
	private static final String ITEMS_BLOCK = "block";

	private static final String ITEMS_KEY_ROTATION = "rotation";
	private static final String ITEMS_KEY_POWER = "power";
	private static final String ITEMS_KEY_RADIUS = "radius";
	// private static final String ITEMS_KEY_BOUNCINESS = "bounciness";
	private static final String ITEMS_KEY_TYPE = "type";
	
	/**
	 * Other default {@link Vec2} for {@link #getCoord(Attributes, Vec2)}.
	 */
	private static final Vec2 DEF_WALL = new Vec2(200, 200);
	
	private static RootElement root = null;
	
	private static LevelItem level;
	
	private static boolean errorOccurred = false;
	
	/**
	 * Initialise the class. This should be run at the splash screen and uses less memory,
	 * by only having one instance.
	 */
	public static final void initialise()
	{
		if(root != null) return;
		root = new RootElement(ROOT);
		root.setElementListener(new ElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level = new LevelItem();
			}

			@Override
			public void end()
			{
				level.initialisePart2();
			}
		});
		
		root.getChild(NAME).setEndTextElementListener(new EndTextElementListener()
		{
			@Override
			public void end(String body)
			{
				level.levelName = body;
			}
		});

		root.getChild(START).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.startPos = getCoord(attributes);
			}
		});
		root.getChild(STARTSPEED).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.startSpeed = getCoord(attributes);
			}
		});
		root.getChild(BOUNDS).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.bounds = getCoord(attributes, DEF_WALL);
			}
		});
		
		root.getChild(PORTAL).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.portal = getCoord(attributes);
			}
		});
		
		root.getChild(STARS).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.starsToCollect = getInt(attributes, "value", 10);
			}
		});
		
		Element items = root.getChild(ITEMS);
		
		items.getChild(ITEMS_BH).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new BlackHole(context, getCoord(attributes)));
			}
		});
		
		items.getChild(ITEMS_GRAV).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new GravityField(context,
						getCoord(attributes),
						getSCoord(attributes),
						getFloat(attributes, ITEMS_KEY_ROTATION, 0),
						getFloat(attributes, ITEMS_KEY_POWER, 0)));
			}
		});
		
		items.getChild(ITEMS_INFOBOX).setTextElementListener(new TextElementListener()
		{
			Vec2 coord;
			boolean initialshow;
			float rotation;
			@Override
			public void start(Attributes attributes)
			{
				coord = getCoord(attributes);
				initialshow = attributes.getValue("initialshow").contains("true");
				rotation = getFloat(attributes, ITEMS_KEY_ROTATION, 0);
			}

			@Override
			public void end(String body)
			{
				level.planetList.add(new InfoBox(context, coord, rotation, body, initialshow));
			}
		});
		
		items.getChild(ITEMS_PLANET).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				Planet p = new Planet(
						context,
						getCoord(attributes),
						getFloat(attributes, ITEMS_KEY_RADIUS, 30) * LOAD_SCALE,
						getInt(attributes, ITEMS_KEY_TYPE, 0));
				level.planetList.add(p);
			}
		});
		
		/* items.getChild(ITEMS_SPRING).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				Coord[] cs = getAbCoord(attributes);
				level.planetList.add(
						new Spring(context, cs[0], cs[1], getFloat(attributes, ITEMS_KEY_BOUNCINESS, 1)));
			}
		}); */
		/* 
		items.getChild(ITEMS_TELEPORTER).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new Teleporter(context, getCoord(attributes), getCoord(attributes, "d")));
			}
		});
		*/
		items.getChild(ITEMS_WALL).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new Wall(
						context,
						getCoord(attributes),
						getFloat(attributes, "sx", 100) * LOAD_SCALE,
						getFloat(attributes, ITEMS_KEY_ROTATION, 0)));
			}
		});
		
		items.getChild(ITEMS_STAR).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new Star(
						context,
						getCoord(attributes)));
			}
		});
		
		items.getChild(ITEMS_BLOCK).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				final int blockId = getInt(attributes, "type", 0);
				final boolean hasVortex = getBoolean(attributes, "hasVortex", true);
				BlockDef def = BlockDef.getBlockDef(blockId);
				if(def == null) return;
				level.planetList.add(def.create(
						context,
						getCoord(attributes),
						getSCoord(attributes),
						getFloat(attributes, ITEMS_KEY_ROTATION, 0),
						hasVortex
						));
			}
		});
	}
	
	/**
	 * Get a {@link Vec2} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @param defaultVec2 The default {@link Vec2} to return if there is an error
	 * @return A new {@link Vec2} containing the values.
	 */
	private static final Vec2 getCoord(Attributes attributes,Vec2 defaultCoord)
	{
		try
		{
			return new Vec2(
					Float.parseFloat(
							attributes.getValue(COORD_X)) * LOAD_SCALE,
					Float.parseFloat(
							attributes.getValue(COORD_Y)) * LOAD_SCALE);
		}
		catch(NumberFormatException e)
		{
			errorOccurred = true;
			return new Vec2(defaultCoord);
		}
		catch(NullPointerException e) {
			errorOccurred = true;
			return new Vec2(defaultCoord);
		}
	}
	
	/**
	 * Get a {@link Vec2} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Vec2} containing the values.
	 */
	private static final Vec2 getCoord(Attributes attributes)
	{
		try
		{
			return new Vec2(
					Float.parseFloat(
							attributes.getValue(COORD_X)) * LOAD_SCALE,
					Float.parseFloat(
							attributes.getValue(COORD_Y)) * LOAD_SCALE);
		}
		catch(NumberFormatException e)
		{
			errorOccurred = true;
			return new Vec2();
		}
		catch(NullPointerException e) {
			errorOccurred = true;
			return new Vec2();
		}
	}
	
	/**
	 * Get a {@link Vec2} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Vec2} containing the values.
	 */
	private static final Vec2 getCoord(Attributes attributes, String prefix)
	{
		try
		{
			return new Vec2(
					Float.parseFloat(
							attributes.getValue(prefix + COORD_X)) * LOAD_SCALE,
					Float.parseFloat(
							attributes.getValue(prefix + COORD_Y)) * LOAD_SCALE);
		}
		catch(NumberFormatException e)
		{
			errorOccurred = true;
			return new Vec2();
		}
		catch(NullPointerException e) {
			errorOccurred = true;
			return new Vec2();
		}
	}
	
	/**
	 * Get a {@link Vec2} with the sx and sy values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Vec2} containing the values.
	 */
	private static final Vec2 getSCoord(Attributes attributes)
	{
		return getCoord(attributes, "s"); // Get coord with the 's' prefix.
	}
	
	/**
	 * Get a {@link Vec2} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Vec2} containing the values.
	 */
	/* private static final Vec2[] getAbCoord(Attributes attributes)
	{
		return new Vec2[]{
				getCoord(attributes, "a"),
				getCoord(attributes, "b"),
		};
	} */
	
	/**
	 * Extract an integer from these attributes - integers aren't scaled
	 */
	private static final int getInt(Attributes attributes, String key, int defaultValue)
	{
		try {
			return Integer.valueOf(attributes.getValue(key));
		}
		catch(NumberFormatException e) {
			errorOccurred = true;
			return defaultValue;
		}
		catch(NullPointerException e) {
			errorOccurred = true;
			return defaultValue;
		}
	}
	
	private static final boolean getBoolean(Attributes attributes, String key, boolean defaultValue) {
		try
		{
			return Boolean.valueOf(attributes.getValue(key));
		}
		catch(NumberFormatException e) {
			errorOccurred = true;
			return defaultValue;
		}
		catch(NullPointerException e) {
			errorOccurred = true;
			return defaultValue;
		}
	}
	
	/**
	 * Extract an integer from these attributes
	 */
	private static final float getFloat(Attributes attributes, String key, float defaultValue)
	{
		try
		{
			return Float.valueOf(attributes.getValue(key));
		}
		catch(NumberFormatException e) {
			errorOccurred = true;
			return defaultValue;
		}
		catch(NullPointerException e) {
			errorOccurred = true;
			return defaultValue;
		}
	}
	
	private static SimulationContext context;
	
	/**
	 * Parse data through this parser.
	 */
	public synchronized static final LevelItem parse(SimulationContext context, String data) throws SAXException
	{
		SaxLoader.context = context;
		try
		{
			Xml.parse(data, root.getContentHandler());
		} catch (SAXException e)
		{
			throw e;
		}
		
		if(errorOccurred)
		{
			errorOccurred = false;
			// throw new SAXException("Error decoding values from XML");
		}
		return level;
	}
}
