package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.spaceitem.BounceableRect;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Wall extends BounceableRect
{
	protected static final int LINES = 10;
	protected static final float RAND_MIN_SIZE = 10 * ITEM_SCALE;
	protected static final float RAND_MAX_SIZE = 20 * ITEM_SCALE;
	protected static final Random rGen = new Random();
	
	protected static final float BOUNCINESS = 0.7f;
	
	protected BitmapDrawable wallside;
	
	protected static final PaintDesc wallPaint = new PaintDesc(20, 100, 40);
	
	protected static final int WALL_WIDTH = 16;
	protected static final int WALL_MIN_X = 80;
	protected static final int WALL_MAX_X = 1000;
	
	/**
	 * Construct a new {@link Wall}.
	 * @param context
	 * @param coord
	 * @param size The size of the wall
	 * @param rotation The rotation of this object, in DEGREES
	 * @param bounciness
	 */
	public Wall(Context context, Coord coord, float size, float rotation)
	{
		super(context, coord, new Coord(CompuFuncs.TrimMinMax(size, WALL_MIN_X, WALL_MAX_X), WALL_WIDTH), rotation, BOUNCINESS);
		
		wallside = (BitmapDrawable) context.getResources().getDrawable(R.drawable.wallside);
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		c.rotate(rotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom);
		
		wallside.setAntiAlias(StaticInfo.Antialiasing);
		wallside.setBounds(new Rect(
				(int)((pos.x - (size.x / 2)) * worldZoom),
				(int)((pos.y - (size.y / 2)) * worldZoom),
				(int)((pos.x - (size.x / 2) + size.y) * worldZoom),
				(int)((pos.y + (size.y / 2)) * worldZoom))
				);
		wallside.draw(c);
		
		Rect corner2pos = new Rect(
				(int)((pos.x + (size.x / 2) - size.y) * worldZoom),
				(int)((pos.y - (size.y / 2)) * worldZoom),
				(int)((pos.x + (size.x / 2)) * worldZoom),
				(int)((pos.y + (size.y / 2)) * worldZoom));
		c.rotate(180, corner2pos.exactCenterX(), corner2pos.exactCenterY());
		wallside.setAntiAlias(StaticInfo.Antialiasing);
		wallside.setBounds(corner2pos);
		wallside.draw(c);
		c.rotate(-180, corner2pos.exactCenterX(), corner2pos.exactCenterY());
		
		final Coord start = new Coord(pos.x - (size.x / 2) + size.y, pos.y);
		final Coord fin   = new Coord(pos.x + (size.x / 2) - size.y, pos.y);
		for(int i = 0; i < LINES; i++)
		{
			int currPos = 0;
			Coord prevPos = new Coord(start);
			while(currPos + RAND_MAX_SIZE < fin.x - start.x)
			{
				final float posHeight = (float) (rGen.nextFloat() * size.y);
				final float posWidth = rGen.nextFloat() * (RAND_MAX_SIZE - RAND_MIN_SIZE) + RAND_MIN_SIZE;
				
				currPos += posWidth;
				
				c.drawLine(
						(float)(prevPos.x) * worldZoom,
						(float)(prevPos.y) * worldZoom,
						(float)(prevPos.x + posWidth) * worldZoom,
						(float)(pos.y - (size.y / 2) + posHeight) * worldZoom,
						PaintLoader.load(wallPaint));
				prevPos.x = prevPos.x + posWidth;
				prevPos.y = pos.y - (size.y / 2) + posHeight;
			}
			c.drawLine(
					(float)prevPos.x * worldZoom,
					(float)prevPos.y * worldZoom,
					(float)fin.x * worldZoom,
					(float)fin.y * worldZoom,
					PaintLoader.load(wallPaint));
		}
		
		c.rotate(-rotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom);
	}
}
