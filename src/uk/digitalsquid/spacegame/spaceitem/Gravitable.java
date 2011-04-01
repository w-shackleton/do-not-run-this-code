package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import android.content.Context;

public abstract class Gravitable extends Spherical implements Forceful
{
	protected final float AIR_RESISTANCE;
	public float density;
	
	public Gravitable(Context context, Coord coord, float internalResistance, float density, float radius)
	{
		super(context, coord, radius);
		AIR_RESISTANCE = internalResistance;
		this.density = density;
	}
	
	private final Coord tmpRF = new Coord();
	
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		tmpRF.x = CompuFuncs.computeForceX(
				pos.x,
				pos.y,
				density,
				radius,
				itemC.x,
				itemC.y);
		tmpRF.y = CompuFuncs.computeForceY(
				pos.x,
				pos.y,
				density,
				radius,
				itemC.x,
				itemC.y);
		return tmpRF;
	}
	
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		if(Coord.getLength(pos, itemC) < radius + itemRadius)
			return new BallData(null, itemVC.scale(AIR_RESISTANCE));
		return null;
	}
}
