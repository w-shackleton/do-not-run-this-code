package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.Bounceable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.BgType;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.FgType;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.Type;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class Planet extends Bounceable implements TopDrawable
{
	protected static final PlanetType[] PLANET_TYPES = {
		new PlanetType(Type.nobounce1,  BgType.image,	R.drawable.planet1,	FgType.image,	R.drawable.planet1p2,	0.2, 0.5, 30, 250, null),
		new PlanetType(Type.sticky1,	BgType.colour,	0,					FgType.image,	R.drawable.planet5,		0,   0.9, 40, 250, new PaintDesc(255, 128, 0)),
		new PlanetType(Type.bounce2,	BgType.colour,	0,					FgType.image,	R.drawable.planet2,		1,   0.1, 20, 200, new PaintDesc(100, 100, 255)),
		new PlanetType(Type.n1,			BgType.colour,	0,					FgType.image,	R.drawable.planet3,		0.6, 0.6, 20, 200, new PaintDesc(0, 100, 200)),
		new PlanetType(Type.n2,			BgType.colour,	0,					FgType.image,	R.drawable.planet4,		0.7, 0.7, 20, 200, new PaintDesc(0, 100, 200)),
		new PlanetType(Type.n3,			BgType.colour,	0,					FgType.image,	R.drawable.planet6,		0.8, 0.8, 20, 200, new PaintDesc(50, 60, 60)),
		new PlanetType(Type.bounce1,	BgType.none,	0,					FgType.image,	R.drawable.planetbouncy,1.3, 0.8, 20, 150, null)
	};
	
	protected PlanetType type = PLANET_TYPES[0];
	
	public static final Random rGen = new Random();
	
	private static BitmapDrawable pShade = null;
	private BitmapDrawable Bg, Fg;
	float fgRotation = 0;
	private PaintDesc p;
	
	public Planet(Context context, Coord coord, float radius, int typeId)
	{
		super(context, coord, getDensityForId(typeId), radius, getBouncinessForId(typeId));
		
		for(PlanetType t : PLANET_TYPES) {
			if(t.typeId == typeId) {
				this.type = t;
			}
		}
		
		if(pShade == null)
			pShade = (BitmapDrawable) context.getResources().getDrawable(R.drawable.planet_s2);
		
		if(type.bgType == BgType.colour)
		{
			p = type.colour1;
		}
		else
		{
			Bg = (BitmapDrawable) context.getResources().getDrawable(type.fileId1);
		}
		if(type.fgType == FgType.image)
		{
			fgRotation = (float) (rGen.nextFloat() * 360);
			Fg = (BitmapDrawable) context.getResources().getDrawable(type.fileId2);
		}
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		Rect dp = new Rect(
				(int)pos.x - (int)radius,
				(int)pos.y - (int)radius,
				(int)pos.x + (int)radius,
				(int)pos.y + (int)radius
				);
		if(type.bgType == BgType.colour)
		{
			c.drawCircle((float)pos.x, (float)pos.y, (float)radius, PaintLoader.load(p));
		}
		else
		{
			Bg.setAntiAlias(StaticInfo.Antialiasing);
			Bg.setBounds(dp);
			Bg.draw(c);
		}
		
		if(type.fgType == FgType.image)
		{
			c.rotate(fgRotation, dp.centerX(), dp.centerY());
			Fg.setAntiAlias(StaticInfo.Antialiasing);
			Fg.setBounds(dp);
			Fg.draw(c);
			c.rotate(-fgRotation, dp.centerX(), dp.centerY());
		}
	}

	@Override
	public void drawTop(Canvas c, float worldZoom)
	{
		pShade.setAntiAlias(StaticInfo.Antialiasing);
		pShade.setBounds(new Rect(
				(int)((pos.x - radius * 2) * worldZoom),
				(int)((pos.y - radius * 2) * worldZoom),
				(int)((pos.x + radius * 2) * worldZoom),
				(int)((pos.y + radius * 2) * worldZoom)));
		pShade.draw(c);
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
		
		public static enum BgType {
			none,
			colour,
			image
		}
		
		public static enum FgType {
			none,
			image
		}
		
		public final int typeId;
		public final BgType bgType;
		public final FgType fgType;
		
		public final int fileId1, fileId2;
		
		public final int minSize, maxSize;
		
		public final double bounciness, density;
		
		public final PaintDesc colour1;
		
		protected PlanetType(int typeId, BgType bg, int fileId1, FgType fg, int fileId2, double bounciness, double density, int minSize, int maxSize, PaintDesc colour1) {
			this.typeId = typeId;
			this.fileId1 = fileId1;
			this.fileId2 = fileId2;
			this.minSize = minSize;
			this.maxSize = maxSize;
			this.bounciness = bounciness;
			this.density = density;
			
			this.colour1 = colour1;
			
			bgType = bg;
			fgType = fg;
		}
	}
}
