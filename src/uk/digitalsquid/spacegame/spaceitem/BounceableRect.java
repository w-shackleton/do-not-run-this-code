package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.BounceVibrate;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import android.content.Context;

public abstract class BounceableRect extends Rectangular implements Forceful
{
	protected float bounciness;
	protected double a, B, c;
	protected Coord abcC = new Coord();
	
	public BounceableRect(Context context, Coord coord, Coord size, float rotation, float bounciness)
	{
		super(context, coord, size, rotation);
		this.bounciness = bounciness;
	}
	
	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		// TODO: Stop sinking through floor bug (help...)
		double l = pos.x - (size.x / 2);
		double t = pos.y - (size.y / 2);
		double r = pos.x + (size.x / 2);
		double b = pos.y + (size.y / 2);

		//Log.v("SpaceGame", "ping...");
		Coord newItemVC = itemVC.rotate(null, -rotation * DEG_TO_RAD); // Now it is relative to the grid aligned rectangle.
		Coord itemRotC = itemC.rotate(pos, -rotation * DEG_TO_RAD);

		Coord newItemC = null;
		
		if((itemRotC.x + itemRadius > l && itemRotC.x - itemRadius < r) && (itemRotC.y > t && itemRotC.y < b))
		{
			newItemVC.x = -newItemVC.x * bounciness;
			// Check for ball sinking through rect
			if(itemRotC.x + itemRadius > l && itemRotC.x + itemRadius < pos.x)
			{
				newItemC = new Coord(itemRotC);
				newItemC.x = l - itemRadius;
			}
			if(itemRotC.x - itemRadius < r && itemRotC.x - itemRadius > pos.x)
			{
				newItemC = new Coord(itemRotC);
				newItemC.x = r + itemRadius;
			}
			BounceVibrate.Vibrate((long) (newItemVC.getLength() / ITEM_SCALE * 1.5));
		}
		else if((itemRotC.y + itemRadius > t && itemRotC.y - itemRadius < b) && (itemRotC.x > l && itemRotC.x < r))
		{
			newItemVC.y = -newItemVC.y * bounciness;
			// Check for ball sinking through rect
			if(itemRotC.y + itemRadius > t && itemRotC.y + itemRadius < pos.y)
			{
				newItemC = new Coord(itemRotC);
				newItemC.y = t - itemRadius;
			}
			if(itemRotC.y - itemRadius < b && itemRotC.y - itemRadius > pos.y)
			{
				newItemC = new Coord(itemRotC);
				newItemC.y = b + itemRadius;
			}
			BounceVibrate.Vibrate((long) (newItemVC.getLength() / ITEM_SCALE * 1.5));
		}
		
		Coord corners[] = {
				new Coord(l, t),
				new Coord(r, t),
				new Coord(l, b),
				new Coord(r, b),
				};
		for(int i = 0; i < 4; i++)
		{
			double currDist = Coord.getLength(itemRotC, corners[i]);
			if(currDist < itemRadius)
			{
				newItemC = new Coord();
				newItemC.x = corners[i].x + (itemRadius * (itemRotC.x - corners[i].x) / currDist);
				newItemC.y = corners[i].y + (itemRadius * (itemRotC.y - corners[i].y) / currDist);
				double angleAt = Math.atan2(itemRotC.y - corners[i].y, itemRotC.x - corners[i].x); // Angle to planet
				double angleVel = Math.atan2(-newItemVC.y, -newItemVC.x); // Angle from velocity
				double angleNeeded = (2 * angleAt) - angleVel;
				a = angleAt;
				B = angleVel;
				c = angleNeeded;
				abcC.reset();
				/*Log.v("SpaceGame", "angleAt: " + (angleAt * RAD_TO_DEG));
				Log.v("SpaceGame", "angleVel: " + (angleVel * RAD_TO_DEG));
				Log.v("SpaceGame", "angleNeeded: " + (angleNeeded * RAD_TO_DEG));
				Log.v("SpaceGame", " ");*/
				
				double speed = newItemVC.getLength();
				newItemVC.x = Math.cos(angleNeeded) * speed * bounciness;
				newItemVC.y = Math.sin(angleNeeded) * speed * bounciness;
				BounceVibrate.Vibrate((long) (newItemVC.getLength() / ITEM_SCALE * 1.5));
				break;
			}
		}
		if(newItemC != null) newItemC = newItemC.rotate(pos, rotation * DEG_TO_RAD);
		BallData data = new BallData(newItemC, newItemVC.rotate(null, rotation * DEG_TO_RAD)); 
		
		return data;
	}
	
	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		return null;
	}
}
