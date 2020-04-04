package uk.digitalsquid.physicsland.pfile;

import java.util.ArrayList;

import android.util.Log;

public class PFileParser
{
	public String name, description, items;
	public ArrayList<String> objs = new ArrayList<String>();
	private final static String OBJ_BEG = "BEGOBJ\n", OBJ_END = "ENDOBJ";
	public PFileParser(String fileText)
	{
		name = getLine("NAME", fileText);
		description = getLine("DESC", fileText);
		
		items = fileText.substring(fileText.indexOf(OBJ_BEG) + OBJ_BEG.length(), fileText.indexOf(OBJ_END));
		Log.v("PhysicsLand", "PFP: Items:\n" + items);
		while(items.contains("\n"))
		{
			objs.add(items.substring(0, items.indexOf("\n")));
			Log.v("PhysicsLand", "PFP: item = \"" + items.substring(0, items.indexOf("\n")) + "\"");
			items = items.substring(items.indexOf("\n") + 1);
		}
	}
	private String getLine(String key, String text)
	{
		key += " ";
		try
		{
			return text.substring(text.indexOf(key) + key.length(), text.indexOf("\n", text.indexOf(key) + 1));
		}
		catch (Exception e)
		{
			return null;
		}
	}
}
