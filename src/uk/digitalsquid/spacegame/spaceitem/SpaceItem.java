package uk.digitalsquid.spacegame.spaceitem;

import uk.digitalsquid.spacegame.Coord;
import android.content.Context;
import android.graphics.Canvas;

public abstract class SpaceItem
{
	public static final float ITEM_SCALE = 1f;
	
	/**
	 * Multiply a number of degrees by this to convert it to radians
	 */
	protected static final float DEG_TO_RAD = (float) (Math.PI / 180);
	
	/**
	 * Multiply a number of radians by this to convert it to degrees
	 */
	protected static final float RAD_TO_DEG = (float) (180 / Math.PI);
	
	protected Context context;
	
	/**
	 * The position of this item
	 */
	protected Coord pos;
	
	public SpaceItem(Context context, Coord coord)
	{
		this.context = context;
		this.pos = coord.scale(ITEM_SCALE);
	}
	
	/**
	 * Draw this object onto the screen
	 * @param c			The canvas to draw to
	 * @param worldZoom	The current Zoom of the canvas
	 */
	public abstract void draw(Canvas c, float worldZoom);
	
	//public Coord gravFSize;
	//public double gravFDir;
	
	//public Coord spring1, spring2;
	
	//public boolean springInside = false; // These are all stored here as one set is needed for every spring
	//public boolean springSwitching = false; // When circle is touching line
	//public boolean springSwitchingPrev = false; // When circle is touching line (previous loop)
	
	//public int bhRot = 0;
	
	//public BhPulseInfo[] bhPulses;
	//public boolean bhActivated = false;
	
	//public PlanetGraphic planetImg;

	//public Coord springC = new Coord();
	//public Coord springVC = new Coord();
	
	/*public static enum Type
	{
		PLANET,
		BLACK_HOLE,
		GRAV_FIELD,
		SPRING,
	}
	
	public SpaceItem(Context context, double posX, double posY, double rad, Paint paint, double density, float bounciness) // Planet
	{
		pos = new Coord(posX * ITEM_SCALE, posY * ITEM_SCALE);
		this.rad = rad * ITEM_SCALE;
		if(paint != null)
			this.paint = paint;
		else
		{
			this.paint = new Paint();
			this.paint.setARGB(255, 255, 255, 255);
		}
		this.paint.setAntiAlias(StaticInfo.Antialiasing);
		this.density = density;
		this.bounciness = bounciness;
		this.type = Type.PLANET;
		
		/// Img decide
		if(bounciness == 0)
			planetImg = new PlanetGraphic(context, 4, (int) rad * ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 480);
		/*else if(rad >= 70)
			planetImg = new PlanetGraphic(context, 0);
		else if(rad >= 40)
			planetImg = new PlanetGraphic(context, 2);
		else if(rad >= 30)
			planetImg = new PlanetGraphic(context, 3);
		else
			planetImg = new PlanetGraphic(context, 1);* /
		else
		{
			planetImg = new PlanetGraphic(context, PlanetGraphic.rGen.nextInt(4), (int) rad * ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 480);
		}
	}
	
	public SpaceItem(Context context, double posX, double posY) // Black Hole
	{
		this.type = Type.BLACK_HOLE;
		pos = new Coord(posX * ITEM_SCALE, posY * ITEM_SCALE);
		rad = 35 * ITEM_SCALE;
		density = 0.5f;
		this.paint = new Paint();
		this.paint.setARGB(170, 128, 128, 128);
		this.paint.setAntiAlias(StaticInfo.Antialiasing);

		bhPulses = new BhPulseInfo[BH_PULSES]; // Initiate random pulses
		for(int i = 0; i < BH_PULSES; i++)
		{
			bhPulses[i] = new BhPulseInfo(pos);
		}
	}
	
	public SpaceItem(Context context, double posX, double posY, double sizeX, double sizeY, double rotation, double density) // Grav field
	{
		this.type = Type.GRAV_FIELD;
		pos = new Coord(posX * ITEM_SCALE, posY * ITEM_SCALE);
		gravFSize = new Coord(sizeX * ITEM_SCALE, sizeY * ITEM_SCALE);
		gravFDir = rotation;
		this.density = density * ITEM_SCALE;
		
		this.paint = new Paint();
		this.paint.setARGB(220, 128, 128, 255);
		this.paint.setAntiAlias(StaticInfo.Antialiasing);
	}
	
	public SpaceItem(Context context, double p1x, double p1y, double p2x, double p2y, float bounciness) // Spring
	{
		spring1 = new Coord(p1x * ITEM_SCALE, p1y * ITEM_SCALE);
		spring2 = new Coord(p2x * ITEM_SCALE, p2y * ITEM_SCALE);
		this.bounciness = bounciness;
		
		this.paint = new Paint();
		this.paint.setARGB(255, 255, 255, 255);
		this.paint.setStrokeWidth(3);
		this.paint.setAntiAlias(StaticInfo.Antialiasing);
		
		this.type = Type.SPRING;
		
		this.pos = new Coord();
		this.springC = new Coord((p1x + p2x) * ITEM_SCALE / 2, (p1y + p2y) * ITEM_SCALE / 2); // To stop spring bouncing about at start.
		this.rad = (float)5 * ITEM_SCALE;
	}
	
	public Coord[] getRectPos()
	{
		Coord[] ret = new Coord[4];
		ret[0] = new Coord(
				pos.x + CompuFuncs.RotateX(-gravFSize.x / 2, -gravFSize.y / 2, gravFDir),
				pos.y + CompuFuncs.RotateY(-gravFSize.x / 2, -gravFSize.y / 2, gravFDir));
		ret[1] = new Coord(
				pos.x + CompuFuncs.RotateX(+gravFSize.x / 2, -gravFSize.y / 2, gravFDir),
				pos.y + CompuFuncs.RotateY(+gravFSize.x / 2, -gravFSize.y / 2, gravFDir));
		ret[2] = new Coord(
				pos.x + CompuFuncs.RotateX(+gravFSize.x / 2, +gravFSize.y / 2, gravFDir),
				pos.y + CompuFuncs.RotateY(+gravFSize.x / 2, +gravFSize.y / 2, gravFDir));
		ret[3] = new Coord(
				pos.x + CompuFuncs.RotateX(-gravFSize.x / 2, +gravFSize.y / 2, gravFDir),
				pos.y + CompuFuncs.RotateY(-gravFSize.x / 2, +gravFSize.y / 2, gravFDir));
		return ret;
	}*/
}
