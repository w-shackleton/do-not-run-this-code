package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

public abstract class Player extends PlayerBase implements Moveable
{
	protected static final float EYE_RADIUS = .05f;
	protected static final int ITERS = Simulation.ITERS; // From PlanetaryView - not really needed.
	
	protected float ballRotation = 0;
	protected float ballRotationSpeed = 0;
	protected float ballMomentum = 0;
	
	protected static final float BALL_ROTATION_AIR_RESISTANCE = 0.98f;
	
	public Player(SimulationContext context, Vec2 coord, float radius)
	{
		super(context, coord, radius);
		body.setAngularDamping(0.1f);
	}
	
	@Override
	public final void draw(GL10 gl, float worldZoom) {
		// Lock position if necessary
		if(landPosition != null) itemC.set(landPosition);
		
		gl.glPushMatrix();
		gl.glTranslatef((float)itemC.x, (float)itemC.y, 0);
		gl.glScalef(warpScale, warpScale, 1);
		gl.glRotatef(warpRotation, 0, 0, 1);
		
		drawPlayer(gl, worldZoom);
		
		gl.glPopMatrix();
	}
	
	/**
	 * Draws the player around the point (0, 0)
	 * @param gl
	 * @param worldZoom
	 */
	public abstract void drawPlayer(GL10 gl, float worldZoom);

	@Override
	public void move(float millistep, float speedScale)
	{
		// Lock position if necessary
		if(landPosition != null) itemC.set(landPosition);
		// Calculate rotation of ball
		ballRotation = body.getAngle() * RAD_TO_DEG;
		ballRotation = CompuFuncs.mod(ballRotation, 360);
		float itemRFDirection = VecHelper.angleDeg(apparentRF);
		ballMomentum = 0;
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
		
		body.applyTorque(ballMomentum / 100000 * apparentRF.length());
		
		/* ballRotationSpeed += ballMomentum * millistep / ITERS / 1000f;
		ballRotationSpeed *= BALL_ROTATION_AIR_RESISTANCE;
		ballRotation += ballRotationSpeed * millistep / ITERS / 1000f * speedScale; */
	}
	
	private Vec2 landPosition;
	
	public void openLanding() {
		landPosition = new Vec2(itemC);
	}
	public void closeLanding() {
		landPosition = null;
	}
	
	/**
	 * Makes the character 'look' towards a certain point. This makes the game seem interactive etc...
	 * @param point A {@link Vec2} in the game (NOT on the screen). Points on the screen need to be put through the reverse matrix first.
	 */
	public void lookTo(Vec2 point) {}
	
	public void setNearestLandingPoint(final Vec2 planet) {}
	
	float warpRotation = 0;
	float warpScale = 1;
}
