package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.WorldManifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.contacts.Contact;

import uk.digitalsquid.spacegame.spaceitem.assistors.Simulation;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

public abstract class Player extends PlayerBase implements Moveable
{
	protected static final float EYE_RADIUS = .5f;
	protected static final int ITERS = Simulation.ITERS; // From PlanetaryView - not really needed.
	
	protected float ballRotation = 0;
	protected float ballRotationSpeed = 0;
	protected float ballMomentum = 0;
	
	public Player(SimulationContext context, Vec2 coord) {
		super(context, coord);
		body.getWorld().setContactListener(contactListener);
	}
	
	/**
	 * An alternate position to draw the character to (temporarily). If set to null, normal drawing will occur.
	 */
	private Vec2 alternateDrawPosition;
	
	/**
	 * The alternate angle. See Player.alternateDrawPosition
	 */
	private float alternateDrawAngle;
	
	@Override
	public final void draw(GL10 gl, float worldZoom) {
		// Lock position if necessary
		// if(landPosition != null) itemC.set(landPosition);
		
		// Landing
		gl.glPushMatrix();
		gl.glTranslatef((float)itemC.x, (float)itemC.y, 0);
		
		gl.glScalef(warpScale, warpScale, 1);
		gl.glRotatef(warpRotation, 0, 0, 1);
		
		drawPlayerLanding(gl, worldZoom);
		
		gl.glPopMatrix();
		
		// Rest
		gl.glPushMatrix();
		if(alternateDrawPosition == null) gl.glTranslatef((float)itemC.x, (float)itemC.y, 0);
		else {
			gl.glTranslatef((float)alternateDrawPosition.x, (float)alternateDrawPosition.y, 0); // Use alternate if available
			gl.glRotatef(alternateDrawAngle - ballRotation, 0, 0, 1); // Rotate to face launch
		}
		gl.glScalef(warpScale, warpScale, 1);
		gl.glRotatef(warpRotation, 0, 0, 1);
		
		drawPlayer(gl, worldZoom);
		
		gl.glPopMatrix();
	}
	
	/**
	 * Draws the player's landing around the point (0, 0)
	 * @param gl
	 * @param worldZoom
	 */
	public abstract void drawPlayerLanding(GL10 gl, float worldZoom);
	
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
		// if(landPosition != null) itemC.set(landPosition);
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
		
		bodyGravityTorque = ballMomentum / 100 * apparentRF.length();
		/**
		 * This multiplier reduces the effect of the gravity torque when the accelerometer is going.
		 */
		float accelMultiplier = CompuFuncs.trimMinMax((3 - Math.abs(accelerometerMoment)) / 3, 0, 1);
		body.applyTorque(bodyGravityTorque * accelMultiplier);
		body.applyTorque(accelerometerMoment);
		earTorque = bodyGravityTorque * accelMultiplier + accelerometerMoment;
		
		// Applying here so we can get angular speed first
		/* float momentumMultiplier = CompuFuncs.trimMinMax(Math.abs(body.getAngularVelocity()) * .1f, 0, 1);
		Log.d(TAG, "Mult: " + momentumMultiplier);
		// Apply a reverse torque proportional to the speed
		body.applyTorque(-momentumMultiplier * bodyGravityTorque * accelMultiplier);
		body.applyTorque(-momentumMultiplier * accelerometerMoment); */
		
		velocityDelta.set(body.getLinearVelocity());
		velocityDelta.subLocal(previousVelocity);
		
		if(velocityDelta.lengthSquared() < 0.00000009f &&
				body.getLinearVelocity().lengthSquared() < 0.04f) { // 0.0003^2
			openLanding();
		}
		if(body.getLinearVelocity().lengthSquared() > 0.07f) {
			closeLanding();
		}
		
