package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.Spherical;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.LevelAffectable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;

public class Star extends Spherical implements LevelAffectable, Forceful
{
	private static final int STAR_RADIUS = 15;
	
	private static BitmapDrawable img;
	
	private boolean available = true;
	private boolean sendStatus = false;
	
	public Star(Context context, Coord coord)
	{
		super(context, coord, STAR_RADIUS);
		
		img = (BitmapDrawable) context.getResources().getDrawable(R.drawable.star);
	}
	
	@Override
	public void draw(Canvas c, float worldZoom)
	{
		if(available) {
			img.setAntiAlias(StaticInfo.Antialiasing);
			img.setBounds(
					(int)(pos.x - radius * worldZoom),
					(int)(pos.y - radius * worldZoom),
					(int)(pos.x + radius * worldZoom),
					(int)(pos.y + radius * worldZoom));
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
		
		return null;
	}
	
	public boolean isAvailable() {
		return available;
	}
}
