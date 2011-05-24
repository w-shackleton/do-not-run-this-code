package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;
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
	/*
	 * This class internally uses a Lines class which stores the Y and Z of each line,
	 * and untranslatedPoints stores the incremented 'flying' X axis.
	 * This is then converted into lines through Math.cos
	 */
	protected static final float VORTEX_POW_MIN = 0.5f;
	protected static final float VORTEX_POW_MAX = 3;
	
	protected static final int VORTEX_SIZE_MIN = 100;
	protected static final int VORTEX_SIZE_MAX = 400;
	
	protected static final int NUM_LINES = 20;
	
	protected static final float GRAVITY_SPEED = 20;
	protected static final float LINE_SPEED = 0.05f;
	protected static final float LINE_LENGTH_IN_PI = (float) (0.1f * Math.PI);
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
		
		if(rGen == null)
			rGen = new Random();
		
		this.size.x = CompuFuncs.TrimMinMax(this.size.x, VORTEX_SIZE_MIN, VORTEX_SIZE_MAX);
		this.size.y = CompuFuncs.TrimMinMax(this.size.y, VORTEX_SIZE_MIN, VORTEX_SIZE_MAX);
		this.speed = CompuFuncs.TrimMinMax(speed, VORTEX_POW_MIN, VORTEX_POW_MAX);
		
		bg = new RectMesh((float)pos.x, (float)pos.y, (float)size.x, (float)size.y, 0, 0, 0, 1);
		bg.setRotation(rotation);
		
		lines = new Lines((float)pos.x, (float)pos.y, createNewPointSet(), GL10.GL_LINES, 1, 1, 1, 1);
		lines.setRotation(rotation);
		
		for(int i = 0; i < untranslatedPoints.length; i++) {
			untranslatedPoints[i] = (float) (rGen.nextFloat() * Math.PI);
		}
	}
	
	private final float[] createNewPointSet() {
		float[] f = new float[NUM_LINES * 6]; // 6 because 2 points per line
		
		for(int i = 0; i < f.length; i += 6) {
			f[i+1] = (float) (rGen.nextFloat() * size.y - (size.y / 2));
			f[i+4] = f[i+1]; // Set line vpos
			f[i+2] = 0;
			f[i+5] = 0;
		}
		
		return f;
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
		lines.draw(gl);
	}

	@Override
	public void drawMove(float millistep, float speedScale)
	{
		FloatBuffer fb = lines.getVertices();
		for(int i = 0; i < untranslatedPoints.length; i++)
		{
			untranslatedPoints[i] += LINE_SPEED * speed;
			if(untranslatedPoints[i] > Math.PI) {
				untranslatedPoints[i] = -LINE_LENGTH_IN_PI; // Reset when reaches end
				
				float newY = (float) (rGen.nextFloat() * size.y - (size.y / 2));
				fb.put(i * 6 + 1, newY); // Reposition Y
				fb.put(i * 6 + 4, newY);
			}
			
			fb.put(i * 6 + 0, (float) (-Math.cos(CompuFuncs.TrimMin(untranslatedPoints[i]					 , 0))		 * size.x / 2));
			fb.put(i * 6 + 3, (float) (-Math.cos(CompuFuncs.TrimMax(untranslatedPoints[i] + LINE_LENGTH_IN_PI, Math.PI)) * size.x / 2));
		}
	}
	
	/**
	 * Not used
	 */
	@Override
	public BallData calculateVelocityImmutable(Coord itemC, Coord itemVC, float itemRadius, boolean testRun) {
		return null;
	}
	
	/**
	 * Not used
	 */
	@Override
	public void calculateVelocityMutable(Coord itemC, Coord itemVC, float itemRadius) {
	}
	
	@Override
	public void move(float millistep, float speedScale) { }
}
