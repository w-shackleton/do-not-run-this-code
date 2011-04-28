package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
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
	
	protected static final float GRAVITY_SPEED = 20;
	protected static final float LINE_SPEED = 0.07f;
	protected float speed;
	
	protected List<LineInfo> lines = new ArrayList<LineInfo>();
	
	protected static Random rGen = null;
	
	protected static final PaintDesc bgPaint = new PaintDesc(0, 0, 0);
	
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
		
		if(rGen == null)
			rGen = new Random();
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
	
	private final Coord tmpP1 = new Coord();
	private final Coord tmpP2 = new Coord();

	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		c.rotate(
				(float)(rotation),
				(float)pos.x * worldZoom,
				(float)pos.y * worldZoom);
		c.drawRect(getRectF(), PaintLoader.load(bgPaint));
		c.rotate(
				(float)(-rotation),
				(float)pos.x * worldZoom,
				(float)pos.y * worldZoom);
		
		for(int i = 0; i < lines.size(); i++)
		{
			LineInfo line = lines.get(i);
			
			tmpP1.y = line.x * size.x;
			tmpP2.y = line.x * size.x;

			tmpP1.x = (-Math.cos(((line.y < 0) ? 0 : line.y) * DEG_TO_RAD) / 2 + .5) * size.y; // If less than 0, set to 0.
			tmpP2.x = (-Math.cos(
					(
							(line.y + line.length < 180) ?
									(line.y + line.length) :
									180
							) * DEG_TO_RAD
					) / 2 + .5) * size.y; // If line position + size > 180, put it to 180.
			size.scaleThis(0.5f);
			tmpP1.minusThis(size); // Move to centre around middle of gravity field
			tmpP2.minusThis(size); // Move to centre around middle of gravity field
			size.scaleThis(2);
			tmpP1.addThis(pos);
			tmpP2.addThis(pos);
			tmpP1.rotateThis(pos, rotation * DEG_TO_RAD);
			tmpP2.rotateThis(pos, rotation * DEG_TO_RAD);
			
			//Log.v("SpaceGame", "p1: " + p1 + ", p2: " + p2);
			
			LineInfo.LinePaint.stroke = line.lineSize;
			c.drawLine(
					(float)tmpP1.x * worldZoom,
					(float)tmpP1.y * worldZoom,
					(float)tmpP2.x * worldZoom,
					(float)tmpP2.y * worldZoom,
					PaintLoader.load(LineInfo.LinePaint));
		}
		/*for(int i = (int) (pos.x - (size.x / 2)); i < pos.x + (size.x / 2); i += 12)
		{
			for(int j = (int) (pos.y - (size.y / 2)); j < pos.y + (size.y / 2); j += 12)
			{
				if(CompuFuncs.PointInPolygon(getRectPos(), new Coord(i, j)))
				{
					c.drawPoint(i * worldZoom, j * worldZoom, LineInfo.LinePaint);
				}
			}
		}*/
		
		/*Coord[] points = getRectPos();
		c.drawLine(
				(float)points[0].x * worldZoom,
				(float)points[0].y * worldZoom,
				(float)points[1].x * worldZoom,
				(float)points[1].y * worldZoom, LineInfo.LinePaint);
		c.drawLine(
				(float)points[1].x * worldZoom,
				(float)points[1].y * worldZoom,
				(float)points[2].x * worldZoom,
				(float)points[2].y * worldZoom, LineInfo.LinePaint);
		c.drawLine(
				(float)points[2].x * worldZoom,
				(float)points[2].y * worldZoom,
				(float)points[3].x * worldZoom,
				(float)points[3].y * worldZoom, LineInfo.LinePaint);
		c.drawLine(
				(float)points[3].x * worldZoom,
				(float)points[3].y * worldZoom,
				(float)points[0].x * worldZoom,
				(float)points[0].y * worldZoom, LineInfo.LinePaint);
		
		/*for(int i = -200; i < 200; i += 12)
		{
			for(int j = -200; j < 200; j += 12)
			{
				if(CompuFuncs.PointInPolygon(getRectPos(), new Coord(i, j)))
				{
					c.drawPoint(i * worldZoom, j * worldZoom, LineInfo.LinePaint);
				}
			}
		}*/
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
	
	protected static class LineInfo
	{
		public float x, y;
		public float length;
		
		public float lineSize;
		
		public static PaintDesc LinePaint = new PaintDesc(255, 255, 255, 255, 0.8f);
		
		/**
		 * Manual constructor
		 * @param x The position, from 0 to 1, of the line
		 * @param length The length of the line, in degrees from 0 to 180
		 * @param lineSize The size of the brush used to paint the line.
		 */
		public LineInfo(float x, float length, float lineSize)
		{
			this.y = -length;
			this.x = x;
			this.lineSize = lineSize;
			this.length = length;
		}
		
		public LineInfo()
		{
			x = rGen.nextFloat();
			length = rGen.nextInt(30);
			lineSize = rGen.nextFloat() + 1;
			y = -length;
		}
	}
}
