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
	
	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		CompuFuncs.computeForce(tmpRF, pos, density, radius, itemC);
		return tmpRF;
	}
	
	@Override
	public BallData calculateVelocityImmutable(Coord itemC, Coord itemVC, float itemRadius, boolean testRun)
	{
		if(Coord.getLength(pos, itemC) < radius + itemRadius)
			return new BallData(null, itemVC.scale(radius == 0 ? 1 : internalResistance));
		return null;
	}
	
	@Override
	public void calculateVelocityMutable(Coord itemC, Coord itemVC, float itemRadius) { }
}
