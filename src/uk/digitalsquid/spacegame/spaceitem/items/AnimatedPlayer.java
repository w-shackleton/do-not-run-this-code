package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class AnimatedPlayer extends Player
{
	protected static final Random random = new Random();

	protected Coord eyePos = new Coord(0, 0);
	protected Coord eyeRotatedPos;
	
	/**
	 * The amount that the eye can move in its socket
	 */
	protected static final int EYE_MOVE_AMOUNT = 3;
	
	/**
	 * The position that the eye is aiming to be at (for animation)
	 */
	protected Coord eyeMoveTo = new Coord(0, 0);
	
	/**
	 * The point in the game where the eyes should be looking
	 */
	protected Coord eyeMoveToOnGame = new Coord(0, 0);
	
	/**
	 * Distance between me and focus point
	 */
	protected Coord lookToDistance = new Coord();
	
	/**
	 * How quickly the distance affects the eye position
	 */
	protected static final int LOOKTO_DISTANCE_AFFECTOR = 150;
	
	protected Drawable ball, eye, eyeinside, eyeblinking, leftEar, rightEar;
	
	/**
	 * Amount of time left blinking
	 */
	protected int blinkTimeLeft = 0;
	
	/**
	 * Amount of time to blink for
	 */
	protected static final int BLINK_TIME = 3;
	
	/**
	 * Speed to move the eyes at. Higher is slower.
	 */
	protected static final float EYE_MOVE_SPEED = 70;
	
	protected static final Coord lEar = new Coord(-7 * ITEM_SCALE, -4 * ITEM_SCALE);
	protected static final Coord rEar = new Coord(-7 * ITEM_SCALE, 4 * ITEM_SCALE);
	protected static final Coord EAR_SIZE = new Coord(20 * ITEM_SCALE, 10 * ITEM_SCALE);
	protected static final float EAR_RESTING_POSITION = 30;
	protected static final Coord EAR_ROTATING_AXIS = new Coord(2 * ITEM_SCALE, 5 * ITEM_SCALE);
	protected float lEarRotation = 0;
	protected float rEarRotation = 0;
	
	protected static final Coord lEye = new Coord(-6 * ITEM_SCALE, -7 * ITEM_SCALE);
	protected static final Coord rEye = new Coord(-6 * ITEM_SCALE, 7 * ITEM_SCALE);
	
	public AnimatedPlayer(Context context, Coord coord, Coord velocity)
	{
		super(context, coord, BALL_RADIUS);
		ball = context.getResources().getDrawable(R.drawable.ball);
		eye = context.getResources().getDrawable(R.drawable.eye);
		eyeblinking = context.getResources().getDrawable(R.drawable.eyeblinking);
		eyeinside = context.getResources().getDrawable(R.drawable.eyeinside);
		leftEar = context.getResources().getDrawable(R.drawable.earleft);
		rightEar = context.getResources().getDrawable(R.drawable.earright);
		
		itemC = pos; // Referenced?
		itemVC = velocity;
		itemRF = new Coord();
		eP.setARGB(255, 255, 255, 255);
		eP.setStrokeWidth(4);
		
		lookTo(new Coord(0, 0));
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		// Calculation steps...
		
		eyeMoveToOnGame.minusInto(itemC, lookToDistance);
		
		float lookLength = (float) lookToDistance.getLength();
		float lookAngle = lookToDistance.getRotation() * DEG_TO_RAD;
		
		lookLength = CompuFuncs.TrimMinMax(lookLength, -LOOKTO_DISTANCE_AFFECTOR, LOOKTO_DISTANCE_AFFECTOR);
		lookLength *= (float)EYE_MOVE_AMOUNT / (float)LOOKTO_DISTANCE_AFFECTOR;

		//float eyeMovePosX = (float) ((Math.abs(lookToDistance.x) < LOOKTO_DISTANCE_AFFECTOR) ? (float) (lookToDistance.x / LOOKTO_DISTANCE_AFFECTOR * EYE_MOVE_AMOUNT) : EYE_MOVE_AMOUNT * Math.signum(lookToDistance.x));
		//float eyeMovePosY = (float) ((Math.abs(lookToDistance.y) < LOOKTO_DISTANCE_AFFECTOR) ? (float) (lookToDistance.y / LOOKTO_DISTANCE_AFFECTOR * EYE_MOVE_AMOUNT) : EYE_MOVE_AMOUNT * Math.signum(lookToDistance.y));
		eyeMoveTo.x = Math.cos(lookAngle) * lookLength; // Reapply angle
		eyeMoveTo.y = Math.sin(lookAngle) * lookLength;
		
		Log.v("SpaceGame", "X: " + eyeMoveTo.x + ", Y: " + eyeMoveTo.y);
		
		//eyeMoveTo.x = eyeMovePosX; // CompuFuncs.RotateX(eyeMovePosX, eyeMovePosY, ballRotation * DEG_TO_RAD);
		//eyeMoveTo.y = eyeMovePosY; //CompuFuncs.RotateY(eyeMovePosX, eyeMovePosY, ballRotation * DEG_TO_RAD);
		
		Coord eyeDistanceToMove = eyeMoveTo.minus(eyePos);
		eyePos.x += eyeDistanceToMove.x / EYE_MOVE_SPEED;
		eyePos.y += eyeDistanceToMove.y / EYE_MOVE_SPEED;
		
		eyeRotatedPos = eyeMoveTo.rotate(null, -ballRotation * DEG_TO_RAD);
		
		// Draw
		
		c.rotate(ballRotation, (float)itemC.x, (float)itemC.y);
		ball.setBounds(new Rect(
				(int)((itemC.x - AnimatedPlayer.BALL_RADIUS)),
				(int)((itemC.y - AnimatedPlayer.BALL_RADIUS)),
				(int)((itemC.x + AnimatedPlayer.BALL_RADIUS)),
				(int)((itemC.y + AnimatedPlayer.BALL_RADIUS))
				));
		ball.draw(c);
		Coord earPos = itemC.add(lEar);
		Rect earPosRect = new Rect((int)(earPos.x - EAR_SIZE.x), (int)(earPos.y - EAR_SIZE.y), (int)earPos.x, (int)earPos.y);
		leftEar.setBounds(earPosRect);
		leftEar.draw(c);
		earPos = itemC.add(rEar);
		earPosRect = new Rect((int)(earPos.x - EAR_SIZE.x), (int)earPos.y, (int)earPos.x, (int)(earPos.y + EAR_SIZE.y));
		rightEar.setBounds(earPosRect);
		rightEar.draw(c);
		
		if(random.nextInt(1000) == 42) blinkTimeLeft = BLINK_TIME;
		if(blinkTimeLeft-- > 0) // Blinking
		{
			Coord eyeSPos = itemC.add(lEye);
			Rect eyePosRect = new Rect((int) ((eyeSPos.x - EYE_RADIUS)),
					(int) ((eyeSPos.y - EYE_RADIUS)),
					(int) ((eyeSPos.x + EYE_RADIUS)),
					(int) ((eyeSPos.y + EYE_RADIUS)));
			eyeblinking.setBounds(eyePosRect);
			eyeblinking.draw(c);

			eyeSPos = itemC.add(rEye);
			eyePosRect = new Rect((int) ((eyeSPos.x - EYE_RADIUS)),
					(int) ((eyeSPos.y - EYE_RADIUS)),
					(int) ((eyeSPos.x + EYE_RADIUS)),
					(int) ((eyeSPos.y + EYE_RADIUS)));
			eyeblinking.setBounds(eyePosRect);
			eyeblinking.draw(c);
		}
		else
		{
			Coord eyeSPos = itemC.add(lEye);
			Rect eyePosRect = new Rect((int) ((eyeSPos.x - EYE_RADIUS)),
					(int) ((eyeSPos.y - EYE_RADIUS)),
					(int) ((eyeSPos.x + EYE_RADIUS)),
					(int) ((eyeSPos.y + EYE_RADIUS)));
			eye.setBounds(eyePosRect);
			eye.draw(c);
			
			eyeSPos.x += eyeRotatedPos.x;
			eyeSPos.y += eyeRotatedPos.y;
			eyePosRect.left = (int) (eyeSPos.x - EYE_RADIUS);
			eyePosRect.top = (int) (eyeSPos.y - EYE_RADIUS);
			eyePosRect.right = (int)(eyeSPos.x + EYE_RADIUS);
			eyePosRect.bottom = (int)(eyeSPos.y + EYE_RADIUS);
			
			eyeinside.setBounds(eyePosRect);
			eyeinside.draw(c);

			eyeSPos = itemC.add(rEye);
			
			eyePosRect.left = (int) (eyeSPos.x - EYE_RADIUS);
			eyePosRect.top = (int) (eyeSPos.y - EYE_RADIUS);
			eyePosRect.right = (int)(eyeSPos.x + EYE_RADIUS);
			eyePosRect.bottom = (int)(eyeSPos.y + EYE_RADIUS);
			
			eye.setBounds(eyePosRect);
			eye.draw(c);
			
			eyeSPos.x += eyeRotatedPos.x;
			eyeSPos.y += eyeRotatedPos.y;
			eyePosRect.left = (int) (eyeSPos.x - EYE_RADIUS);
			eyePosRect.top = (int) (eyeSPos.y - EYE_RADIUS);
			eyePosRect.right = (int)(eyeSPos.x + EYE_RADIUS);
			eyePosRect.bottom = (int)(eyeSPos.y + EYE_RADIUS);
			
			eyeinside.setBounds(eyePosRect);
			eyeinside.draw(c);
		}
		
		c.rotate(-ballRotation, (float)itemC.x, (float)itemC.y);
	}
	
	/**
	 * Makes the character 'look' towards a certain point. This makes the game seem interactive etc...
	 * @param point A {@link Coord} in the game (NOT on the screen). Points on the screen need to be put through the reverse matrix first.
	 */
	public void lookTo(Coord point)
	{
		eyeMoveToOnGame = point;
	}
}
