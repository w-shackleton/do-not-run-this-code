package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

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
	private final Rect ballRect = new Rect();
	
	/**
	 * Amount of time left blinking
	 */
	private int blinkTimeLeft = 0;
	
	/**
	 * Amount of time to blink for
	 */
	private static final int BLINK_TIME = 3;
	
	/**
	 * Speed to move the eyes at. Higher is slower.
	 */
	private static final float EYE_MOVE_SPEED = 70;
	
	private static final Coord lEar = new Coord(-7 * ITEM_SCALE, -4 * ITEM_SCALE);
	private static final Coord rEar = new Coord(-7 * ITEM_SCALE, 4 * ITEM_SCALE);
	private static final Coord EAR_SIZE = new Coord(20 * ITEM_SCALE, 10 * ITEM_SCALE);
	private static final float LEFT_EAR_RESTING_POSITION = 20;
	private static final float RIGHT_EAR_RESTING_POSITION = -20;
	private static final float EAR_ROTATING_AIR_RESISTANCE = 0.996f;
	private static final float EAR_ROTATING_SPEED = 15;
	private float lEarRotation = 30;
	private float rEarRotation = -30;
	private float lEarRotationSpeed = 0;
	private float rEarRotationSpeed = 0;
	
	protected static final Coord lEye = new Coord(-6 * ITEM_SCALE, -7 * ITEM_SCALE);
	protected static final Coord rEye = new Coord(-6 * ITEM_SCALE, 7 * ITEM_SCALE);
	
	private final Coord eyeSPos = new Coord();
	
	public AnimatedPlayer(Context context, Coord coord, Coord velocity)
	{
		super(context, coord, BALL_RADIUS);
		ball = context.getResources().getDrawable(R.drawable.ball);
		eye = context.getResources().getDrawable(R.drawable.eye);
		eyeblinking = context.getResources().getDrawable(R.drawable.eyeblinking);
		eyeinside = context.getResources().getDrawable(R.drawable.eyeinside);
		leftEar = context.getResources().getDrawable(R.drawable.earleft);
		rightEar = context.getResources().getDrawable(R.drawable.earright);
		
		itemC.copyFrom(pos); // Referenced?
		itemVC.copyFrom(velocity);
		itemRF.reset();
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
		
		// Log.v("SpaceGame", "X: " + eyeMoveTo.x + ", Y: " + eyeMoveTo.y);
		
		//eyeMoveTo.x = eyeMovePosX; // CompuFuncs.RotateX(eyeMovePosX, eyeMovePosY, ballRotation * DEG_TO_RAD);
		//eyeMoveTo.y = eyeMovePosY; //CompuFuncs.RotateY(eyeMovePosX, eyeMovePosY, ballRotation * DEG_TO_RAD);
		
		Coord eyeDistanceToMove = eyeMoveTo.minus(eyePos);
		eyePos.x += eyeDistanceToMove.x / EYE_MOVE_SPEED;
		eyePos.y += eyeDistanceToMove.y / EYE_MOVE_SPEED;
		
		eyeRotatedPos = eyeMoveTo.rotate(null, -ballRotation * DEG_TO_RAD);
		
		// Draw
		
		c.rotate(ballRotation, (float)itemC.x, (float)itemC.y);
		
		ballRect.left = (int)((itemC.x - AnimatedPlayer.BALL_RADIUS));
		ballRect.top = (int)((itemC.y - AnimatedPlayer.BALL_RADIUS));
		ballRect.right = (int)((itemC.x + AnimatedPlayer.BALL_RADIUS));
		ballRect.bottom = (int)((itemC.y + AnimatedPlayer.BALL_RADIUS));
		
		ball.setBounds(ballRect);
		ball.draw(c);
		Coord earPos = itemC.add(lEar);
		c.rotate((float)lEarRotation, (float)earPos.x, (float)earPos.y);
		leftEar.setBounds((int)(earPos.x - EAR_SIZE.x), (int)(earPos.y - EAR_SIZE.y / 2), (int)earPos.x, (int)(earPos.y + EAR_SIZE.y / 2));
		leftEar.draw(c);
		c.rotate((float)-lEarRotation, (float)earPos.x, (float)earPos.y);
		
		earPos.copyFrom(itemC);
		earPos.addThis(rEar);
		c.rotate((float)rEarRotation, (float)earPos.x, (float)earPos.y);
		rightEar.setBounds((int)(earPos.x - EAR_SIZE.x), (int)(earPos.y - EAR_SIZE.y / 2), (int)earPos.x, (int)(earPos.y + EAR_SIZE.y / 2));
		rightEar.draw(c);
		c.rotate((float)-rEarRotation, (float)earPos.x, (float)earPos.y);
		
		if(random.nextInt(1000) == 42) blinkTimeLeft = BLINK_TIME;
		if(blinkTimeLeft-- > 0) // Blinking
		{
			eyeSPos.x = itemC.x + lEye.x;
			eyeSPos.y = itemC.y + lEye.y;
			eyeblinking.setBounds((int) ((eyeSPos.x - EYE_RADIUS)), (int) ((eyeSPos.y - EYE_RADIUS)), (int) ((eyeSPos.x + EYE_RADIUS)), (int) ((eyeSPos.y + EYE_RADIUS)));
			eyeblinking.draw(c);

			eyeSPos.x = itemC.x + rEye.x;
			eyeSPos.y = itemC.y + rEye.y;
			eyeblinking.setBounds((int) ((eyeSPos.x - EYE_RADIUS)), (int) ((eyeSPos.y - EYE_RADIUS)), (int) ((eyeSPos.x + EYE_RADIUS)), (int) ((eyeSPos.y + EYE_RADIUS)));
			eyeblinking.draw(c);
		}
		else
		{
			eyeSPos.x = itemC.x + lEye.x;
			eyeSPos.y = itemC.y + lEye.y;
			eye.setBounds((int) ((eyeSPos.x - EYE_RADIUS)), (int) ((eyeSPos.y - EYE_RADIUS)), (int) ((eyeSPos.x + EYE_RADIUS)), (int) ((eyeSPos.y + EYE_RADIUS)));
			eye.draw(c);
			
			eyeSPos.x += eyeRotatedPos.x;
			eyeSPos.y += eyeRotatedPos.y;
			
			eyeinside.setBounds((int) ((eyeSPos.x - EYE_RADIUS)), (int) ((eyeSPos.y - EYE_RADIUS)), (int) ((eyeSPos.x + EYE_RADIUS)), (int) ((eyeSPos.y + EYE_RADIUS)));
			eyeinside.draw(c);

			eyeSPos.x = itemC.x + rEye.x;
			eyeSPos.y = itemC.y + rEye.y;
			
			eye.setBounds((int) ((eyeSPos.x - EYE_RADIUS)), (int) ((eyeSPos.y - EYE_RADIUS)), (int) ((eyeSPos.x + EYE_RADIUS)), (int) ((eyeSPos.y + EYE_RADIUS)));
			eye.draw(c);
			
			eyeSPos.x += eyeRotatedPos.x;
			eyeSPos.y += eyeRotatedPos.y;
			
			eyeinside.setBounds((int) ((eyeSPos.x - EYE_RADIUS)), (int) ((eyeSPos.y - EYE_RADIUS)), (int) ((eyeSPos.x + EYE_RADIUS)), (int) ((eyeSPos.y + EYE_RADIUS)));
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
	
	@Override
	public void move(float millistep, float speedScale)
	{
		super.move(millistep, speedScale);
		
		double leftEarFullRotation = ballRotation + lEarRotation;
		double rightEarFullRotation = ballRotation + rEarRotation;
		
		double leftEarExternalForce  = CompuFuncs.RotateY(itemRF.x, itemRF.y, (180-leftEarFullRotation) / 180 * Math.PI);
		double rightEarExternalForce = CompuFuncs.RotateY(itemRF.x, itemRF.y, (180-rightEarFullRotation) / 180 * Math.PI);
		
		double leftEarForce = LEFT_EAR_RESTING_POSITION - lEarRotation + leftEarExternalForce / 10;
		
		lEarRotationSpeed += leftEarForce * millistep / ITERS / 1000f;
		lEarRotationSpeed *= EAR_ROTATING_AIR_RESISTANCE;
		lEarRotation += lEarRotationSpeed * millistep / ITERS / 1000f * speedScale * EAR_ROTATING_SPEED;
		
		double rightEarForce = RIGHT_EAR_RESTING_POSITION - rEarRotation + rightEarExternalForce / 10;
		
		rEarRotationSpeed += rightEarForce * millistep / ITERS / 1000f;
		rEarRotationSpeed *= EAR_ROTATING_AIR_RESISTANCE;
		rEarRotation += rEarRotationSpeed * millistep / ITERS / 1000f * speedScale * EAR_ROTATING_SPEED;
	}
}
