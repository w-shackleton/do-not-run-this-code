package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.content.Context;

public class AnimatedPlayer extends Player
{
	protected static final Random random = new Random();

	protected Coord eyePos = new Coord(0, 0);
	protected Coord eyeRotatedPos = new Coord();
	
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
	
	protected final RectMesh ball, leftEar, rightEar;
	protected final RectMesh leftEye, leftEyeinside, leftEyeblinking;
	protected final RectMesh rightEye, rightEyeinside, rightEyeblinking;
	
	protected final RectMesh landingGearLeft, landingGearRight;
	
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
	private static final float EAR_ROTATING_AIR_RESISTANCE = 0.993f;
	private static final float EAR_ROTATING_SPEED = 15;
	private float lEarRotation = 30;
	private float rEarRotation = -30;
	private float lEarRotationSpeed = 0;
	private float rEarRotationSpeed = 0;
	
	protected static final Coord lEye = new Coord(-6 * ITEM_SCALE, -7 * ITEM_SCALE);
	protected static final Coord rEye = new Coord(-6 * ITEM_SCALE, 7 * ITEM_SCALE);
	
	private static final Coord LANDING_GEAR_SIZE = new Coord(40 * ITEM_SCALE, 20 * ITEM_SCALE);
	
	/**
	 * Left gear has to be negative
	 */
	private static final int LANDING_GEAR_CLOSED_ROTATION = 120;
	
	/**
	 * Left gear has to be negative
	 */
	private static final int LANDING_GEAR_OPEN_ROTATION = 0;
	
	public AnimatedPlayer(Context context, Coord coord, Coord velocity)
	{
		super(context, coord, BALL_RADIUS);
		ball = new RectMesh(0, 0, BALL_RADIUS * 2, BALL_RADIUS * 2, R.drawable.ball);
		
		leftEye = new RectMesh((float)lEye.x, (float)lEye.y, EYE_RADIUS * 2, EYE_RADIUS * 2, R.drawable.eye);
		leftEyeblinking = new RectMesh((float)lEye.x, (float)lEye.y, EYE_RADIUS * 2, EYE_RADIUS * 2, R.drawable.eyeblinking);
		leftEyeinside = new RectMesh((float)lEye.x, (float)lEye.y, EYE_RADIUS * 2, EYE_RADIUS * 2, R.drawable.eyeinside);
		rightEye = new RectMesh((float)rEye.x, (float)rEye.y, EYE_RADIUS * 2, EYE_RADIUS * 2, R.drawable.eye);
		rightEyeblinking = new RectMesh((float)rEye.x, (float)rEye.y, EYE_RADIUS * 2, EYE_RADIUS * 2, R.drawable.eyeblinking);
		rightEyeinside = new RectMesh((float)rEye.x, (float)rEye.y, EYE_RADIUS * 2, EYE_RADIUS * 2, R.drawable.eyeinside);
		
		leftEar  = new RectMesh(- (float)EAR_SIZE.x / 2, 0, (float)EAR_SIZE.x, (float)EAR_SIZE.y, R.drawable.earleft);
		rightEar = new RectMesh(- (float)EAR_SIZE.x / 2, 0, (float)EAR_SIZE.x, (float)EAR_SIZE.y, R.drawable.earright);
		
		landingGearLeft  = new RectMesh((float)LANDING_GEAR_SIZE.x / 3, 0, (float)LANDING_GEAR_SIZE.x, (float)LANDING_GEAR_SIZE.y, 0f, -0.5f, R.drawable.landing_gear_half_left, true);
		landingGearRight  = new RectMesh((float)LANDING_GEAR_SIZE.x / 3, 0, (float)LANDING_GEAR_SIZE.x, (float)LANDING_GEAR_SIZE.y, 0f, 0.5f, R.drawable.landing_gear_half_right, true);
		landingGearLeft.setRotation(-LANDING_GEAR_CLOSED_ROTATION);
		landingGearRight.setRotation(LANDING_GEAR_CLOSED_ROTATION);
		
		itemC.copyFrom(pos); // Referenced?
		itemVC.copyFrom(velocity);
		itemRF.reset();
		
		lookTo(new Coord(0, 0));
		closeLanding();
	}
	
	@Override
	public void drawPlayer(GL10 gl, float worldZoom)
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
		
		eyeRotatedPos.copyFrom(eyeMoveTo);
		eyeRotatedPos.rotateThis(null, -ballRotation * DEG_TO_RAD);
		
		// Draw
		// Landing gear
		
		{
			gl.glPushMatrix();
			float transitionFactor = (float)(landingPosition - LANDING_GEAR_OPEN_ROTATION) / (float)(LANDING_GEAR_CLOSED_ROTATION - LANDING_GEAR_OPEN_ROTATION);
			gl.glRotatef(Coord.getRotationFrom(itemC, nearestPlanet) * (1 - transitionFactor) + ballRotation * transitionFactor, 0, 0, 1);
			gl.glTranslatef(-landingDrawShiftX, 0, 0);
			landingGearLeft.draw(gl);
			landingGearRight.draw(gl);
			gl.glPopMatrix();
		}
		
		
		gl.glPushMatrix();
		gl.glRotatef(ballRotation, 0, 0, 1);
		gl.glTranslatef(-landingDrawShiftX, 0, 0);
		
		ball.draw(gl);
		
