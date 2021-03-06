package uk.digitalsquid.spacegame.levels;

import java.util.ArrayList;
import java.util.List;

import org.jbox2d.common.Vec2;

import android.os.Parcel;
import android.os.Parcelable;

import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.misc.LevelWall;
import uk.digitalsquid.spacegamelib.spaceitem.SpaceItem;

public final class LevelItem
{
	public String levelName;
	public List<SpaceItem> planetList;
	public Vec2 startPos;
	public Vec2 startSpeed;
	public Vec2 bounds;
	
	public Vec2 portal;
	
	public int starsToCollect;
	
	// TODO: Move COORD_BOUNDS_DRAWEXT?
	public static final Vec2 COORD_BOUNDS_DRAWEXT = new Vec2(1, 1);
	
	public LevelItem(String name, SpaceItem[] planets, float coordX, float coordY, float boundX, float boundY, int starsToCollect)
	{
		levelName = name;
		startPos = new Vec2(coordX, coordY);
		bounds = new Vec2(boundX, boundY);
		planetList = new ArrayList<SpaceItem>();
		for(int i = 0; i < planets.length; i++)
		{
			planetList.add(planets[i]);
		}
		
		startSpeed = new Vec2();
		portal = new Vec2();
		
		this.starsToCollect = starsToCollect;
	}
	
	/**
	 * Initialise a new {@link LevelItem} with blank variables, for internal constructors.
	 */
	protected LevelItem()
	{
		planetList = new ArrayList<SpaceItem>();
		portal = new Vec2();
	}
	
	protected void initialisePart2()
	{
		if(startPos == null)
			startPos = new Vec2(); // In case it was accidentially not initialised. (ie not in XML)
		
		if(startSpeed == null)
			startSpeed = new Vec2(); // In case it was accidentially not initialised. (ie not in XML)
		
		if(bounds == null)
			bounds = new Vec2(2, 2);
		
		// if(portal == null) 
			// portal = new Coord();
		if(portal.equals(startPos)) {
			startPos.x++;
		}
		
		if(levelName == null) levelName = "";
		
		if(starsToCollect < 1) starsToCollect = 10;
	}
	
	public void initialiseBox2D(SimulationContext context) {
		new LevelWall(context, new Vec2(-(bounds.x / 2 + 1), 0), new Vec2(2, bounds.y));
		new LevelWall(context, new Vec2(+(bounds.x / 2 + 1), 0), new Vec2(2, bounds.y));
		
		new LevelWall(context, new Vec2(0, -(bounds.y / 2 + 1)), new Vec2(bounds.x, 2));
		new LevelWall(context, new Vec2(0, +(bounds.y / 2 + 1)), new Vec2(bounds.x, 2));
	}
	
	public static final class LevelSummary implements Parcelable {
		public final int starsToCollect;
		public final int starsCollected;
		public final int timeTaken;
		
		public LevelSummary(int starsToCollect, int starsCollected, int timeTaken) {
			this.starsCollected = starsCollected;
			this.starsToCollect = starsToCollect;
			this.timeTaken = timeTaken;
		}
		
		private LevelSummary(Parcel p) {
			starsToCollect = p.readInt();
			starsCollected = p.readInt();
			timeTaken = p.readInt();
		}

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(starsToCollect);
			dest.writeInt(starsCollected);
			dest.writeInt(timeTaken);
		}
	     public static final Parcelable.Creator<LevelSummary> CREATOR
		         = new Parcelable.Creator<LevelSummary>() {
		     public LevelSummary createFromParcel(Parcel in) {
		         return new LevelSummary(in);
		     }
		
		     public LevelSummary[] newArray(int size) {
		         return new LevelSummary[size];
		     }
	     };
	}
}
