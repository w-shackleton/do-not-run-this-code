package uk.digitalsquid.spacegame.spaceitem.assistors;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;

import uk.digitalsquid.spacegame.spaceitem.items.AnimatedPlayer;
import uk.digitalsquid.spacegame.spaceitem.items.Player;
import uk.digitalsquid.spacegame.spaceitem.items.PlayerBase;
import uk.digitalsquid.spacegame.spaceitem.items.Tether;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.ExtendedClickable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import android.util.Log;

/**
 * A class combining everything to do with firing the character.
 * @author William Shackleton
 *
 */
public final class LaunchingMechanism implements Moveable, Player.PlayerStateChangedListener, ExtendedClickable, Forceful {
	
	private final Player p;
	
	private final Tether left, right;
	
	private PlayerSimulator playerSimulator;
	
	private final SimulationContext context;
	
	/**
	 * The source of the drag action. Set to the physics engine's idea of the player's position
	 */
	private final Vec2 dragFromPoint= new Vec2();
	/**
	 * Where the user's finger is.
	 */
	private final Vec2 dragToPoint = new Vec2();
	
	public LaunchingMechanism(SimulationContext context, Player p) {
		this.p = p;
		p.registerPlayerStateChangedListener(this);
		this.context = context;
		left = new Tether(context);
		right = new Tether(context);
	}

	@Override
	public void move(float millistep, float speedScale) {
		switch(state) {
		case AIMING:
		case ACCELERATING:
			left.move(millistep, speedScale);
			right.move(millistep, speedScale);
			break;
		}
	}

	@Override
	public void drawMove(float millistep, float speedScale) {
		switch(state) {
		case ACCELERATING:
		case ACCELERATING_P2:
			p.setAlternateDrawPosition(playerSimulator.getPos());
			updateSprings();
			
			if(CompuFuncs.distanceFromLine(playerSimulator.getPos(), p.getPos(), p.getBallRotation()) > 0) { // Until cutoff
				// Go back to normal play
				state = State.NONE;
				p.getBody().setLinearVelocity(playerSimulator.getBody().getLinearVelocity());
				p.setAlternateDrawPosition(null);
				if(p instanceof AnimatedPlayer) {
					((AnimatedPlayer)p).setLeftEarExtraForce(0, 0);
					((AnimatedPlayer)p).setRightEarExtraForce(0, 0);
				}
				p.closeLanding();
				
				playerSimulator.dispose();
				playerSimulator = null;
			}
		case AIMING: // Both of these update here
			left.drawMove(millistep, speedScale);
			right.drawMove(millistep, speedScale);
			break;
		}
	}
	
	public void draw(GL10 gl) {
		switch(state) {
		case AIMING:
		case ACCELERATING:
			left.draw(gl, 1);
			right.draw(gl, 1);
			break;
		}
	}
	
	private boolean playerLanded = false;
	
	private enum State {
		NONE,
		AIMING,
		ACCELERATING,
		ACCELERATING_P2,
	}
	
	private State state = State.NONE;

	/**
	 * Player has landed, we take control from here.
	 */
	@Override
	public void onLanded() {
		playerLanded = true;
	}

	@Override
	public void onLaunch() {
		playerLanded = false;
	}
	
	protected static final float CAPTURE_RADIUS = 3;

	/**
	 * Used for drag events.
	 */
	@Override
	public boolean isClicked(float x, float y) {
		if(!playerLanded) return false; // Only when player has landed
		if(Math.hypot(x - p.getPosX(), y - p.getPosY()) < 3) { // If near to player
			return true;
		}
		return false;
	}
	
	/**
	 * The distance the player has to get to the launchpad before the force of the springs cuts off
	 */
	private static final float LAUNCH_FORCE_CUTOFF = 2f;

