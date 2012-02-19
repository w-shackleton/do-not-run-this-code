package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.RectMesh;

public class AnimatedPlayer extends Player
{
	protected static final Random random = new Random();

	protected Vec2 eyePos = new Vec2(0, 0);
	protected Vec2 eyeRotatedPos = new Vec2();
	
	/**
	 * The amount that the eye can move in its socket
	 */
	protected static final float EYE_MOVE_AMOUNT = 0.35f;
	
	/**
	 * The position that the eye is aiming to be at (for animation)
	 */
	protected Vec2 eyeMoveTo = new Vec2(0, 0);
	
	/**
	 * The point in the game where the eyes should be looking
	 */
	protected Vec2 eyeMoveToOnGame = new Vec2(0, 0);
	
	/**
	 * Distance between me and focus point
	 */
	protected Vec2 lookToDistance = new Vec2();
	
	/**
	 * How quickly the distance affects the eye position
	 */
	protected static final float LOOKTO_DISTANCE_AFFECTOR = 5f;
	
	protected final RectMesh ball, leftEar, rightEar;
	protected final RectMesh leftEye, leftEyeinside, leftEyeblinking;
	protected final RectMesh rightEye, rightEyeinside, rightEyeblinking;
	
	protected final RectMesh landingGearLeft, landingGearRight;
	
	/**
	 * Amount of time left blinking
	 */
	private int blinkTimeLeft = 0;
	
	/**
	 * The progress, from 0 to 1 of the eye roll progress
	 */
	private float eyeRollProgress = -1;
	
	/**
	 * Amount of time to blink for
	 */
	private static final int BLINK_TIME = 7;
	private static final float EYE_ROLL_TIMETAKEN = 2f; // In seconds
	
	/**
	 * Speed to move the eyes at. Higher is slower.
	 */
	private float EYE_MOVE_SPEED = 10;
	
	private static final Vec2 lEar = new Vec2(-.7f, -.4f);
	private static final Vec2 rEar = new Vec2(-.7f, .4f);
	private static final Vec2 EAR_SIZE = new Vec2(2f, 1f);
	/**
	 * The end (bobble) of the ear, relative to it's rotation point
	 */
	private static final Vec2 EAR_END = new Vec2(-1.5f, 0f);
	private static final float LEFT_EAR_RESTING_POSITION = 20;
	private static final float RIGHT_EAR_RESTING_POSITION = -20;
	private static final float EAR_ROTATING_AIR_RESISTANCE = 0.985f;
	private static final float EAR_ROTATING_SPEED = 15;
	private static final float EAR_FORCE_MULTIPLIER = 1f;
	private static final float EAR_FORCE_PREMULTIPLIER = 1f;
	private float lEarRotation = 30;
	private float rEarRotation = -30;
	private float lEarRotationSpeed = 0;
	private float rEarRotationSpeed = 0;
	
	protected static final Vec2 lEye = new Vec2(-0.6f, -0.7f);
	protected static final Vec2 rEye = new Vec2(-0.6f, 0.7f);
	
	private static final Vec2 LANDING_GEAR_SIZE = new Vec2(4f, 2f);
	
	/**
	 * Left gear has to be negative
	 */
	private static final int LANDING_GEAR_CLOSED_ROTATION = 120;
	
	/**
	 * Left gear has to be negative
	 */
	private static final int LANDING_GEAR_OPEN_ROTATION = 0;
	
	public AnimatedPlayer(SimulationContext context, Vec2 coord, Vec2 velocity)
	{
		super(context, coord);
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
		
		body.setLinearVelocity(velocity);
		itemRF.setZero();
		
		lookTo(new Vec2(0, 0));
		closeLanding();
	}
	
