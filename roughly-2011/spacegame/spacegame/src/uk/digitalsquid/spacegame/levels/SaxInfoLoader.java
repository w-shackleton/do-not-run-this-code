package uk.digitalsquid.spacegame.levels;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.sax.ElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class SaxInfoLoader
{
	private static final String ROOT = "level";

	private static final String NAME = "name";
	private static final String CREATOR = "creator";
	
	private static RootElement root = null;
	
	private static LevelInfo level;
	
	public static class LevelInfo
	{
		private String author;
		private String name;
		
		public LevelInfo(String author, String name)
		{
			setName(name);
			setAuthor(author);
		}
		
		public LevelInfo()
		{
			
		}

		public void setName(String name)
		{
			this.name = name;
		}

		public String getName()
		{
			return name;
		}

		public void setAuthor(String author)
		{
			this.author = author;
		}

		public String getAuthor()
		{
			return author;
		}
	}
	
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
				level = new LevelInfo();
			}

			@Override
			public void end()
			{
				
			}
		});
		
		root.requireChild(NAME).setEndTextElementListener(new EndTextElementListener()
		{
			@Override
			public void end(String body)
			{
				level.name = body;
			}
		});
		
		root.requireChild(CREATOR).setEndTextElementListener(new EndTextElementListener()
		{
			@Override
			public void end(String body)
			{
				level.author = body;
			}
		});
	}
	
	/**
	 * Makes sure that only one instance of this class is processing data at one time,
	 * to stop data becoming mashed up.
	 */
	private static boolean running = false;
	
	/**
	 * Parse data through this parser.
	 */
	public synchronized static final LevelInfo parse(String data) throws SAXException
	{
		while(running)	// Wait until not running, then continue.
						// This will probably never be needed, but is there in case of simulataneous threads running this code.
		{
			try
			{
				Thread.sleep(20);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		running = true;
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
			throw new SAXException("Error decoding values from XML");
		}
		return level;
	}
}
