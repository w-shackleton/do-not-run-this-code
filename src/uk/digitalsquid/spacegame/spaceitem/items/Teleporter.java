package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Teleporter extends Gravitable implements TopDrawable, Moveable, Warpable
{
	private static final int TRANSPORTER_RADIUS = 70;
	private static final float TRANSPORTER_DENSITY = 0.7f;
	
	protected final Coord destination;
	
	protected int rotation = 0;
	
	protected BitmapDrawable teleporter;
	
	public Teleporter(Context context, Coord coord, Coord destination)
	{
		super(context, coord, 0.98f, TRANSPORTER_DENSITY, TRANSPORTER_RADIUS / 2);
		this.destination = destination;
		teleporter = (BitmapDrawable) context.getResources().getDrawable(R.drawable.teleporter);
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		c.drawCircle((float)pos.x, (float)pos.y, radius, PaintLoader.load(new PaintDesc(0, 0,0)));
	}

	@Override
	public void drawTop(Canvas c, float worldZoom)
	{
		c.rotate(-rotation, (float)pos.x, (float)pos.y);
		teleporter.setAntiAlias(StaticInfo.Antialiasing);
		teleporter.setBounds(new Rect(
				(int)((pos.x - (radius)) * worldZoom),
				(int)((pos.y - (radius)) * worldZoom),
				(int)((pos.x + (radius)) * worldZoom),
				(int)((pos.y + (radius)) * worldZoom)));
		teleporter.draw(c);
		c.rotate(rotation, (float)pos.x, (float)pos.y);
	}
	
	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		BallData data = super.calculateVelocity(itemC, itemVC, itemRadius);

		double currDist = pos.minus(itemC).getLength();
		if(currDist < 10f * ITEM_SCALE) // Start teleport
		{
			data.itemC = new Coord(destination);
			data.itemVC = new Coord();
		}
		
		return data;
	}

	@Override
	public void move(float millistep, float speedScale)
	{
		rotation -= 1;
	}

	@Override
	public WarpData sendWarpData()
	{
		return null; // new WarpData((float)(Math.sin((double)rotation / 20) + 2) / 10 + 1, 0, 0, false);
	}
	
}
