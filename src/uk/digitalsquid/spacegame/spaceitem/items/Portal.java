package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Portal extends Gravitable implements Moveable {
	
	private static final int PORTAL_RADIUS = 70;
	private static final float PORTAL_DENSITY = .8f;

	private final Drawable img;
	
	private float rotation = 0;
	
	public Portal(Context context, Coord coord) {
		super(context, coord, 0, PORTAL_DENSITY, PORTAL_RADIUS);
		
		img = (BitmapDrawable) context.getResources().getDrawable(R.drawable.portal);
	}

	@Override
	public void draw(Canvas c, float worldZoom) {
		c.rotate(rotation, (float)pos.x, (float)pos.y);
		img.setBounds(
				(int)((pos.x - (radius)) * worldZoom),
				(int)((pos.y - (radius)) * worldZoom),
				(int)((pos.x + (radius)) * worldZoom),
				(int)((pos.y + (radius)) * worldZoom));
		img.draw(c);
		c.rotate(-rotation, (float)pos.x, (float)pos.y);
	}

	@Override
	public void move(float millistep, float speedScale) {
		rotation--;
	}
}
