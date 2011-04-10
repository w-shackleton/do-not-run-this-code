package uk.digitalsquid.spacegame.spaceitem;

import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import uk.digitalsquid.spacegame.Coord;

public class CompuFuncs
{
	public static final double GRAVCONST = .1f;

	public static final double computeForceX(
			double planetX,			// ForceX = G m x 
			double planetY,			//          -----
			double planetDensity,	//           r^3
			double planetRad,		// G = grav constant, m = planet mass
			double itemX,			// r = distance from planet to object, x = x distance from planet to object
			double itemY)			// computeForceY is the same.
	{
		if(Math.pow(planetX - itemX, 2) + Math.pow(planetY - itemY, 2) < planetRad * planetRad)
			planetRad = Math.sqrt(Math.pow(planetX - itemX, 2) + Math.pow(planetY - itemY, 2));
		double r = Math.sqrt(
				Math.pow(
						planetX - itemX,
						2
						) +
				Math.pow(
						planetY - itemY,
						2
						)
				);
		return
			GRAVCONST * (planetX - itemX) *
			computeWeight(planetRad,planetDensity) /
			(r * r * Math.sqrt(r));
	}
	
	public static final double computeForceY(
			double planetX,
			double planetY,
			double planetDensity,
			double planetRad,
			double itemX,
			double itemY)
	{
		if(Math.pow(planetX - itemX, 2) + Math.pow(planetY - itemY, 2) < planetRad * planetRad)
			planetRad = Math.sqrt(Math.pow(planetX - itemX, 2) + Math.pow(planetY - itemY, 2));
		double r = Math.sqrt(
		Math.pow(
				planetX - itemX,
				2
				) +
		Math.pow(
				planetY - itemY,
				2
				)
		);
		return
			GRAVCONST * (planetY - itemY) * 
			computeWeight(planetRad,planetDensity) /
			(r * r * Math.sqrt(r));
	}
	/* Old version - didn't work
	public static final double computeForceY(
			double planetX,
			double planetY,
			double planetDensity,
			double planetRad,
			double itemX,
			double itemY)
	{
		if(Math.pow(planetX - itemX, 2) + Math.pow(planetY - itemY, 2) < planetRad * planetRad)
			planetRad = Math.sqrt(Math.pow(planetX - itemX, 2) + Math.pow(planetY - itemY, 2));
		return
			GRAVCONST *
			computeWeight(planetRad,planetDensity) /
			Math.pow(
					Math.pow(
							planetX - itemX,
							2
							) +
					Math.pow(
							planetY - itemY,
							2
							),
					3 / 2
					) *
					(planetY - itemY);
	}*/
	
	public static final double computeWeight(
			double rad,
			double den)
	{
		return (double) (rad * rad * den * 300);
	}
	
	public static final double TrimMin(double num, double min)
	{
		if(num < min)
			return min;
		return num;
	}
	
	public static final int TrimMin(int num, int min)
	{
		if(num < min)
			return min;
		return num;
	}
	
	public static final double TrimMax(double num, double max)
	{
		if(num > max)
			return max;
		return num;
	}
	
	public static final double TrimMinMax(double num, double min, double max)
	{
		if(num < min)
			return min;
		if(num > max)
			return max;
		return num;
	}
	
	public static final float TrimMinMax(float num, float min, float max)
	{
		if(num < min)
			return min;
		if(num > max)
			return max;
		return num;
	}
	
	public static final double RotateX(double x, double y, double rot)
	{
		return Math.sqrt((x * x) + (y * y)) * Math.cos(rot + Math.atan2(y, x));
	}
	
	public static final double RotateY(double x, double y, double rot)
	{
		return Math.sqrt((x * x) + (y * y)) * Math.sin(rot + Math.atan2(y, x));
	}
	
	public static final Coord[] RotateCoords(Coord[] old, Coord orig, float rot)
	{
		Coord[] ret = new Coord[old.length];
		for(int i = 0; i < old.length; i++)
			ret[i] = old[i].rotate(orig, rot);
		return ret;
	}
	
	/*public static final Coord PointInArea(Coord orig, float origScale, Coord tl, Coord br, Coord gs) // Top-left, bottom-right & grid size of stars
	{
		if(orig.x < tl.x)
		{
			while(orig.x < tl.x)
				orig.x += gs.x * origScale;
		}
		else if(orig.x > br.x)
		{
			while(orig.x > br.x)
				orig.x -= gs.x * origScale;
		}
		if(orig.y < tl.y)
		{
			while(orig.y < tl.y)
				orig.y += gs.y * origScale;
		}
		else if(orig.y > br.y)
		{
			while(orig.y > br.y)
				orig.y -= gs.y * origScale;
		}
		return orig;
	}*/
	
	/**
	 * Check of point is inside the polygon points
	 */
	public static final boolean PointInPolygon(Coord points[], Coord point)
	{
		int i, j;
		boolean c = false;
		
		for (i = 0, j = points.length - 1; i < points.length; j = i++)
		{
			//Log.v("SpaceGame", "" + i + ": " + points[i] + ", " + j + ": " + points[j]);
			if (( ((points[i].y<=point.y) && (point.y<points[j].y)) || ((points[j].y<=point.y) && (point.y<points[i].y)) ) &&
					(point.x < (points[j].x - points[i].x) * (point.y - points[i].y) / (points[j].y - points[i].y) + points[i].x))
			//{
				c = !c;
			//	Log.v("SpaceGame", "          is ON  line");
			//}
			//else
				//Log.v("SpaceGame", "          is OFF line");
		}
		//Log.v("SpaceGame", " ");
		//Log.v("SpaceGame", " ");
		return c;
	}
	/*public static final boolean PointInPolygon(Coord points[], Coord point)
	{
		int i, j;
		boolean c = false;
		for (i = 0, j = points.length - 1; i < points.length; j = i++)
		{
			if (( ((points[i].y<=point.y) && (point.y<points[i].y)) || ((points[i].y<=point.y) && (point.y<points[i].y)) ) &&
					(point.x < (points[j].x - points[i].x) * (point.y - points[j].y) / (points[j].y - points[i].y) + points[i].x))
				c = !c;
		}
		return c;
	}*/
	
	/*public static final Paint mkPaint(int r, int g, int b)
	{
		Paint p = new Paint();
		p.setARGB(255, r, g, b);
		p.setAntiAlias(StaticInfo.Antialiasing);
		p.setStrokeWidth(2);
		return p;
	}
	
	public static final Paint mkPaint(int a, int r, int g, int b)
	{
		Paint p = new Paint();
		p.setARGB(a, r, g, b);
		p.setAntiAlias(StaticInfo.Antialiasing);
		p.setStrokeWidth(2);
		return p;
	}*/
	
	public static final String decodeIStream(InputStream in) throws IOException
	{
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for(int n; (n = in.read(b)) != -1;)
		{
		    out.append(new String(b, 0, n));
		}
		return out.toString();
	}
	
	/**
	 * Checks if the external SD Card is mounted.
	 * @param requireWriteAccess
	 * @return
	 */
	public static final boolean hasStorage(boolean requireWriteAccess)
	{
		String state = Environment.getExternalStorageState();

		if(Environment.MEDIA_MOUNTED.equals(state))
		{
			return true;
		} else if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
		{
			if(requireWriteAccess) return false;
			return true;
		} else
		{
			return false;
		}
	}
}
