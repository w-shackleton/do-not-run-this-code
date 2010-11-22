package uk.digitalsquid.spacegame.spaceitem.assistors;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import android.graphics.Canvas;
import android.graphics.Paint.Style;

public class BhPulseInfo
{
	protected static final PaintDesc paint = new PaintDesc(255, 255, 255, 255, 1, Style.STROKE);
	protected static final int LENGTH = 20;
	
	protected boolean finished = false;
	protected float position;
	protected float extension;
	protected final Coord itemPos;
	
	public BhPulseInfo(Coord itemPos, float position)
	{
		this.itemPos = itemPos;
		this.position = position;
		extension = -position / 5;
	}
	
	/**
	 * Reset Pulse
	 */
	public void reset(float position)
	{
		finished = false;
		this.position = position;
	}

	
	/**
	 * Move position
	 * @param millis	Milliseconds to move by
	 * @return			Whether pulse has finished
	 */
	public boolean move(float millis)
	{
		position += millis / 4f;
		extension += millis * 2;
		
		if(extension > 128)
			finished = true;
		if(finished)
			return true;
		return false;
	}
	
	public void draw(Canvas c, float worldScale)
	{
		if(extension < 0)
			return;
		if(finished)
			return;
		Coord p1 = new Coord(itemPos.x + extension, itemPos.y).rotate(itemPos, position * (float)Math.PI / 180);
		Coord p2 = new Coord(itemPos.x + extension + LENGTH, itemPos.y).rotate(itemPos, position * (float)Math.PI / 180);
		paint.a = (int) CompuFuncs.TrimMinMax(256 - (extension * 2), 0, 255);
		c.drawLine((float)p1.x * worldScale, (float)p1.y * worldScale, (float)p2.x * worldScale, (float)p2.y * worldScale, PaintLoader.load(paint));
	}
}
/*public class BhPulseInfo
{
	private static final Random rGen = new Random();
	private static final Paint paint = new Paint();
	static
	{
		paint.setARGB(255, 255, 255, 255);
		paint.setAntiAlias(StaticInfo.Antialiasing);
		paint.setStrokeWidth(1);
		paint.setStyle(Style.STROKE);
	}
	private float pos, size;
	private float time;
	private Coord itemPos;
	
	public BhPulseInfo(Coord itemPos)
	{
		this.itemPos = new Coord(itemPos);
		//this.itemPos.x += (rGen.nextInt(40) - 20) * SpaceItem.ITEM_SCALE;
		//this.itemPos.y += (rGen.nextInt(40) - 20) * SpaceItem.ITEM_SCALE;
		pos = rGen.nextInt(360);
		size = rGen.nextInt(20) + 5;
		time = rGen.nextInt(160) - 160;
		//extent = rGen.nextInt(50) + 20;
	}
	
	private boolean started = false, finished = false;
	
	/**
	 * Reset Pulse
	 * /
	public void reset()
	{
		pos = rGen.nextInt(360);
		size = rGen.nextInt(45) + 10;
		time = rGen.nextInt(160) - 160;
		//extent = rGen.nextInt(100) + 70;
		
		started = false;
		finished = false;
	}
	
	/**
	 * Move position
	 * @param millis	Milliseconds to move by
	 * @return			Whether pulse has finished
	 * /
	public boolean move(float millis)
	{
		if(time >= 0) started = true;
		if(started && time > 30) finished = true;
		time += millis / 3;
		pos += millis / 2;
		if(pos > 360)
			pos -= 360;
		if(finished) return true;
		return false;
	}
	
	public void draw(Canvas c, float worldScale)
	{
		//float extent = (extent * Math.sin((time * Math.PI) / 180) * SpaceItem.ITEM_SCALE;
		/*c.drawArc(new RectF(
				(float)(itemPos.x - extent) * worldScale,
				(float)(itemPos.y - extent) * worldScale,
				(float)(itemPos.x + extent) * worldScale,
				(float)(itemPos.y + extent) * worldScale
			),pos, size, false, paint);* /
		float time = this.time;
		if(time < 0)
			time = 0;
		float size = this.size;
		if(time == 0)
			size = 0;
		Coord p1 = new Coord(itemPos.x + time, itemPos.y).rotate(itemPos, pos * (float)Math.PI / 180);
		Coord p2 = new Coord(itemPos.x + time + size, itemPos.y).rotate(itemPos, pos * (float)Math.PI / 180);
		c.drawLine((float)p1.x * worldScale, (float)p1.y * worldScale, (float)p2.x * worldScale, (float)p2.y * worldScale, paint);
	}
}*/