package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.assistors.SoundManager;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Gravitable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable;
import android.util.Log;

/**
 * Finishing portal for the game
 * @author william
 *
 */
public class Portal extends Gravitable implements Moveable, Warpable {
	
	private static final float PORTAL_RADIUS = 7f;
	private static final float PORTAL_DENSITY = 2f;
	private static final float PORTAL_NORMAL_DENSITY = 0;
	private static final float PORTAL_NORMAL_RADIUS = 0;
	
	private static enum Status {
		DISABLED,
		OPENING,
		ENABLED,
		
		FINISHING,
		FINISHED
	}
	
	private static enum FinishingStatus {
		OPENING,
		ENTERING,
		CLOSING
	}
	
	private Status status = Status.DISABLED;
	private FinishingStatus finStatus = FinishingStatus.OPENING;

	private final RectMesh img, opening;
	
	private float rotation = 0, rotation2 = 0;
	
	private static final float OPENING_RADIUS = 3f;
	private float openingRadius = 0;
	
	public Portal(SimulationContext context, Vec2 coord) {
		super(context, coord, 10f, PORTAL_NORMAL_DENSITY, PORTAL_NORMAL_RADIUS, BodyType.STATIC);
		
		img = new RectMesh((float)getPos().x, (float)getPos().y, 0, 0, R.drawable.portal);
		opening = new RectMesh((float)getPos().x, (float)getPos().y, 0, 0, R.drawable.portal_opening);
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_NONE;
		fixture.getFilterData().maskBits = COLLISION_GROUP_NONE;
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		if(status != Status.DISABLED) {
			img.setRotation(rotation);
			img.draw(gl);
		}
		if(status == Status.FINISHING) {
			opening.setRotation(rotation2);
			opening.draw(gl);
		}
	}
	
	/**
	 * Simple timer to move to next stage of animation of portal
	 */
	private float openingTimer = 0;

	@Override
	public void move(float millistep, float speedScale) { }

	@Override
	public void drawMove(float millistep, float speedscale) {
		rotation -= 2.5;
		rotation2 += 1;
		if(status == Status.OPENING) {
			setRadius(getRadius() + (float)(PORTAL_RADIUS  - getRadius() ) / 20f);
			setDensity(getDensity() + (float)(PORTAL_DENSITY - getDensity()) / 20f);
			
			if(PORTAL_RADIUS - getRadius() < 1) {
				setRadius(PORTAL_RADIUS);
				setDensity(PORTAL_DENSITY);
				status = Status.ENABLED;
			}
			
			img.setWH(getRadius() * 2, getRadius() * 2);
		} else if(status == Status.FINISHING) {
			switch(finStatus) {
			case OPENING:
				openingRadius += (float)(OPENING_RADIUS - openingRadius) / 20f;
				if(openingRadius > OPENING_RADIUS)
					openingRadius = OPENING_RADIUS;
				
				if(openingTimer++ > 40) {
					finStatus = FinishingStatus.ENTERING;
				}
				opening.setWH(openingRadius * 2, openingRadius * 2);
				break;
			case ENTERING:
				break;
			case CLOSING:
				openingRadius -= (float)(OPENING_RADIUS - openingRadius) / 20f;
				if(openingRadius < 0) {
					openingRadius = 0;
					status = Status.FINISHED;
				}
				opening.setWH(openingRadius * 2, openingRadius * 2);
				break;
			}
		}
	}
	
	public void activate() {
		status = Status.OPENING;
	}
	
	private float tmpWarpRotateSpeed = 1;
	private float tmpWarpScaleSpeed = 0.05f;
	
	private final Vec2 tmpFinishPoint = new Vec2();
	
	public void calculateVelocityImmutable(PlayerBase p, float itemRadius) {
		super.calculateVelocityImmutable(p.itemC, p.getVelocity(), itemRadius);
		
		if(status == Status.FINISHING || status == Status.FINISHED) {
			tmpFinishPoint.x -= (tmpFinishPoint.x - getPosX()) / 100f;
			tmpFinishPoint.y -= (tmpFinishPoint.y - getPosY()) / 100f;
			p.itemC.set(tmpFinishPoint);
		} else if(status != Status.DISABLED && VecHelper.dist(getPos(), p.itemC) < 2f) {
			tmpFinishPoint.set(p.itemC);
			SoundManager.get().playSound(SoundManager.SOUND_PORTAL);
		}
	}
	
	public void calculateVelocityMutable(PlayerBase p, float itemRadius) {
		if(status != Status.DISABLED && VecHelper.dist(getPos(), p.itemC) < 2f)
			status = Status.FINISHING;
	}
	
	public void calculateAnimation(Player p) {
		if(status == Status.FINISHING) {
			if(finStatus == FinishingStatus.ENTERING) {
				p.warpRotation += tmpWarpRotateSpeed;
				tmpWarpRotateSpeed += 0.6f;
				
				p.warpScale += tmpWarpScaleSpeed;
				tmpWarpScaleSpeed -= 0.003f;
				
				if(p.warpScale < 0) {
					p.warpScale = 0;
					finStatus = FinishingStatus.CLOSING;
				}
			}
		}
	}
	
	private boolean firstTimeFinish = true;
	
	@Override
	public WarpData sendWarpData() {
		if(status == Status.FINISHED) {
			return new WarpData(true, WarpData.END_SUCCESS);
		} else if(status == Status.FINISHING && firstTimeFinish) {
			firstTimeFinish = false;
			return new WarpData(true);
		}
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		if(status == Status.FINISHING || status == Status.FINISHED) {
			Log.v("SpaceGame", "Exclusive");
		}
		return status == Status.FINISHING || status == Status.FINISHED; // Only when entering portal
	}
}
