package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import uk.digitalsquid.spacegame.spaceitem.SpaceItem;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import android.content.Context;
import android.graphics.Canvas;

public class Spring extends SpaceItem implements Forceful, Moveable
{
	protected static final float SPRING_FORCE = 100;
	protected static final float SPRING_AIR_RESISTANCE = .99f;
	protected static final float SPRING_BALL_AIR_RESISTANCE = .996f;
	
	protected float bounciness;
	
	Coord spring1, spring2;
	
	Coord springC;
	Coord springVC = new Coord();
	
	protected static PaintDesc paint = new PaintDesc(255, 255, 255);
	
	double Fx, Fy;
	
	/**
	 * These are all stored here as one set is needed for every spring
	 */
	public boolean springInside = false;
	/**
	 * When circle is touching line
	 */
	public boolean springSwitching = false;
	/**
	 * When circle is touching line (previous loop)
	 */
	public boolean springSwitchingPrev = false;
	
	public Spring(Context context, Coord coord1, Coord coord2, float bounciness)
	{
		super(context, coord1.add(coord2).scale(0.5f));
		spring1 = coord1.scale(ITEM_SCALE);
		spring2 = coord2.scale(ITEM_SCALE);
		
		springC = spring1.add(spring2).scale(0.5f);
		
		this.bounciness = bounciness;
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		c.drawCircle(
				(float)spring1.x * worldZoom,
				(float)spring1.y * worldZoom,
				5f,
				PaintLoader.load(paint));
		c.drawCircle(
				(float)spring2.x * worldZoom,
				(float)spring2.y * worldZoom,
				5f,
				PaintLoader.load(paint));
		c.drawLine(
				(float)spring1.x * worldZoom,
				(float)spring1.y * worldZoom,
				(float)springC.x * worldZoom,
				(float)springC.y * worldZoom,
				PaintLoader.load(paint));
		c.drawLine(
				(float)spring2.x * worldZoom,
				(float)spring2.y * worldZoom,
				(float)springC.x * worldZoom,
				(float)springC.y * worldZoom,
				PaintLoader.load(paint));
	}

	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		springSwitchingPrev = springSwitching;
		double angle1 = Math.atan2(itemC.y - spring1.y, itemC.x - spring1.x);
		double angle2 = Math.atan2(spring2.y - itemC.y, spring2.x - itemC.x);
		if(Math.abs(angle1 - angle2) < (.05 * Math.PI)) // Determine if ball just crossed line
		{
			springSwitching = true;
		}
		else // Ball is not crossing line...
		{
			springSwitching = false;
		}
		
		if(
				springInside == true &
				springSwitching == false &
				springSwitchingPrev == true) // If leaving area
		{
			springInside = false;
		}
		else if(springSwitching == false & springSwitchingPrev == true) // If entering area
		{
			springInside = true;
		}
		
		if(springInside) // Calculate Physics time! // If inside capture area already
		{
			springC = new Coord(itemC);
			springVC = new Coord(itemVC);
		}
		else // Else, simulate springback
		{
			springVC.x *= SPRING_AIR_RESISTANCE;
			springVC.y *= SPRING_AIR_RESISTANCE;
		}
		double D = spring1.minus(spring2).getLength();
		double D1 = spring1.minus(springC).getLength();
		double D2 = spring2.minus(springC).getLength();
		double F = SPRING_FORCE * (
				((D1 + D2) / D) - 1
				);
		Fx = F * bounciness * (((spring1.x - springC.x) / D1) + ((spring2.x - springC.x) / D1));
		Fy = F * bounciness * (((spring1.y - springC.y) / D1) + ((spring2.y - springC.y) / D1));
		
		if(springInside)
		{
			return new Coord(Fx, Fy);
		}
		return null;
	}

	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC, float itemRadius)
	{
		if(springInside)
		{
			return new BallData(null, itemVC.scale(SPRING_BALL_AIR_RESISTANCE));
		}
		else return null;
	}

	@Override
	public void move(float millistep, float speedScale)
	{
		springVC.x += Fx * millistep / 1000f;
		springVC.y += Fy * millistep / 1000f; // Since no Resultant Force var
		
		springC.x  += springVC.x * millistep / 1000f * speedScale;
		springC.y  += springVC.y * millistep / 1000f * speedScale;
	}
}
