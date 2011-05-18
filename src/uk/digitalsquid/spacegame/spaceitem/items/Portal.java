package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import android.content.Context;

public class Portal extends Gravitable implements Moveable, Warpable {
	
	private static final int PORTAL_RADIUS = 70;
	private static final float PORTAL_DENSITY = .5f;
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
	
	private static final int OPENING_RADIUS = 30;
	private float openingRadius = 0;
	
	public Portal(Context context, Coord coord) {
		super(context, coord, 0.95f, PORTAL_NORMAL_DENSITY, PORTAL_NORMAL_RADIUS);
		
		img = new RectMesh((float)pos.x, (float)pos.y, 0, 0, R.drawable.portal);
		opening = new RectMesh((float)pos.x, (float)pos.y, 0, 0, R.drawable.portal_opening);
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
		rotation -= 0.5;
		rotation2 += 0.2;
		if(status == Status.OPENING) {
			radius  += (float)(PORTAL_RADIUS  - radius ) / 100f;
			density += (float)(PORTAL_DENSITY - density) / 100f;
			
			if(PORTAL_RADIUS - radius < 1) {
				radius = PORTAL_RADIUS;
				density = PORTAL_DENSITY;
				status = Status.ENABLED;
			}
			
			img.setWH(radius * 2, radius * 2);
		} else if(status == Status.FINISHING) {
			switch(finStatus) {
			case OPENING:
				openingRadius += (float)(OPENING_RADIUS - openingRadius) / 70f;
				if(openingRadius > OPENING_RADIUS)
					openingRadius = OPENING_RADIUS;
				
				if(openingTimer++ > 200)
					finStatus = FinishingStatus.ENTERING;
				opening.setWH(openingRadius * 2, openingRadius * 2);
				break;
			case ENTERING:
				break;
			case CLOSING:
				openingRadius -= (float)(OPENING_RADIUS - openingRadius) / 70f;
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
	
	private float tmpWarpRotateSpeed = 0.2f;
	private float tmpWarpScaleSpeed = 0.02f;
	
	private final Coord tmpFinishPoint = new Coord();
	
	public BallData calculateVelocity(Player p, float itemRadius) {
		BallData data = super.calculateVelocity(p.itemC, p.itemVC, itemRadius);
		
		if(status == Status.FINISHING || status == Status.FINISHED) {
			tmpFinishPoint.x -= (tmpFinishPoint.x - pos.x) / 100f;
			tmpFinishPoint.y -= (tmpFinishPoint.y - pos.y) / 100f;
			p.itemC.copyFrom(tmpFinishPoint);
		} else if(status != Status.DISABLED && Coord.getLength(pos, p.itemC) < 10) {
			status = Status.FINISHING;
			tmpFinishPoint.copyFrom(p.itemC);
		}
		
		if(status == Status.FINISHING) {
			if(finStatus == FinishingStatus.ENTERING) {
				p.warpRotation += tmpWarpRotateSpeed;
				tmpWarpRotateSpeed += 0.07f;
				
				p.warpScale += tmpWarpScaleSpeed;
				tmpWarpScaleSpeed -= 0.0006f;
				
				if(p.warpScale < 0) {
					p.warpScale = 0;
					finStatus = FinishingStatus.CLOSING;
				}
			}
		}
		
		return data;
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
}
