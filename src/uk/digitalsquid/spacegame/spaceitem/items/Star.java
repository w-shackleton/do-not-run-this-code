package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.Spherical;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.LevelAffectable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

public class Star extends Spherical implements LevelAffectable, Forceful, StaticDrawable
{
	private static final int STAR_RADIUS = 15;
	
	private static BitmapDrawable img;
	
	private boolean available = true;
	private boolean drawingP1 = true;
	private boolean drawingP2 = false;
	private boolean sendStatus = false;
	
	private float drawRadius;
	
	public Star(Context context, Coord coord)
	{
		super(context, coord, STAR_RADIUS);
		
		drawRadius = radius;
		
		img = (BitmapDrawable) context.getResources().getDrawable(R.drawable.star);
	}
	
	@Override
	public void draw(Canvas c, float worldZoom)
	{
		if(drawingP1) {
			img.setAntiAlias(StaticInfo.Antialiasing);
			img.setAlpha(0xFF);
			img.setBounds(
					(int)(pos.x - drawRadius * worldZoom),
					(int)(pos.y - drawRadius * worldZoom),
					(int)(pos.x + drawRadius * worldZoom),
					(int)(pos.y + drawRadius * worldZoom));
			img.draw(c);
		}
	}

	@Override
	public AffectData affectLevel() {
		if(sendStatus) {
			sendStatus = false;
			return new AffectData(1);
		}
		return null;
	}

	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC) {
		return null;
	}

	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC,
			float itemRadius) {
		if(available && Coord.getLength(pos, itemC) < radius + itemRadius) {
			available = false;
			sendStatus = true;
		}
		
		if(!available && drawingP1) {
			drawingP1 = false;
			drawingP2 = true;
		}
		
		return null;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	private Matrix tmpInverse = null;
	private float animX, animY;
	private float animAngle = 1;

	@Override
	public void drawStatic(Canvas c, final float worldZoom, final int width, final int height,
			final Matrix matrix) {
		if(drawingP2) {
			
			if(tmpInverse == null) { // Get position first time
				tmpInverse = new Matrix();
				matrix.invert(tmpInverse);
				float[] points = {(float) pos.x, (float) pos.y};
				matrix.mapPoints(points);
				
				animX = points[0];
				animY = points[1];
			}
			
			animX *= 0.9f;
			animY *= 0.9f;
			if(animAngle < Math.PI) animAngle += 0.1f;
			float adjustedAngle = (float) (Math.cos(animAngle) * 360);
			
			float distFromDest = (float) Math.hypot(animX, animY);
			int opacity = 255;
			if(distFromDest < 128) opacity = CompuFuncs.TrimMin((int) ((distFromDest - 64) * 255f / 64f), 0);
			if(opacity == 0) drawingP2 = false;
			
			c.rotate(adjustedAngle, animX, animY);
			img.setAntiAlias(StaticInfo.Antialiasing);
			img.setAlpha(opacity);
			img.setBounds(
					(int)(animX - radius * 1.5 * worldZoom),
					(int)(animY - radius * 1.5 * worldZoom),
					(int)(animX + radius * 1.5 * worldZoom),
					(int)(animY + radius * 1.5 * worldZoom));
			img.draw(c);
			c.rotate(-adjustedAngle, animX, animY);
		}
	}
}
