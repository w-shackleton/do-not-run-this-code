package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import android.content.Context;

public class Teleporter extends Gravitable implements TopDrawable, Moveable, Warpable
{
	private static final int TRANSPORTER_RADIUS = 70;
	private static final float TRANSPORTER_DENSITY = 0.7f;
	
	protected final Coord destination;
	
	protected int rotation = 0;
	
	protected final RectMesh teleporter;
	
	public Teleporter(Context context, Coord coord, Coord destination)
	{
		super(context, coord, 0.98f, TRANSPORTER_DENSITY, TRANSPORTER_RADIUS / 2);
		this.destination = destination;
		teleporter = new RectMesh((float)pos.x, (float)pos.y, radius * 2, radius * 2, R.drawable.teleporter);
	}

	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		// c.drawCircle((float)pos.x, (float)pos.y, radius, PaintLoader.load(BG_COL));
		// TODO: Re-implement!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!1
	}

	@Override
	public void drawTop(GL10 gl, float worldZoom)
	{
		teleporter.setRotation(rotation);
		teleporter.draw(gl);
	}
	
	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		BallData data = super.calculateVelocity(itemC, itemVC, itemRadius);

		double currDist = pos.minus(itemC).getLength();
		if(currDist < 10f * ITEM_SCALE) // Start teleport
		{
			data.itemC.copyFrom(destination);
			data.itemVC.reset();
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