	@Override
	public Vec2 calculateRF(Vec2 itemC) {
		switch(state) {
		case ACCELERATING:
		case ACCELERATING_P2:
			Vec2 leftForce = left.calculateRF(null);
			Vec2 rightForce = right.calculateRF(null);
			float i = CompuFuncs.distanceFromLine(playerSimulator.getPos(), p.getPos(), p.getBallRotation());
			if(CompuFuncs.distanceFromLine(playerSimulator.getPos(), p.getPos(), p.getBallRotation()) < -LAUNCH_FORCE_CUTOFF) { // Until cutoff
				if(leftForce != null && rightForce != null) playerSimulator.applyForce(leftForce.addLocal(rightForce)); // Return both forces
				Log.v("SpaceGame", "Force" + i);
			} else {
				Log.v("SpaceGame", "NoForce" + i);
				if(state == State.ACCELERATING) { // First time passing only
					state = State.ACCELERATING_P2;
					left.disable(); // Get rid of springs as they stop becoming used.
					right.disable();
				}
			}
			return null; // TODO: Include v (that) in ^ (here)?
		case AIMING:
			// Update player ears here.
			if(p instanceof AnimatedPlayer) {
				AnimatedPlayer ap = (AnimatedPlayer) p;
				updateSprings();
				Vec2 leftForce2 = left.calculateRF(null);
				Vec2 rightForce2 = right.calculateRF(null);
				ap.setLeftEarExtraForce(leftForce2.x * 20, leftForce2.y * 20);
				ap.setRightEarExtraForce(rightForce2.x * 20, rightForce2.y * 20);
			}
			return null;
		default:
			return null;
		}
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemPos, Vec2 itemV, float itemRadius) { }
	
	@Override public void onClick(float x, float y) { }
	
	@Override
	public void mouseDown(float x, float y) {
		if(playerLanded) {
			state = State.AIMING;
			dragFromPoint.x = p.getPosX(); // Use original position as reference
			dragFromPoint.y = p.getPosY();
			
			dragToPoint.x = x;
			dragToPoint.y = y;
			
			left.activate(dragFromPoint.x, dragFromPoint.y, dragFromPoint.x, dragFromPoint.y);
			right.activate(dragFromPoint.x, dragFromPoint.y, dragFromPoint.x, dragFromPoint.y);
			
			mouseMove(x, y); // Update on first down as well
		}
	}
	
	/**
	 * Gets the springs' destinations from the player object, puts into springs.
	 */
	private void updateSprings() {
		Vec2[] earPoints = p.getEarAbsolutePositions();
		left.update(dragFromPoint.x, dragFromPoint.y, earPoints[0].x, earPoints[0].y);
		right.update(dragFromPoint.x, dragFromPoint.y, earPoints[1].x, earPoints[1].y);
	}

	@Override
	public void mouseMove(float x, float y) {
		if(state == State.AIMING) {
			dragToPoint.x = x;
			dragToPoint.y = y;
			p.setAlternateDrawPosition(dragToPoint);
			
			float i = CompuFuncs.distanceFromLine(dragToPoint, p.getPos(), p.getBallRotation());
			if(CompuFuncs.distanceFromLine(dragToPoint, p.getPos(), p.getBallRotation()) < 0) { // Until cutoff
				Log.i("SpaceGame", "less" + i);
			} else {
				Log.i("SpaceGame", "more" + i);
			}
		}
	}

	@Override
	public void mouseUp(float x, float y) {
		state = State.ACCELERATING;
		playerSimulator = new PlayerSimulator(context, new Vec2(p.getAlternateDrawPosition()));
	}
	
	private static final class PlayerSimulator extends PlayerBase {

		public PlayerSimulator(SimulationContext context, Vec2 coord) {
			super(context, coord);
			fixture.getFilterData().categoryBits = COLLISION_GROUP_SIMULATION;
			fixture.getFilterData().maskBits = COLLISION_GROUP_SIMULATION;
		}
		
		public void applyForce(Vec2 force) {
			body.applyForce(force, getPos());
		}

		@Override public void draw(GL10 gl, float worldZoom) { }
	}
}
