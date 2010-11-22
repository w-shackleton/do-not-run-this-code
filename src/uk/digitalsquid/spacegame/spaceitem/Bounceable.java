package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import android.content.Context;

public abstract class Bounceable extends Gravitable
{
	protected float bounciness;
	
	public Bounceable(Context context, Coord coord, float density, float radius, float bounciness)
	{
		super(context, coord, 1, density, radius);
		this.bounciness = bounciness;
	}
	
	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		BallData newBall = super.calculateVelocity(itemC, itemVC, itemRadius);
		double currDist = pos.minus(itemC).getLength();
		if(currDist < radius + itemRadius)
		{
			newBall.itemC = new Coord(
					pos.x + ((radius + itemRadius) * (itemC.x - pos.x) / currDist),
					pos.y + ((radius + itemRadius) * (itemC.y - pos.y) / currDist));
			double angleAt = Math.atan2(itemC.y - pos.y, itemC.x - pos.x); // Angle to planet
			double angleVel = Math.atan2(-newBall.itemVC.y, -newBall.itemVC.x); // Angle from velocity
			double angleNeeded = (2 * angleAt) - angleVel;
			
			double speed = newBall.itemVC.getLength();
			newBall.itemVC = new Coord(Math.cos(angleNeeded) * speed * bounciness, Math.sin(angleNeeded) * speed * bounciness);
			//Log.v("SpaceGame", "" + newBall.itemVC.getLength());
			// TODO: Fix bouncing thing at bounciness 1.
			if(newBall.itemVC.getLength() < 4)
			{
				newBall.stopBall = true;
			}
			BounceVibrate.Vibrate((long) (itemVC.getLength() / ITEM_SCALE));
		}
		return newBall;
	}
}
