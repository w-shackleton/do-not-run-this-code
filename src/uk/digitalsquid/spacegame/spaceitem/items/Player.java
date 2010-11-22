package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.Spherical;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;
import android.graphics.Paint;

public abstract class Player extends Spherical implements Moveable
{
	public static final float BALL_RADIUS = 14 * ITEM_SCALE;
	protected static final float EYE_RADIUS = 5 * ITEM_SCALE;
	protected static final int ITERS = 5; // From PlanetaryView - not really needed.
	
	public Coord itemC, itemVC, itemRF; // For portability to old code
	
	protected float ballRotation = 0;
	protected float ballRotationSpeed = 0;
	protected float ballMomentum = 0;
	
	protected static final float BALL_ROTATION_AIR_RESISTANCE = 0.98f;
	
	
	Paint eP = new Paint();

	public Player(Context context, Coord coord, float radius)
	{
		super(context, coord, radius);
	}

	@Override
	public void move(float millistep, float speedScale)
	{
		// Calculate rotation of ball
		ballRotation = ballRotation % 360;
		float itemRFDirection = itemRF.getRotation();
		if(Math.abs(ballRotation - itemRFDirection) < 180)
			ballMomentum = itemRFDirection - ballRotation;
		else
		{
			ballRotation -= 360;
			if(Math.abs(ballRotation - itemRFDirection) < 180) // Try again
				ballMomentum = itemRFDirection - ballRotation;
			else
			{
				ballRotation += 720;
				if(Math.abs(ballRotation - itemRFDirection) < 180) // Try again!
					ballMomentum = itemRFDirection - ballRotation;
			}
		}
		
		ballRotationSpeed += ballMomentum * millistep / ITERS / 1000f;
		ballRotationSpeed *= BALL_ROTATION_AIR_RESISTANCE;
		ballRotation += ballRotationSpeed * millistep / ITERS / 1000f * speedScale;
	}
}
