package uk.digitalsquid.spacegame.spaceitem.items;

import android.content.Context;
import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.Spherical;

public abstract class PlayerBase extends Spherical {
	public static final float BALL_RADIUS = 14 * ITEM_SCALE;
	
	public final Coord itemC = new Coord(), itemVC = new Coord(), itemRF = new Coord(); // For portability to old code

	public PlayerBase(Context context, Coord coord, float radius) {
		super(context, coord, radius);
	}

}
