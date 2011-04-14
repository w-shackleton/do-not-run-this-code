package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Portal extends Gravitable implements Moveable {
	
	private static final int PORTAL_RADIUS = 70;
	private static final float PORTAL_DENSITY = .8f;
	private static final float PORTAL_NORMAL_DENSITY = 0;
	private static final float PORTAL_NORMAL_RADIUS = 0;
	
	private static enum Status {
		DISABLED,
		OPENING,
		ENABLED
	}
	
	private Status status = Status.DISABLED;

	private final Drawable img;
	
	private float rotation = 0;
	
	private final Path circleClip;
	
	public Portal(Context context, Coord coord) {
		super(context, coord, 0.75f, PORTAL_NORMAL_DENSITY, PORTAL_NORMAL_RADIUS);
		
		circleClip = new Path();
		
		img = (BitmapDrawable) context.getResources().getDrawable(R.drawable.portal);
	}

	@Override
	public void draw(Canvas c, float worldZoom) {
		if(status == Status.OPENING || status == Status.ENABLED) {
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
	}

	@Override
	public void move(float millistep, float speedScale) {
		rotation -= 3;
		if(status == Status.OPENING) {
			radius  += (float)(PORTAL_RADIUS  - radius ) / 100f;
			density += (float)(PORTAL_DENSITY - density) / 100f;
			
			if(PORTAL_RADIUS - radius < 1) {
				radius = PORTAL_RADIUS;
				density = PORTAL_DENSITY;
				status = Status.ENABLED;
			}
		}
	}
	
	public void activate() {
		status = Status.OPENING;
	}
}