	@Override
	public void drawPlayerLanding(GL10 gl, float worldZoom) {
		// Landing gear
		gl.glPushMatrix();
		float transitionFactor = (float)(landingPosition - LANDING_GEAR_OPEN_ROTATION) / (float)(LANDING_GEAR_CLOSED_ROTATION - LANDING_GEAR_OPEN_ROTATION);
		// gl.glRotatef(VecHelper.angleFromDeg(nearestPlanet, itemC) * (1 - transitionFactor) + getBallRotation() * transitionFactor, 0, 0, 1);
		gl.glRotatef(nearestPlanetAngle * (1 - transitionFactor) + getBallRotation() * transitionFactor, 0, 0, 1);
		gl.glTranslatef(-landingDrawShiftX, 0, 0);
		landingGearLeft.draw(gl);
		landingGearRight.draw(gl);
		gl.glPopMatrix();
	}
	
	@Override
	public void drawPlayer(GL10 gl, float worldZoom) {
		// Calculation steps...
		
		lookToDistance.set(itemC);
		lookToDistance.subLocal(eyeMoveToOnGame); // lookTo = itemC - eyeMoveToOnGame
		
		float lookLength, lookAngle;
		if(eyeRollProgress == -1) { // Not
			lookLength = lookToDistance.length();
			lookAngle = VecHelper.angleRad(lookToDistance);
			EYE_MOVE_SPEED = 10;
		} else {
			lookLength = 10000; // Large number
			lookAngle = (float) (eyeRollProgress * Math.PI) + (getRotation()-90) * DEG_TO_RAD; // Half circle
			EYE_MOVE_SPEED = 1;
		}
		
		lookLength = CompuFuncs.trimMinMax(lookLength, -LOOKTO_DISTANCE_AFFECTOR, LOOKTO_DISTANCE_AFFECTOR);
		lookLength *= (float)EYE_MOVE_AMOUNT / (float)LOOKTO_DISTANCE_AFFECTOR;

		//float eyeMovePosX = (float) ((Math.abs(lookToDistance.x) < LOOKTO_DISTANCE_AFFECTOR) ? (float) (lookToDistance.x / LOOKTO_DISTANCE_AFFECTOR * EYE_MOVE_AMOUNT) : EYE_MOVE_AMOUNT * Math.signum(lookToDistance.x));
		//float eyeMovePosY = (float) ((Math.abs(lookToDistance.y) < LOOKTO_DISTANCE_AFFECTOR) ? (float) (lookToDistance.y / LOOKTO_DISTANCE_AFFECTOR * EYE_MOVE_AMOUNT) : EYE_MOVE_AMOUNT * Math.signum(lookToDistance.y));
		eyeMoveTo.x = (float) (Math.cos(lookAngle) * lookLength); // Reapply angle
		eyeMoveTo.y = (float) (Math.sin(lookAngle) * lookLength);
		
		// Log.v(TAG, "X: " + eyeMoveTo.x + ", Y: " + eyeMoveTo.y);
		
		//eyeMoveTo.x = eyeMovePosX; // CompuFuncs.RotateX(eyeMovePosX, eyeMovePosY, ballRotation * DEG_TO_RAD);
		//eyeMoveTo.y = eyeMovePosY; //CompuFuncs.RotateY(eyeMovePosX, eyeMovePosY, ballRotation * DEG_TO_RAD);
		
		Vec2 eyeDistanceToMove = eyeMoveTo.sub(eyePos);
		eyePos.x += eyeDistanceToMove.x / EYE_MOVE_SPEED;
		eyePos.y += eyeDistanceToMove.y / EYE_MOVE_SPEED;
		
		eyeRotatedPos.set(eyePos);
		VecHelper.rotateLocal(eyeRotatedPos, null, -getBallRotation() * DEG_TO_RAD);
		
		
		// Draw
		
		gl.glPushMatrix();
		ballRotation = body.getAngle() * RAD_TO_DEG;
		gl.glRotatef(getBallRotation(), 0, 0, 1);
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
			gl.glTranslatef((float)-eyeRotatedPos.x, (float)-eyeRotatedPos.y, 0);
			
			leftEyeinside.draw(gl);
			rightEyeinside.draw(gl);
			
			gl.glPopMatrix();
		}
		
