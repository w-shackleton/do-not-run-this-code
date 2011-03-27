
package uk.digitalsquid.spacegame.levels;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.items.BlackHole;
import uk.digitalsquid.spacegame.spaceitem.items.GravityField;
import uk.digitalsquid.spacegame.spaceitem.items.InfoBox;
import uk.digitalsquid.spacegame.spaceitem.items.Planet;
import uk.digitalsquid.spacegame.spaceitem.items.Spring;
import uk.digitalsquid.spacegame.spaceitem.items.Teleporter;
import uk.digitalsquid.spacegame.spaceitem.items.Wall;
import android.content.Context;
import android.sax.Element;
import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.sax.StartElementListener;
import android.sax.TextElementListener;
import android.util.Xml;

public class SaxLoader
{
	private static final String ROOT = "level";
	
	private static final String NAME = "name";
	private static final String START = "start";
	private static final String STARTSPEED = "startspeed";
	private static final String BOUNDS = "bounds";

	private static final String COORD_X = "x";
	private static final String COORD_Y = "y";
	
	private static final String ITEMS = "items";
	
	private static final String ITEMS_BH = "blackhole";
	private static final String ITEMS_GRAV = "gravity";
	private static final String ITEMS_INFOBOX = "infobox";
	private static final String ITEMS_PLANET = "planet";
	private static final String ITEMS_SPRING = "spring";
	private static final String ITEMS_TELEPORTER = "teleporter";
	private static final String ITEMS_WALL = "wall";

	private static final String ITEMS_KEY_SIZE = "size";
	private static final String ITEMS_KEY_ROTATION = "rotation";
	private static final String ITEMS_KEY_SPEED = "speed";
	private static final String ITEMS_KEY_RADIUS = "radius";
	private static final String ITEMS_KEY_BOUNCINESS = "bounciness";
	private static final String ITEMS_KEY_TYPE = "type";
	
	/**
	 * Other default {@link Coord} for {@link #getCoord(Attributes, Coord)}.
	 */
	private static final Coord DEF_WALL = new Coord(200, 200);
	
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
		
		root.requireChild(NAME).setEndTextElementListener(new EndTextElementListener()
		{
			@Override
			public void end(String body)
			{
				level.LevelName = body;
			}
		});

		root.requireChild(START).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.startPos = getCoord(attributes);
			}
		});
		root.requireChild(STARTSPEED).setStartElementListener(new StartElementListener()
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
						getNum(attributes, ITEMS_KEY_ROTATION, 0),
						getNum(attributes, ITEMS_KEY_SPEED, 0)));
			}
		});
		
		items.getChild(ITEMS_INFOBOX).setTextElementListener(new TextElementListener()
		{
			Coord coord;
			boolean initialshow;
			float rotation;
			@Override
			public void start(Attributes attributes)
			{
				coord = getCoord(attributes);
				initialshow = attributes.getValue("initialshow").contains("true");
				rotation = getNum(attributes, ITEMS_KEY_ROTATION, 0);
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
						getNum(attributes, ITEMS_KEY_RADIUS, 30),
						(int) getNum(attributes, ITEMS_KEY_TYPE, 0));
				level.planetList.add(p);
			}
		});
		
		items.getChild(ITEMS_SPRING).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				Coord[] cs = getAbCoord(attributes);
				level.planetList.add(
						new Spring(context, cs[0], cs[1], getNum(attributes, ITEMS_KEY_BOUNCINESS, 1)));
			}
		});
		
		items.getChild(ITEMS_TELEPORTER).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new Teleporter(context, getCoord(attributes), getCoord(attributes, "d")));
			}
		});
		
		items.getChild(ITEMS_WALL).setStartElementListener(new StartElementListener()
		{
			@Override
			public void start(Attributes attributes)
			{
				level.planetList.add(new Wall(
						context,
						getCoord(attributes),
						getNum(attributes, ITEMS_KEY_SIZE, 100),
						getNum(attributes, ITEMS_KEY_ROTATION, 0)));
			}
		});
	}
	
	/**
	 * Get a {@link Coord} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @param defaultCoord The default {@link Coord} to return if there is an error
	 * @return A new {@link Coord} containing the values.
	 */
	private static final Coord getCoord(Attributes attributes, Coord defaultCoord)
	{
		try
		{
			return new Coord(
					Integer.parseInt(
							attributes.getValue(COORD_X)),
					Integer.parseInt(
							attributes.getValue(COORD_Y)));
		}
		catch(NumberFormatException e)
		{
			errorOccurred = true;
			return new Coord(defaultCoord);
		}
	}
	
	/**
	 * Get a {@link Coord} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Coord} containing the values.
	 */
	private static final Coord getCoord(Attributes attributes)
	{
		try
		{
			return new Coord(
					Integer.parseInt(
							attributes.getValue(COORD_X)),
					Integer.parseInt(
							attributes.getValue(COORD_Y)));
		}
		catch(NumberFormatException e)
		{
			errorOccurred = true;
			return new Coord();
		}
	}
	
	/**
	 * Get a {@link Coord} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Coord} containing the values.
	 */
	private static final Coord getCoord(Attributes attributes, String prefix)
	{
		try
		{
			return new Coord(
					Integer.parseInt(
							attributes.getValue(prefix + COORD_X)),
					Integer.parseInt(
							attributes.getValue(prefix + COORD_Y)));
		}
		catch(NumberFormatException e)
		{
			errorOccurred = true;
			return new Coord();
		}
	}
	
	/**
	 * Get a {@link Coord} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Coord} containing the values.
	 */
	private static final Coord getSCoord(Attributes attributes)
	{
		return getCoord(attributes, "s"); // Get coord with the 's' prefix.
	}
	
	/**
	 * Get a {@link Coord} with the x and y values from attributes
	 * @param attributes The SAX Attributes from which to get the values
	 * @return A new {@link Coord} containing the values.
	 */
	private static final Coord[] getAbCoord(Attributes attributes)
	{
		return new Coord[]{
				getCoord(attributes, "a"),
				getCoord(attributes, "b"),
		};
	}
	
	/**
	 * Extract an integer from these attributes
	 */
	private static final float getNum(Attributes attributes, String key, float defaultValue)
	{
		try
		{
			return Float.valueOf(attributes.getValue(key)).floatValue();
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
	 * Makes sure that only one instance of this class is processing data at one time,
	 * to stop data becoming mashed up.
	 */
	private static boolean running = false;
	
	private static Context context;
	
	/**
	 * Parse data through this parser.
	 */
	public synchronized static final LevelItem parse(Context context, String data) throws SAXException
	{
		SaxLoader.context = context;
		try
		{
			Xml.parse(data, root.getContentHandler());
		} catch (SAXException e)
		{
			running = false;
			throw e;
		}
		running = false;
		if(errorOccurred)
		{
			errorOccurred = false;
			// throw new SAXException("Error decoding values from XML");
		}
		return level;
	}
}