		gl.glPushMatrix();
		gl.glTranslatef((float)lEar.x, (float)lEar.y, 0);
		gl.glRotatef((float)lEarRotation, 0, 0, 1);
		leftEar.draw(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef((float)rEar.x, (float)rEar.y, 0);
		gl.glRotatef((float)rEarRotation, 0, 0, 1);
		rightEar.draw(gl);
		gl.glPopMatrix();
		
		if(random.nextInt(1000) == 42) blinkTimeLeft = BLINK_TIME;
		if(blinkTimeLeft-- > 0) // Blinking
		{
			leftEyeblinking.draw(gl);

			rightEyeblinking.draw(gl);
		}
		else
		{
			leftEye.draw(gl);
			rightEye.draw(gl);
			
			gl.glPushMatrix();
			gl.glTranslatef((float)eyeRotatedPos.x, (float)eyeRotatedPos.y, 0);
			
			leftEyeinside.draw(gl);
			rightEyeinside.draw(gl);
			
			gl.glPopMatrix();
		}
		
		gl.glPopMatrix();
	}
	
	@Override
	public void lookTo(Coord point)
	{
		eyeMoveToOnGame = point;
	}
	
	private float landingDestinationPos = 0;
	
	@Override
	public final void openLanding() {
		if(landingDestinationPos != LANDING_GEAR_OPEN_ROTATION)
			moveLandingTo(LANDING_GEAR_OPEN_ROTATION, LANDING_DRAW_SHIFT_TOTAL);
		landingDestinationPos = LANDING_GEAR_OPEN_ROTATION;
	}
	
	@Override
	public final void closeLanding() {
		if(landingDestinationPos != LANDING_GEAR_CLOSED_ROTATION)
			moveLandingTo(LANDING_GEAR_CLOSED_ROTATION, 0);
		landingDestinationPos = LANDING_GEAR_CLOSED_ROTATION;
	}
	
	private static final float LANDING_MOVE_ANIMATION_STEP = (float) (Math.PI * 0.004);
	private static final int LANDING_DRAW_SHIFT_TOTAL = 14;
	
	private float landingAnimationMidPoint = 0;
	private float landingAnimationScale = 0;
	private float landingAnimationShiftMidPoint = 0;
	private float landingAnimationShiftScale = 0;
	private float landingAnimation = (float) Math.PI;
	private float landingPosition = LANDING_GEAR_CLOSED_ROTATION;
	private float landingDrawShiftX = 0;
	
	private final void moveLandingTo(int degrees, int moveTo) {
		landingAnimation = 0;
		landingAnimationScale = (degrees - landingPosition) / 2;
		landingAnimationMidPoint = (degrees + landingPosition) / 2;
		
		landingAnimationShiftMidPoint = (moveTo + landingDrawShiftX) / 2;
		landingAnimationShiftScale = (moveTo - landingDrawShiftX) / 2;
	}
	
	@Override
	public void move(float millistep, float speedScale) {
		super.move(millistep, speedScale);
		
		double leftEarFullRotation = ballRotation + lEarRotation;
		double rightEarFullRotation = ballRotation + rEarRotation;
		
		double leftEarExternalForce  = CompuFuncs.RotateY(itemRF.x, itemRF.y, (180-leftEarFullRotation) / 180 * Math.PI);
		double rightEarExternalForce = CompuFuncs.RotateY(itemRF.x, itemRF.y, (180-rightEarFullRotation) / 180 * Math.PI);
		
		double leftEarForce = LEFT_EAR_RESTING_POSITION - lEarRotation + leftEarExternalForce / 2f;
		
		lEarRotationSpeed += leftEarForce * millistep / ITERS / 1000f;
		lEarRotationSpeed *= EAR_ROTATING_AIR_RESISTANCE;
		lEarRotation += lEarRotationSpeed * millistep / ITERS / 1000f * speedScale * EAR_ROTATING_SPEED;
		lEarRotation = CompuFuncs.TrimMinMax(lEarRotation, lEarRotation - 45, lEarRotation + 45);
		
		double rightEarForce = RIGHT_EAR_RESTING_POSITION - rEarRotation + rightEarExternalForce / 2f;
		
		rEarRotationSpeed += rightEarForce * millistep / ITERS / 1000f;
		rEarRotationSpeed *= EAR_ROTATING_AIR_RESISTANCE;
		rEarRotation += rEarRotationSpeed * millistep / ITERS / 1000f * speedScale * EAR_ROTATING_SPEED;
		rEarRotation = CompuFuncs.TrimMinMax(rEarRotation, rEarRotation - 45, rEarRotation + 45);
		
		// Landing gear
		if(landingAnimation < Math.PI) {
			landingAnimation += LANDING_MOVE_ANIMATION_STEP;
			landingPosition = (float) (-Math.cos(landingAnimation /* from 0 to PI for anim */) * landingAnimationScale + landingAnimationMidPoint);
			landingGearLeft.setRotation(-landingPosition);
			landingGearRight.setRotation(landingPosition);
			
			landingDrawShiftX = (float) (-Math.cos(landingAnimation /* from 0 to PI for anim */) * landingAnimationShiftScale + landingAnimationShiftMidPoint);
		} else {
			landingAnimation = (float) Math.PI; // Pi is ending point of curve
		}
	}
	
	private Coord nearestPlanet = new Coord();
	
	@Override
	public void setNearestLandingPoint(final Coord planet) {
		if(landingAnimation >= Math.PI) { // If animation is in progress, don't set new planet pos, as it will disrupt smooth animation
			nearestPlanet = planet;
		}
	}

	@Override
	public void drawMove(float millistep, float speedscale) { }
}