		previousVelocity.set(body.getLinearVelocity());
	}
	
	@Override
	public void drawMove(float millistep, float speedScale) {
		setLandingPointFromContact();
	}
	
	/**
	 * The torque experienced due to gravity.
	 */
	private float bodyGravityTorque;
	
	/**
	 * The difference between this velocity and the previous.
	 * TODO: This solution is a bit rough, and won't work under fps changes.
	 */
	private Vec2 velocityDelta = new Vec2();
	private Vec2 previousVelocity = new Vec2();
	
	/**
	 * True if open
	 */
	private boolean landingState = false;
	
	/**
	 * Opens the landing gear. Returns true on success, false if already open.
	 * @return
	 */
	public boolean openLanding() {
		if(isLanded()) {
			setLanded(true);
			return false;
		} else {
			setLanded(true);
			playerStateChangedBroadcast.onLanded();
			return true;
		}
	}
	/**
	 * Closes the landing gear.
	 * @return <code>true</code> on success, <code>false</code> if already closed.
	 */
	public boolean closeLanding() {
		if(!isLanded()) {
			setLanded(false);
			return false;
		} else {
			setLanded(false);
			playerStateChangedBroadcast.onLaunch();
			return true;
		}
	}
	
	/**
	 * Makes the character 'look' towards a certain point. This makes the game seem interactive etc...
	 * @param point A {@link Vec2} in the game (NOT on the screen). Points on the screen need to be put through the reverse matrix first.
	 */
	public void lookTo(Vec2 point) {}
	
	public void setNearestLandingPoint(final float angle) {}
	
	private WorldManifold _worldManifold;
	
	/**
	 * Uses the stored contact point to recompute the normal angle
	 */
	void setLandingPointFromContact() {
		if(landingContact != null) {
			_worldManifold = new WorldManifold(); // Get world normal, use as angle of landing
			landingContact.getWorldManifold(_worldManifold);
			if(landingIsA)
				setNearestLandingPoint(VecHelper.angleDeg(_worldManifold.normal));
			else
				setNearestLandingPoint(VecHelper.angleDeg(_worldManifold.normal.negateLocal()));
		}
	}
	
	private Contact landingContact;
	private boolean landingIsA;
	
	private ContactListener contactListener = new ContactListener() {
		@Override
		public void preSolve(Contact contact, Manifold oldManifold) {
		}
		@Override
		public void postSolve(Contact contact, ContactImpulse impulse) {
		}
		@Override
		public void endContact(Contact contact) {
			if(contact.equals(landingContact)) {
				landingContact = null;
			}
		}
		@Override
		public void beginContact(Contact contact) {
			Fixture iter = body.getFixtureList();
			do {
				if(contact.getFixtureA().equals(iter)) { // Contact is ours
					landingIsA = true;
					landingContact = contact;
					setLandingPointFromContact();
				} else if(contact.getFixtureB().equals(iter)) { // Contact is other's, touching us
					landingIsA = false;
					landingContact = contact;
					setLandingPointFromContact();
				}
			} while((iter = iter.getNext()) != null);
		}
	};
	
	protected boolean isLanded() {
		return landingState;
	}

	private void setLanded(boolean landingState) {
		this.landingState = landingState;
	}

	float warpRotation = 0;
	float warpScale = 1;
	
	/**
	 * Events about the character.
	 * @author william
	 *
	 */
	public static interface PlayerStateChangedListener {
		public void onLanded();
		public void onLaunch();
	}
	
	public final void registerPlayerStateChangedListener(PlayerStateChangedListener l) {
		playerStateChangedBroadcastList.add(l);
	}
	
	private final List<PlayerStateChangedListener> playerStateChangedBroadcastList = new LinkedList<Player.PlayerStateChangedListener>();
	
	protected PlayerStateChangedListener playerStateChangedBroadcast = new PlayerStateChangedListener() {
		@Override
		public void onLaunch() {
			for(PlayerStateChangedListener l : playerStateChangedBroadcastList) {
				l.onLaunch();
			}
		}
		
		@Override
		public void onLanded() {
			for(PlayerStateChangedListener l : playerStateChangedBroadcastList) {
				l.onLanded();
			}
		}
	};
	
	/**
	 * Uses the alternate drawing position if necessary
	 * @return an array length 2 containing the positions of both ears in gamespace.
	 */
	public abstract Vec2[] getEarAbsolutePositions();
	
	/**
	 * Sets the alternate place to draw the character.
	 * @param position A {@link Vec2}, or <code>null</code> to use actual position
	 */
	public void setAlternateDrawPosition(Vec2 position, boolean updateAngle) {
		alternateDrawPosition = position;
		if(updateAngle) alternateDrawAngle = alternateDrawPosition != null ? VecHelper.angleFromDeg(alternateDrawPosition, itemC) : 0; // Player faces launcher.
	}

	public Vec2 getAlternateDrawPosition() {
		return alternateDrawPosition;
	}

	public float getAlternateDrawAngle() {
		return alternateDrawAngle;
	}

	public float getBallRotation() {
		return ballRotation;
	}
	
	protected float earTorque;

	protected float getEarTorque() {
		return earTorque;
	}
	
	private float accelerometerMoment;
	
	public void setAccelerometerMoment(float moment) {
		// This multiplier stops the player spinning too fast - the faster it spins, the less the moment is applied
		accelerometerMoment = -moment;
	}
}
