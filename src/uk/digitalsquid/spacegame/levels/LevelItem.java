package uk.digitalsquid.spacegame.levels;

import java.util.ArrayList;
import java.util.List;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;

public class LevelItem
{
	public String LevelName;
	public List<SpaceItem> planetList;
	public Coord startPos;
	public Coord startSpeed;
	public Coord bounds;
	
	// TODO: Move COORD_BOUNDS_DRAWEXT?
	public static final Coord COORD_BOUNDS_DRAWEXT = new Coord(1, 1);
	
	public LevelItem(String name, SpaceItem[] planets, float coordX, float coordY, float boundX, float boundY)
	{
		LevelName = name;
		startPos = new Coord(coordX * SpaceItem.ITEM_SCALE, coordY * SpaceItem.ITEM_SCALE);
		bounds = new Coord(boundX * SpaceItem.ITEM_SCALE, boundY * SpaceItem.ITEM_SCALE);
		planetList = new ArrayList<SpaceItem>();
		for(int i = 0; i < planets.length; i++)
		{
			planetList.add(planets[i]);
		}
		
		startSpeed = new Coord();
	}
	
	/**
	 * Initialise a new {@link LevelItem} with blank variables, for internal constructors.
	 */
	protected LevelItem()
	{
		planetList = new ArrayList<SpaceItem>();
	}
	
	protected void initialisePart2()
	{
		if(startPos == null)
			startPos = new Coord(); // In case it was accidentially not initialised. (ie not in XML)
		else
		{
			startPos.x *= SpaceItem.ITEM_SCALE;
			startPos.y *= SpaceItem.ITEM_SCALE;
		}
		
		if(startSpeed == null)
			startSpeed = new Coord(); // In case it was accidentially not initialised. (ie not in XML)
		else
		{
			startSpeed.x *= SpaceItem.ITEM_SCALE;
			startSpeed.y *= SpaceItem.ITEM_SCALE;
		}
		
		if(bounds == null)
			bounds = new Coord(200 * SpaceItem.ITEM_SCALE, 200 * SpaceItem.ITEM_SCALE);
		else
		{
			bounds.x *= SpaceItem.ITEM_SCALE;
			bounds.y *= SpaceItem.ITEM_SCALE;
		}
	}
}