		gl.glPopMatrix();
	}
	
	@Override
	public void lookTo(Vec2 point)
	{
		eyeMoveToOnGame = point;
	}
	
	private float landingDestinationPos = 120;
	
	@Override
	public final boolean openLanding() {
		boolean ret = super.openLanding();
		if(!ret) { // Already open
			return ret; // Return early
		}
		if(landingDestinationPos != LANDING_GEAR_OPEN_ROTATION)
			moveLandingTo(LANDING_GEAR_OPEN_ROTATION, LANDING_DRAW_SHIFT_TOTAL);
		landingDestinationPos = LANDING_GEAR_OPEN_ROTATION;
		return ret;
	}
	
	@Override
	public final boolean closeLanding() {
		boolean ret = super.closeLanding();
		if(!ret) { // Already open
			return ret; // Return early
		}
		if(landingDestinationPos != LANDING_GEAR_CLOSED_ROTATION)
			moveLandingTo(LANDING_GEAR_CLOSED_ROTATION, 0);
		landingDestinationPos = LANDING_GEAR_CLOSED_ROTATION;
		return ret;
	}
	
	public final void rollEyes() {
		if(eyeRollProgress == -1) eyeRollProgress = 0;
	}
	
	private static final float LANDING_MOVE_ANIMATION_STEP = (float) (Math.PI * 0.02);
	private static final float LANDING_DRAW_SHIFT_TOTAL = 1.4f;
	
	private float landingAnimationMidPoint = 0;
	private float landingAnimationScale = 0;
	private float landingAnimationShiftMidPoint = 0;
	private float landingAnimationShiftScale = 0;
	private float landingAnimation = (float) Math.PI;
	private float landingPosition = LANDING_GEAR_CLOSED_ROTATION;
	private float landingDrawShiftX = 0;
	
	private final void moveLandingTo(int degrees, float moveTo) {
		landingAnimation = 0;
		landingAnimationScale = (degrees - landingPosition) / 2;
		landingAnimationMidPoint = (degrees + landingPosition) / 2;
		
		landingAnimationShiftMidPoint = (moveTo + landingDrawShiftX) / 2;
		landingAnimationShiftScale = (moveTo - landingDrawShiftX) / 2;
	}
	
	private Vec2 previousVelocity, deltaVelocity = new Vec2();
	private static final float VELOCITY_FORCE_FACTOR = 100000f;
	
	@Override
	public void move(float millistep, float speedScale) {
		super.move(millistep, speedScale);
		
		double leftEarFullRotation = getBallRotation() + lEarRotation;
		double rightEarFullRotation = getBallRotation() + rEarRotation;
		
		// Get delta velocity as sort of force on ears
		if(previousVelocity == null) previousVelocity = new Vec2(body.getLinearVelocity());
		
		if(eyeRollProgress > 1) eyeRollProgress = -1;
		if(eyeRollProgress != -1) eyeRollProgress += millistep / 1000 * EYE_ROLL_TIMETAKEN;
		
		deltaVelocity.set(body.getLinearVelocity()); // delta = current - previous
		deltaVelocity.subLocal(previousVelocity);
		deltaVelocity.mul(VELOCITY_FORCE_FACTOR);
		
		float forceX = (itemRF.x + leftEarExtraForce.x + deltaVelocity.x) * EAR_FORCE_PREMULTIPLIER;
		float forceY = (itemRF.y + leftEarExtraForce.y + deltaVelocity.y) * EAR_FORCE_PREMULTIPLIER;
		
		// Work out angular force on ears.
		double leftEarExternalForce  = CompuFuncs.rotateY(
				forceX,
				forceY,
				(float) ((180-leftEarFullRotation ) / 180 * Math.PI));
		double rightEarExternalForce = CompuFuncs.rotateY(
				forceX,
				forceY,
				(float) ((180-rightEarFullRotation) / 180 * Math.PI));
		
		previousVelocity.set(body.getLinearVelocity());
		
		// Natural rotation + the force from earlier - torque, to make ears delay when body moves.
		double leftEarForce = LEFT_EAR_RESTING_POSITION - lEarRotation + leftEarExternalForce - (getEarTorque() * 2);
		
		lEarRotationSpeed += leftEarForce * millistep / ITERS / 1000f * EAR_FORCE_MULTIPLIER;
		lEarRotationSpeed *= EAR_ROTATING_AIR_RESISTANCE;
		lEarRotation += lEarRotationSpeed * millistep / ITERS / 1000f * speedScale * EAR_ROTATING_SPEED;
		
		double rightEarForce = RIGHT_EAR_RESTING_POSITION - rEarRotation + rightEarExternalForce - (getEarTorque() * 1); // Different to make ears wobble differently
		
		rEarRotationSpeed += rightEarForce * millistep / ITERS / 1000f * EAR_FORCE_MULTIPLIER;
		rEarRotationSpeed *= EAR_ROTATING_AIR_RESISTANCE;
		rEarRotation += rEarRotationSpeed * millistep / ITERS / 1000f * speedScale * EAR_ROTATING_SPEED;
		
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
	
	// private Vec2 nearestPlanet = new Vec2();
	/**
	 * The angle to the nearest planet IN DEGREES
	 */
	private float nearestPlanetAngle;
	
	@Override
	public void setNearestLandingPoint(final float angle) {
		super.setNearestLandingPoint(angle);
		// if(landingAnimation >= Math.PI) { // If animation is in progress, don't set new planet pos, as it will disrupt smooth animation
			nearestPlanetAngle = angle;
		// }
	}

	@Override
	public void drawMove(float millistep, float speedscale) {
		super.drawMove(millistep, speedscale);
	}
	
	private final Vec2 leftEarExtraForce = new Vec2(), rightEarExtraForce = new Vec2();
	
	/**
	 * Sets any extra force on the left ear, making it bounce.
	 * @param x
	 * @param y
	 */
	public void setLeftEarExtraForce(float x, float y) {
		leftEarExtraForce.x = x;
		leftEarExtraForce.y = y;
	}
	
	/**
	 * Sets any extra force on the right ear, making it bounce.
	 * @param x
	 * @param y
	 */
	public void setRightEarExtraForce(float x, float y) {
		rightEarExtraForce.x = x;
		rightEarExtraForce.y = y;
	}
	
	private final Vec2[] earAbsolutePositions = new Vec2[] {
			new Vec2(),
			new Vec2(),
	};

	@Override
	public Vec2[] getEarAbsolutePositions() {
		earAbsolutePositions[0].x = lEar.x + CompuFuncs.rotateX(EAR_END.x, EAR_END.y, lEarRotation * DEG_TO_RAD) - landingDrawShiftX;
		earAbsolutePositions[0].y = lEar.y + CompuFuncs.rotateY(EAR_END.x, EAR_END.y, lEarRotation * DEG_TO_RAD);
		earAbsolutePositions[1].x = rEar.x + CompuFuncs.rotateX(EAR_END.x, EAR_END.y, rEarRotation * DEG_TO_RAD) - landingDrawShiftX;
		earAbsolutePositions[1].y = rEar.y + CompuFuncs.rotateY(EAR_END.x, EAR_END.y, rEarRotation * DEG_TO_RAD);
		if(getAlternateDrawPosition() == null) {
			VecHelper.rotateLocal(earAbsolutePositions[0], null, getBallRotation() * DEG_TO_RAD);
			VecHelper.rotateLocal(earAbsolutePositions[1], null, getBallRotation() * DEG_TO_RAD);
			earAbsolutePositions[0].x += itemC.x;
			earAbsolutePositions[0].y += itemC.y;
			earAbsolutePositions[1].x += itemC.x;
			earAbsolutePositions[1].y += itemC.y;
		} else {
			VecHelper.rotateLocal(earAbsolutePositions[0], null, getAlternateDrawAngle() * DEG_TO_RAD);
			VecHelper.rotateLocal(earAbsolutePositions[1], null, getAlternateDrawAngle() * DEG_TO_RAD);
			earAbsolutePositions[0].x += getAlternateDrawPosition().x;
			earAbsolutePositions[0].y += getAlternateDrawPosition().y;
			earAbsolutePositions[1].x += getAlternateDrawPosition().x;
			earAbsolutePositions[1].y += getAlternateDrawPosition().y;
		}
		
		return earAbsolutePositions;
	}
}
