package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.assistors.SoundManager;
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
		double currDist = Coord.getLength(pos, itemC);
		if(currDist < radius + itemRadius)
		{
			newBall.itemC = new Coord(
					pos.x + ((radius + itemRadius) * (itemC.x - pos.x) / currDist),
					pos.y + ((radius + itemRadius) * (itemC.y - pos.y) / currDist));
			double angleAt = Math.atan2(itemC.y - pos.y, itemC.x - pos.x); // Angle to planet
			double angleVel = Math.atan2(-newBall.itemVC.y, -newBall.itemVC.x); // Angle from velocity
			double angleNeeded = (2 * angleAt) - angleVel;
			
			double speed = newBall.itemVC.getLength();
			
			// TODO: Check this code is correct. What about 1.2 bounciness?
			double bouncinessX = Math.abs(Math.sin(angleAt) * (1 - bounciness) + bounciness);
			double bouncinessY = Math.abs(Math.cos(angleAt) * (1 - bounciness) + bounciness);
			newBall.itemVC = new Coord(Math.cos(angleNeeded) * speed * bouncinessX, Math.sin(angleNeeded) * speed * bouncinessY);
			// Log.v("SpaceGame", "" + speed * bouncinessX + ", " + speed * bouncinessY);
			// TODO: Fix bouncing thing at bounciness 1.
			if(speed < 8) {
				newBall.itemVC.scaleThis(0.5f);
			}
			if(newBall.itemVC.getLength() < 2) {
				newBall.stopBall = true;
			}
			BounceVibrate.Vibrate((long) (itemVC.getLength() / ITEM_SCALE));
				SoundManager.get().playSound(SoundManager.SOUND_BOUNCE, (float) (itemVC.getLength() / ITEM_SCALE / 30));
		}
		return newBall;
	}
}
