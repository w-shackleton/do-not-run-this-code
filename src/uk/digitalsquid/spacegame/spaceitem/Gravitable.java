package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import android.content.Context;

public abstract class Gravitable extends Spherical implements Forceful
{
	protected final float internalResistance;
	public float density;
	
	public Gravitable(Context context, Coord coord, float internalResistance, float density, float radius)
	{
		super(context, coord, radius);
		this.internalResistance = internalResistance;
		this.density = density;
	}
	
	private final Coord tmpRF = new Coord();
	
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		CompuFuncs.computeForce(tmpRF, pos, density, radius, itemC);
		return tmpRF;
	}
	
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		if(Coord.getLength(pos, itemC) < radius + itemRadius)
			return new BallData(null, itemVC.scale(internalResistance));
		return null;
	}
}
