package uk.digitalsquid.spacegame.spaceitem.assistors;

import java.util.Random;

import uk.digitalsquid.spacegame.PaintLoader;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.PaintLoader.PaintDesc;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;

public class PlanetGraphic
{
	PaintDesc p;
	int plan;
	float rot = 0;
	
	public static final Random rGen = new Random();
	private static BitmapDrawable pShade = null;
	private BitmapDrawable Bg, Fg;
	
	public PlanetGraphic(Context context, int plan, int radius)
	{
		if(plan >= Types.length)
			plan = Types.length - 1;
		this.plan = plan;
		
		if(Types[plan].requiresColBg)
		{
			p = Types[plan].BgRandCol[rGen.nextInt(Types[plan].BgRandCol.length)]; // Random col
		}
		else
		{
			Bg = (BitmapDrawable) context.getResources().getDrawable(Types[plan].BgId);
		}
		if(Types[plan].hasFg)
		{
			rot = (float) (rGen.nextFloat() * 360);
			Fg = (BitmapDrawable) context.getResources().getDrawable(Types[plan].FgId);
		}
		
		if(pShade == null)
			pShade = (BitmapDrawable) context.getResources().getDrawable(R.drawable.planet_s2);
	}
	
	public final void Draw(Canvas c, Rect dp)
	{
		if(Types[plan].requiresColBg)
		{
			c.drawCircle(dp.centerX(), dp.centerY(), dp.width() / 2, PaintLoader.load(p));
		}
		else
		{
			Bg.setAntiAlias(StaticInfo.Antialiasing);
			Bg.setBounds(dp);
			Bg.draw(c);
		}
		
		if(Types[plan].hasFg)
		{
			c.rotate(rot, dp.centerX(), dp.centerY());
			Fg.setAntiAlias(StaticInfo.Antialiasing);
			Fg.setBounds(dp);
			Fg.draw(c);
			c.rotate(-rot, dp.centerX(), dp.centerY());
		}
	}
	
	public final void DrawTop(Canvas c, Rect dp)
	{
		pShade.setAntiAlias(StaticInfo.Antialiasing);
		pShade.setBounds(new Rect(
				dp.left - dp.width() / 2,
				dp.top - dp.height() / 2,
				dp.right + dp.width() / 2,
				dp.bottom + dp.height() / 2
				));
		pShade.draw(c);
	}
	
	public static class PlanetData
	{
		boolean hasFg;
		boolean requiresColBg;
		PaintDesc[] BgRandCol;
		int BgId, FgId;
		//Bitmap Bg = null, Fg = null; // Constructed later
		
		public PlanetData(PaintDesc[] bgCols, int fg)
		{
			BgRandCol = bgCols;
			
			FgId = fg;
			hasFg = true;
			requiresColBg = true;
		}
		
		public PlanetData(int fg, int bg)
		{
			BgId = bg;
			FgId = fg;
			hasFg = true;
			requiresColBg = false;
		}
		
		public PlanetData(int bg)
		{
			BgId = bg;
			hasFg = false;
			requiresColBg = false;
		}
	}
	public static final PlanetData[] Types = new PlanetData[] {
			new PlanetData(R.drawable.planet1p2, R.drawable.planet1),
			new PlanetData(new PaintDesc[] {
					newPaint(100, 100, 255),
					newPaint(150, 170, 255),
					newPaint(0, 100, 200),
			}, R.drawable.planet2),
			new PlanetData(new PaintDesc[] {
					newPaint(0, 100, 200),
					newPaint(0, 175, 255),
					newPaint(0, 210, 220),
					
					newPaint(255, 80, 0),
					newPaint(255, 182, 0),
					newPaint(255, 220, 71),
					newPaint(240, 90, 60),
			}, R.drawable.planet3),
			new PlanetData(new PaintDesc[] {
					newPaint(0, 100, 200),
					newPaint(0, 175, 255),
					newPaint(0, 210, 220),

					newPaint(100, 150, 50),
					newPaint(255, 0, 0),
					newPaint(150, 90, 100),
			}, R.drawable.planet4),
			new PlanetData(new PaintDesc[] {
					newPaint(50, 60, 60),
					newPaint(200, 200, 220),
					newPaint(20, 240, 30),
			}, R.drawable.planet6),
			new PlanetData(new PaintDesc[] {
					newPaint(255, 0, 0),
					newPaint(255, 100, 0),
					newPaint(255, 200, 0),
			}, R.drawable.planet5),
	};
	
	public static final PaintDesc newPaint(int r, int g, int b)
	{
		return new PaintDesc(r, g, b);
	}
}
