package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.Bounceable;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.FgType;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.Type;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Planet extends Bounceable
{
	protected static final PlanetType[] PLANET_TYPES = {
		new PlanetType(Type.nobounce1,	FgType.image,	R.drawable.planet1,		0.2, 0.5, 30, 250, null),
		new PlanetType(Type.sticky1,	FgType.image,	R.drawable.planet5,		0,   0.9, 40, 250, null),
		new PlanetType(Type.bounce2,	FgType.image,	R.drawable.planet2,		1,   0.1, 20, 200, null),
		new PlanetType(Type.n1,			FgType.image,	R.drawable.planet3,		0.6, 0.6, 20, 200, null),
		new PlanetType(Type.n2,			FgType.image,	R.drawable.planet4,		0.7, 0.7, 20, 200, null),
		new PlanetType(Type.n3,			FgType.image,	R.drawable.planet6,		0.8, 0.8, 20, 200, null),
		new PlanetType(Type.bounce1,	FgType.image,	R.drawable.planetbouncy,1.3, 0.8, 20, 150, null)
	};
	
	protected PlanetType type = PLANET_TYPES[0];
	
	public static final Random rGen = new Random();
	
	private BitmapDrawable Fg;
	float fgRotation = 0;
	
	public Planet(Context context, Coord coord, float radius, int typeId)
	{
		super(context, coord, getDensityForId(typeId), radius, getBouncinessForId(typeId));
		
		for(PlanetType t : PLANET_TYPES) {
			if(t.typeId == typeId) {
				this.type = t;
			}
		}
		
		this.radius = CompuFuncs.TrimMinMax(this.radius, type.minSize, type.maxSize);
		
		if(type.fgType == FgType.image)
		{
			// fgRotation = (float) (rGen.nextFloat() * 360);
			Fg = (BitmapDrawable) context.getResources().getDrawable(type.fileId2);
		}
	}
	
	private Rect drawTmpRect = new Rect();

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		drawTmpRect.left = (int)pos.x - (int)radius;
		drawTmpRect.top = (int)pos.y - (int)radius;
		drawTmpRect.right = (int)pos.x + (int)radius;
		drawTmpRect.bottom = (int)pos.y + (int)radius;
		
		if(type.fgType == FgType.image)
		{
			// c.rotate(fgRotation, drawTmpRect.centerX(), drawTmpRect.centerY());
			Fg.setAntiAlias(StaticInfo.Antialiasing);
			Fg.setBounds(drawTmpRect);
			Fg.draw(c);
			// c.rotate(-fgRotation, drawTmpRect.centerX(), drawTmpRect.centerY());
		}
	}
	
	/**
	 * Used by constructor to find info
	 * @param typeId
	 * @return
	 */
	protected static final float getBouncinessForId(int typeId) {
		for(PlanetType t : PLANET_TYPES) {
			if(t.typeId == typeId) {
				return (float) t.bounciness;
			}
		}
		return (float) PLANET_TYPES[0].bounciness;
	}
	
	/**
	 * Used by constructor to find info
	 * @param typeId
	 * @return
	 */
	protected static final float getDensityForId(int typeId) {
		for(PlanetType t : PLANET_TYPES) {
			if(t.typeId == typeId) {
				return (float) t.density;
			}
		}
		return (float) PLANET_TYPES[0].density;
	}

	public static class PlanetType {
		
		public static final class Type {
			public static final int n1 = 0;
			public static final int n2 = 1;
			public static final int n3 = 2;
			public static final int sticky1 = 3;
			public static final int nobounce1 = 4;
			public static final int bounce1 = 5;
			public static final int bounce2 = 6;
		}
		
		public static enum FgType {
			none,
			image
		}
		
		public final int typeId;
		public final FgType fgType;
		
		public final int fileId2;
		
		public final int minSize, maxSize;
		
		public final double bounciness, density;
		
		public final PaintDesc colour1;
		
		protected PlanetType(int typeId, FgType fg, int fileId2, double bounciness, double density, int minSize, int maxSize, PaintDesc colour1) {
			this.typeId = typeId;
			this.fileId2 = fileId2;
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.bounciness = bounciness;
			this.density = density;
			
			this.colour1 = colour1;
			
			fgType = fg;
		}
	}
}
