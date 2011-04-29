package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Portal extends Gravitable implements Moveable, Warpable {
	
	private static final int PORTAL_RADIUS = 70;
	private static final float PORTAL_DENSITY = .8f;
	private static final float PORTAL_NORMAL_DENSITY = 0;
	private static final float PORTAL_NORMAL_RADIUS = 0;
	
	private static final double ONER2 = 0.707106781; // 1f / Math.sqrt(2);
	
	private static final PaintDesc PAINT_BLACK = new PaintDesc(0, 0, 0);
	
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

	private final Drawable img;
	
	private float rotation = 0, rotation2 = 0;
	
	private final Path circleClip;
	private final Path octagonClip;
	
	private static final int OPENING_RADIUS = 30;
	private float openingRadius = 0;
	
	public Portal(Context context, Coord coord) {
		super(context, coord, 0.75f, PORTAL_NORMAL_DENSITY, PORTAL_NORMAL_RADIUS);
		
		circleClip = new Path();
		octagonClip = new Path();
		
		img = (BitmapDrawable) context.getResources().getDrawable(R.drawable.portal);
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		if(status != Status.DISABLED) {
			circleClip.reset();
			circleClip.addCircle((float)pos.x, (float)pos.y, radius, Direction.CW);
			
			c.save();
			c.clipPath(circleClip);
			
			c.rotate(rotation, (float)pos.x, (float)pos.y);
			img.setBounds(
					(int)((pos.x - PORTAL_RADIUS) * worldZoom),
					(int)((pos.y - PORTAL_RADIUS) * worldZoom),
					(int)((pos.x + PORTAL_RADIUS) * worldZoom),
					(int)((pos.y + PORTAL_RADIUS) * worldZoom));
			img.draw(c);
			c.rotate(-rotation, (float)pos.x, (float)pos.y);
			
			c.restore();
		}
		if(status == Status.FINISHING) {
			updateOcatgonClip(openingRadius);
			
			c.save();
			c.rotate(rotation2, (float)pos.x, (float)pos.y);
			c.clipPath(octagonClip);
			c.drawPaint(PaintLoader.load(PAINT_BLACK));
			
			c.restore();
		}
	}
	
	/**
	 * Simple timer to move to next stage of animation of portal
	 */
	private float openingTimer = 0;

	@Override
	public void move(float millistep, float speedScale) {
		rotation -= 3;
		rotation2 -= 0.3;
		if(status == Status.OPENING) {
			radius  += (float)(PORTAL_RADIUS  - radius ) / 100f;
			density += (float)(PORTAL_DENSITY - density) / 100f;
			
			if(PORTAL_RADIUS - radius < 1) {
				radius = PORTAL_RADIUS;
				density = PORTAL_DENSITY;
				status = Status.ENABLED;
			}
		} else if(status == Status.FINISHING) {
			switch(finStatus) {
			case OPENING:
				openingRadius += (float)(OPENING_RADIUS - openingRadius) / 70f;
				if(openingRadius > OPENING_RADIUS)
					openingRadius = OPENING_RADIUS;
				
				if(openingTimer++ > 200)
					finStatus = FinishingStatus.ENTERING;
				break;
			case ENTERING:
				break;
			case CLOSING:
				openingRadius -= (float)(OPENING_RADIUS - openingRadius) / 70f;
				if(openingRadius < 0) {
					openingRadius = 0;
					status = Status.FINISHED;
				}
				break;
			}
		}
	}
	
	public void activate() {
		status = Status.OPENING;
	}
	
	private float tmpWarpRotateSpeed = 0.2f;
	private float tmpWarpScaleSpeed = 0.02f;
	
	public BallData calculateVelocity(Player p, float itemRadius) {
		BallData data = super.calculateVelocity(p.itemC, p.itemVC, itemRadius);
		
		if(status == Status.FINISHING || status == Status.FINISHED) {
			p.itemC.copyFrom(pos);
		} else if(status != Status.DISABLED && Coord.getLength(pos, p.itemC) < 10) {
			status = Status.FINISHING;
		}
		
		if(status == Status.FINISHING) {
			if(finStatus == FinishingStatus.ENTERING) {
				p.warpRotation += tmpWarpRotateSpeed;
				tmpWarpRotateSpeed += 0.07f;
				
				p.warpScale += tmpWarpScaleSpeed;
				tmpWarpScaleSpeed -= 0.001f;
				
				if(p.warpScale < 0) {
					p.warpScale = 0;
					finStatus = FinishingStatus.CLOSING;
				}
			}
		}
		
		return data;
	}
	
	private void updateOcatgonClip(float size) {
		octagonClip.reset();
		octagonClip.moveTo((float)(pos.x + 1     * size), (float)(pos.y + 0     * size));
		octagonClip.lineTo((float)(pos.x + ONER2 * size), (float)(pos.y + ONER2 * size));
		
		octagonClip.lineTo((float)(pos.x - 0     * size), (float)(pos.y + 1     * size));
		octagonClip.lineTo((float)(pos.x - ONER2 * size), (float)(pos.y + ONER2 * size));
		
		octagonClip.lineTo((float)(pos.x - 1     * size), (float)(pos.y - 0     * size));
		octagonClip.lineTo((float)(pos.x - ONER2 * size), (float)(pos.y - ONER2 * size));
		
		octagonClip.lineTo((float)(pos.x + 0     * size), (float)(pos.y - 1     * size));
		octagonClip.lineTo((float)(pos.x + ONER2 * size), (float)(pos.y - ONER2 * size));
		octagonClip.close();
	}

	@Override
	public WarpData sendWarpData() {
		if(status == Status.FINISHED) {
			return new WarpData(true, WarpData.END_SUCCESS);
		}
		return null;
	}
}
