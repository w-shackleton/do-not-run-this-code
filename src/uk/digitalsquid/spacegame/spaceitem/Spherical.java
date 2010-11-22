package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.interfaces.IsClickable;
import android.content.Context;

public abstract class Spherical extends SpaceItem implements IsClickable
{
	protected float radius;
	
	public Spherical(Context context, Coord coord, float radius)
	{
		super(context, coord);
		this.radius = radius * ITEM_SCALE;
	}
	
	@Override
	public boolean isClicked(Coord point)
	{
		return pos.minus(point).getLength() < radius;
	}
}
