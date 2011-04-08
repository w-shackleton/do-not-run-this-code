package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
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
	
	private float drawRadius, drawRadius2 = 1;
	
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
		
		if(!available && drawingP2) {
			drawRadius2++;
		}
		if(drawRadius2 > 32) {
			drawingP2 = false;
		}
		
		return null;
	}
	
	public boolean isAvailable() {
		return available;
	}

	@Override
	public void drawStatic(Canvas c, final float worldZoom, final int width, final int height,
			final Matrix matrix) {
		if(drawingP2) {
			img.setAntiAlias(StaticInfo.Antialiasing);
			img.setAlpha(128);
			img.setBounds(
					(int)(pos.x - drawRadius2 * worldZoom),
					(int)(pos.y - drawRadius2 * worldZoom),
					(int)(pos.x + drawRadius2 * worldZoom),
					(int)(pos.y + drawRadius2 * worldZoom));
			img.draw(c);
		}
	}
}
