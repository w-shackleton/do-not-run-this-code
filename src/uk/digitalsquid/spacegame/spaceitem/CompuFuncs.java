package uk.digitalsquid.spacegame.spaceitem;

import java.io.IOException;
import java.io.InputStream;

import android.os.Environment;

import uk.digitalsquid.spacegame.Coord;

public class CompuFuncs
{
	public static final double GRAVCONST = .008f;
	
	/**
	 * Computes the force between a massive body and the player
	 * @param outForce The {@link Coord} to put the force into
	 * @param planet The body's position
	 * @param planetDensity The body's density
	 * @param planetRad The body's radius
	 * @param item The position of the player
	 */
	public static final void computeForce(
			Coord outForce,
			Coord planet,
			double planetDensity,
			double planetRad,
			Coord item) {
		double r = Math.sqrt((planet.x - item.x) * (planet.x - item.x) + (planet.y - item.y) * (planet.y - item.y)); // Distance between objects
		
		if((planet.x - item.x) * (planet.x - item.x) + (planet.y - item.y) * (planet.y - item.y) < planetRad * planetRad)
			planetRad = r;
		
		outForce.x =
			GRAVCONST * (planet.x - item.x) *
			computeWeight(planetRad,planetDensity) /
			(r * r);
		outForce.y =
			GRAVCONST * (planet.y - item.y) * 
			computeWeight(planetRad,planetDensity) /
			(r * r);
	}

	@Deprecated
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
	
	@Deprecated
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
	 * Check if point is inside the polygon points
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
	
	/**
	 * Check if point is inside the polygon points
	 */
	public static final boolean PointInPolygon(Coord points[], float pointx, float pointy)
	{
		int i, j;
		boolean c = false;
		
		for (i = 0, j = points.length - 1; i < points.length; j = i++)
		{
			//Log.v("SpaceGame", "" + i + ": " + points[i] + ", " + j + ": " + points[j]);
			if (( ((points[i].y<=pointy) && (pointy<points[j].y)) || ((points[j].y<=pointy) && (pointy<points[i].y)) ) &&
					(pointx < (points[j].x - points[i].x) * (pointy - points[i].y) / (points[j].y - points[i].y) + points[i].x))
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
	
	/**
	 * Returns the precomputed factorial of a number
	 * @param n
	 * @return
	 */
	public static final double fact(int n) {
		return factNums[n]; // Let it throw out of range exception
	}
	
	private static final double[] factNums = {
		1.0,
		1.0,
		2.0,
		6.0,
		24.0,
		120.0,
		720.0,
		5040.0,
		40320.0,
		362880.0,
		3628800.0,
		39916800.0,
		479001600.0,
		6227020800.0,
		87178291200.0,
		1307674368000.0,
		20922789888000.0,
		355687428096000.0,
		6402373705728000.0,
		121645100408832000.0,
		2432902008176640000.0,
		51090942171709440000.0,
		1124000727777607680000.0,
		25852016738884976640000.0,
		620448401733239439360000.0,
		15511210043330985984000000.0,
		403291461126605635584000000.0,
		10888869450418352160768000000.0,
		304888344611713860501504000000.0,
		8841761993739701954543616000000.0,
		265252859812191058636308480000000.0,
		8222838654177922817725562880000000.0,
		263130836933693530167218012160000000.0,
	};
	
	/**
	 * Combinations
	 * @param n
	 * @param i
	 * @return
	 */
	public static final double nCr(int n, int i) {
		return fact(n) / (fact(i) * fact(n - i));
	}
}
