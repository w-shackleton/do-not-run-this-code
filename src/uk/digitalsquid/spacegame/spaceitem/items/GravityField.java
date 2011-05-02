package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.misc.Lines;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.Rectangular;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;

public class GravityField extends Rectangular implements Forceful, Moveable
{
	protected static final float VORTEX_POW_MIN = 0.5f;
	protected static final float VORTEX_POW_MAX = 3;
	
	protected static final int VORTEX_SIZE_MIN = 100;
	protected static final int VORTEX_SIZE_MAX = 400;
	
	protected static final int NUM_LINES = 20;
	
	protected static final float GRAVITY_SPEED = 20;
	protected static final float LINE_SPEED = 0.07f;
	protected float speed;
	
	protected static Random rGen = null;
	
	protected static final PaintDesc bgPaint = new PaintDesc(0, 0, 0);
	
	private final RectMesh bg;
	private final Lines lines;
	
	private final float[] untranslatedPoints = new float[NUM_LINES];
	
	/**
	 * @param coord		Centre position of the rectangle
	 * @param size		Size of the rectangle
	 * @param rotation	Rotation of rectangle, in DEGREES
	 */
	public GravityField(Context context, Coord coord, Coord size, float rotation, float speed)
	{
		super(context, coord, size, rotation);
		this.size.x = CompuFuncs.TrimMinMax(this.size.x, VORTEX_SIZE_MIN, VORTEX_SIZE_MAX);
		this.size.y = CompuFuncs.TrimMinMax(this.size.y, VORTEX_SIZE_MIN, VORTEX_SIZE_MAX);
		this.speed = CompuFuncs.TrimMinMax(speed, VORTEX_POW_MIN, VORTEX_POW_MAX);
		
		bg = new RectMesh((float)pos.x, (float)pos.y, (float)size.x, (float)size.y, 0, 0, 0, 1);
		bg.setRotation(rotation);
		
		lines = new Lines((float)pos.x, (float)pos.y, new float[NUM_LINES * 6], GL10.GL_LINES, 1, 1, 1, 1);
		
		if(rGen == null)
			rGen = new Random();
		
		for(int i = 0; i < untranslatedPoints.length; i++) {
			untranslatedPoints[i] = (float) (rGen.nextFloat() * size.x - (size.x / 2));
		}
	}

	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		if(CompuFuncs.PointInPolygon(getRectPos(), itemC))
		{
			//Log.v("SpaceGame", "Y");
			return new Coord(
					Math.cos(rotation * DEG_TO_RAD) * speed * GRAVITY_SPEED,
					Math.sin(rotation * DEG_TO_RAD) * speed * GRAVITY_SPEED);
		}
		//else
			//Log.v("SpaceGame", "N");
		return null;
	}
	
	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		bg.draw(gl);
	}

	@Override
	public void move(float millistep, float speedScale)
	{
		for(int i = 0; i < lines.size(); i++)
		{
			LineInfo line = lines.get(i);
			//Log.v("SpaceGame", "Line " + i + " is at " + line.y + ".");
			if(line.y > 180)
			{
				lines.remove(i);
				continue;
			}
			
			line.y += millistep * LINE_SPEED * speed;
		}
		
		if(rGen.nextInt(7) == 3) // Randomly add new line
		{
			lines.add(new LineInfo());
		}
	}
	
	/**
	 * Not used
	 */
	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		return null;
	}
}
