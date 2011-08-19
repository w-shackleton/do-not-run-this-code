package uk.digitalsquid.spacegame.gl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;

/**
 * A bezier curve GL object - can't use builtin, as GL ES
 * @author william
 *
 */
public class Bezier extends Lines {
	private static final int BEZIER_POINTS = 30;
	
	public Bezier(float x, float y, float r,
			float g, float b, float a) {
		super(x, y, BEZIER_POINTS, GL10.GL_LINE_STRIP, r, g, b, a);
	}
	
	private float[] bezierPoints;
	
	public void setBezierPoints(float[] points) {
		// if(points.equals(bezierPoints)) return;
		bezierPoints = points;
		
		// TODO: Check
		bezier2D(bezierPoints, BEZIER_POINTS, getVertices());
	}
	
	/**
	 * Bernstein basis. Based on C# bezier example found on CodeProject
	 * @param n
	 * @param i
	 * @param t
	 * @return
	 */
    private static final float bernsteinBasis(int n, int i, double t) {
        double ti; /* t^i */
        double tni; /* (1 - t)^i */

        /* Prevent problems with pow */

        if (t == 0.0 && i == 0) 
            ti = 1.0; 
        else 
            ti = Math.pow(t, i);

        if (n == i && t == 1.0) 
            tni = 1.0; 
        else 
            tni = Math.pow((1 - t), (n - i));

        //Bernstein basis
        return (float)(CompuFuncs.nCr(n, i) * ti * tni); 
    }
    
    private static final void bezier2D(float[] b, int outputPoints, FloatBuffer output)
    {
        output.rewind();
        int npts = (b.length) / 2;
        int icount = 0, jcount;
        float step = 1.0f / (outputPoints - 1);
        float t = 0;

        for (int i1 = 0; i1 != outputPoints; i1++)
        { 
            if ((1.0 - t) < 0.000005) 
                t = 1;

            jcount = 0;
            float px = 0, py = 0;
            for (int i = 0; i != npts; i++)
            {
                float basis = bernsteinBasis(npts - 1, i, t);
                px += basis * b[jcount];
                py += basis * b[jcount + 1];
                jcount = jcount +2;
            }

            output.put(icount++, px);
            output.put(icount++, py);
            output.put(icount++, 0);
            t += step;
        }
    }
}
