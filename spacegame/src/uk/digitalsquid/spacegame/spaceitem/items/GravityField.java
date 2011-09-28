package uk.digitalsquid.spacegame.spaceitem.items;

import java.nio.FloatBuffer;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.Lines;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Rectangular;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;

public class GravityField extends Rectangular implements Forceful, Moveable
{
	/*
	 * This class internally uses a Lines class which stores the Y and Z of each line,
	 * and untranslatedPoints stores the incremented 'flying' X axis.
	 * This is then converted into lines through Math.cos
	 */
	protected static final float VORTEX_POW_MIN = 0.5f;
	protected static final float VORTEX_POW_MAX = 3;
	
	protected static final float VORTEX_SIZE_MIN = 10;
	protected static final float VORTEX_SIZE_MAX = 40;
	
	protected static final int NUM_LINES = 20;
	
	protected static final float GRAVITY_SPEED = 2f;
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
	public GravityField(SimulationContext context, Vec2 coord, Vec2 size, float rotation, float speed)
	{
		super(context, coord, size, 10, rotation, 1, BodyType.STATIC);
		
		if(rGen == null)
			rGen = new Random();
		
		this.size.x = CompuFuncs.TrimMinMax(this.size.x, VORTEX_SIZE_MIN, VORTEX_SIZE_MAX);
		this.size.y = CompuFuncs.TrimMinMax(this.size.y, VORTEX_SIZE_MIN, VORTEX_SIZE_MAX);
		this.speed = CompuFuncs.TrimMinMax(speed, VORTEX_POW_MIN, VORTEX_POW_MAX);
		
		bg = new RectMesh(getPosX(), getPosY(), (float)size.x, (float)size.y, 0, 0, 0, 1);
		bg.setRotation(rotation);
		
		lines = new Lines(getPosX(), getPosX(), createNewPointSet(), GL10.GL_LINES, 1, 1, 1, 1);
		lines.setRotation(rotation);
		
		for(int i = 0; i < untranslatedPoints.length; i++) {
			untranslatedPoints[i] = (float) (rGen.nextFloat() * Math.PI);
		}
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_NONE;
		fixture.getFilterData().maskBits = COLLISION_GROUP_NONE;
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
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV)
	{
		if(CompuFuncs.PointInPolygon(getRectPos(), itemC))
		{
			//Log.v("SpaceGame", "Y");
			return new Vec2(
					(float)Math.cos(getRotation() * DEG_TO_RAD) * speed * GRAVITY_SPEED,
					(float)Math.sin(getRotation() * DEG_TO_RAD) * speed * GRAVITY_SPEED);
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
	public Vec2 calculateVelocityImmutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
		return null;
	}
	
	/**
	 * Not used
	 */
	@Override
	public void calculateVelocityMutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
	}
	
	@Override
	public void move(float millistep, float speedScale) { }

	@Override
	public boolean isForceExclusive() {
		return false;
	}
}
