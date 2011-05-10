package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.Spherical;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.LevelAffectable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import android.content.Context;
import android.graphics.Matrix;

public class Star extends Spherical implements LevelAffectable, Forceful, StaticDrawable
{
	private static final int STAR_RADIUS = 15;
	
	private final RectMesh img;
	
	private boolean available = true;
	private boolean drawingP1 = true;
	private boolean drawingP2 = false;
	private boolean sendStatus = false;
	private boolean sendFinishedStatus = false;
	
	public Star(Context context, Coord coord)
	{
		super(context, coord, STAR_RADIUS);
		
		img = new RectMesh((float)pos.x, (float)pos.y, (float)radius * 2, (float)radius * 2, R.drawable.star);
	}
	
	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		if(drawingP1) {
			img.draw(gl);
		}
	}

	@Override
	public AffectData affectLevel() {
		if(sendStatus) {
			sendStatus = false;
			return new AffectData(true, false);
		}
		if(sendFinishedStatus) {
			sendFinishedStatus = false;
			return new AffectData(false, true);
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
	
	private boolean pointsMapped = false;
	private float animX, animY;
	private float animDestX, animDestY;
	private float animAngle = 1;

	@Override
	public void drawStatic(GL10 gl, final int width, final int height, final Matrix matrix) {
		if(drawingP2) {
			
			if(!pointsMapped)
			{
				float[] points = {(float) pos.x, (float) pos.y};
				matrix.mapPoints(points);
				
				animX = points[0];
				animY = points[1];
				
				animDestX = -width / 2;
				animDestY = height / 2;
				
				pointsMapped = true;
			}
			
			animX -= (animX - animDestX) / 20f;
			animY -= (animY - animDestY) / 20f;
			if(animAngle < Math.PI) animAngle += 0.01f;
			float adjustedAngle = (float) (Math.cos(animAngle) * 360);
			
			float distFromDest = (float) Math.hypot(animX - animDestX, animY - animDestY);
			float opacity = 1;
			if(distFromDest < 128) opacity = (float) CompuFuncs.TrimMin((distFromDest - 64f) / 64f, 0);
			if(opacity == 0) {
				drawingP2 = false;
				sendFinishedStatus = true;
			}
			
			img.setRotation(adjustedAngle);
			img.setXY(animX, animY);
			img.setAlpha(opacity);
			img.draw(gl);
		}
	}
}
