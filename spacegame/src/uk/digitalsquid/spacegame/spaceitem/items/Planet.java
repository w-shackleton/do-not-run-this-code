package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.FgType;
import uk.digitalsquid.spacegame.spaceitem.items.Planet.PlanetType.Type;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Gravitable;

public class Planet extends Gravitable
{
	protected static final PlanetType[] PLANET_TYPES = {
		new PlanetType(Type.nobounce1,	FgType.image,	R.drawable.planet1,		0.2f, 0.5f, 3f, 25f, null),
		new PlanetType(Type.sticky1,	FgType.image,	R.drawable.planet5,		0f,   0.9f, 4f, 25f, null),
		new PlanetType(Type.bounce2,	FgType.image,	R.drawable.planet2,		1f,   0.1f, 2f, 20f, null),
		new PlanetType(Type.n1,			FgType.image,	R.drawable.planet3,		0.6f, 0.6f, 2f, 20f, null),
		new PlanetType(Type.n2,			FgType.image,	R.drawable.planet4,		0.7f, 0.7f, 2f, 20f, null),
		new PlanetType(Type.n3,			FgType.image,	R.drawable.planet6,		0.8f, 0.8f, 2f, 20f, null),
		new PlanetType(Type.bounce1,	FgType.image,	R.drawable.planetbouncy,1.3f, 0.8f, 2f, 15f, null)
	};
	
	protected PlanetType type = PLANET_TYPES[0];
	
	public static final Random rGen = new Random();
	
	private RectMesh fg;
	float fgRotation = 0;
	
	public Planet(SimulationContext context, Vec2 coord, float radius, int typeId)
	{
		super(context, coord, 1, getDensityForId(typeId) * 10, radius, BodyType.STATIC);
		
		fixture.setRestitution(getBouncinessForId(typeId));
		// fixture.setFriction(CompuFuncs.TrimMinMax(1 - getBouncinessForId(typeId), 0, 1));
		
		for(PlanetType t : PLANET_TYPES) {
			if(t.typeId == typeId) {
				this.type = t;
			}
		}
		
		setRadius(CompuFuncs.TrimMinMax(getRadius(), type.minSize, type.maxSize));
		
		if(type.fgType == FgType.image)
		{
			// fgRotation = (float) (rGen.nextFloat() * 360);
			fg = new RectMesh((float)getPos().x, (float)getPos().y, radius * 2, radius * 2, type.fileId2);
		}
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_PLAYER;
		fixture.getFilterData().maskBits = COLLISION_GROUP_PLAYER;
	}
	
	@Override
	public void draw(GL10 gl, float worldZoom) {
		if(type.fgType == FgType.image) {
			fg.setXY(getPosX(), getPosY());
			fg.setRotation(getRotation());
			fg.draw(gl);
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
		
		public final float minSize, maxSize;
		
		public final float bounciness, density;
		
		public final PaintDesc colour1;
		
		protected PlanetType(int typeId, FgType fg, int fileId2, float bounciness, float density, float minSize, float maxSize, PaintDesc colour1) {
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

	@Override
	public boolean isForceExclusive() {
		return false;
	}
}
