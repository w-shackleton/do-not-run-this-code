package uk.digitalsquid.spacegamelib.gl;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.StaticInfo;

/**
 * A bezier curve GL object - can't use builtin, as GL ES
 * @author william
 *
 */
public class Bezier extends Lines {
	private static final int BEZIER_POINTS = 30;
	
	public Bezier(float r,
			float g, float b, float a) {
		super(0, 0, BEZIER_POINTS, GL10.GL_LINE_STRIP, r, g, b, a);
		if(StaticInfo.DEBUG) {
			debugPoints = new Points(0, 0, 0, 1, 1, 0, 1);
			debugPoints.setPointSize(7);
		} else {
			debugPoints = null;
		}
	}
	
	/**
	 * Show bezier control points on screen
	 */
	private final Points debugPoints;
	
	private float[] bezierPoints;
	
	public void setBezierPoints(float[] points) {
		// if(points.equals(bezierPoints)) return;
		bezierPoints = points;
		
		// TODO: Check
		bezier2D(bezierPoints, BEZIER_POINTS, getVertices());
		
		if(debugPoints != null) {
			if(debugPoints.getVertices().capacity() != points.length / 2 * 3) {
				debugPoints.setVertices(points.length / 2);
			}
			
			// Copy points across
			FloatBuffer debugVertices = debugPoints.getVertices();
			int count = 0;
			for(int i = 0; i < points.length; i+=2) {
				debugVertices.put(count++, points[i]);
				debugVertices.put(count++, points[i+1]);
				debugVertices.put(count++, 0);
			}
		}
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
    
    @Override
    public void draw(GL10 gl) {
    	super.draw(gl);
    	if(debugPoints != null) debugPoints.draw(gl);
    }
}
