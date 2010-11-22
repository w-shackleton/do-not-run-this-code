package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.spaceitem.Bounceable;
import uk.digitalsquid.spacegame.spaceitem.assistors.PlanetGraphic;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.WindowManager;

public class Planet extends Bounceable implements TopDrawable
{
	PlanetGraphic planetImg;
	
	public Planet(Context context, Coord coord, float radius, float density,
			float bounciness)
	{
		super(context, coord, density, radius, bounciness);
		
		if(radius < 10)
			radius = 10;
		if(bounciness == 0)
			planetImg = new PlanetGraphic(context, 4, (int) radius * ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 480);
		else
		{
			planetImg = new PlanetGraphic(context, PlanetGraphic.rGen.nextInt(4), (int) radius * ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth() / 480);
		}
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		planetImg.Draw(c, new Rect(
				(int)((pos.x - radius) * worldZoom),
				(int)((pos.y - radius) * worldZoom),
				(int)((pos.x + radius) * worldZoom),
				(int)((pos.y + radius) * worldZoom)));
	}

	@Override
	public void drawTop(Canvas c, float worldZoom)
	{
		planetImg.DrawTop(c, new Rect(
				(int)((pos.x - radius) * worldZoom),
				(int)((pos.y - radius) * worldZoom),
				(int)((pos.x + radius) * worldZoom),
				(int)((pos.y + radius) * worldZoom)));
	}

}
