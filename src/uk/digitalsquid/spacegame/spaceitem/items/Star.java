package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.CompuFuncs;
import uk.digitalsquid.spacegame.spaceitem.Spherical;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegame.spaceitem.interfaces.LevelAffectable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.StaticDrawable;
import android.content.Context;
import android.opengl.Matrix;

public class Star extends Spherical implements LevelAffectable, Forceful, StaticDrawable
{
	private static final int STAR_RADIUS = 15;
	
	private final RectMesh img;
	
	private boolean available = true;
	private boolean drawingP1 = true;
	private boolean drawingP2 = false;
	private boolean sendStatus = false;
	private boolean sendFinishedStatus = false;
	
	public Star(Context context, Coord coord)
	{
		super(context, coord, STAR_RADIUS);
		
		img = new RectMesh((float)pos.x, (float)pos.y, (float)radius * 2, (float)radius * 2, R.drawable.star);
	}
	
	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		if(drawingP1) {
			img.draw(gl);
		}
	}

	@Override
	public AffectData affectLevel() {
		if(sendStatus) {
			sendStatus = false;
			return new AffectData(true, false);
		}
		if(sendFinishedStatus) {
			sendFinishedStatus = false;
			return new AffectData(false, true);
		}
		return null;
	}

	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC) {
		return null;
	}

	@Override
	public BallData calculateVelocity(Coord itemC, Coord itemVC,
			float itemRadius) {
		if(available && Coord.getLength(pos, itemC) < radius + itemRadius) {
			available = false;
			sendStatus = true;
		}
		
		if(!available && drawingP1) {
			drawingP1 = false;
			drawingP2 = true;
		}
		
		return null;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	private float[] tmpInverse;
	private float animX, animY;
	private float animAngle = 1;

	@Override
	public void drawStatic(GL10 gl, final int width, final int height) {
		if(drawingP2) {
			
			GL11 gl11 = (GL11) gl;
			if(tmpInverse == null) { // Get position first time
				float[] matrix = new float[16];
				tmpInverse = new float[16];
				gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, matrix, 0);
				Matrix.invertM(tmpInverse, 0, matrix, 0);
				
				float[] points = {animX, animY, 0, 1,
						0,0,0,0};
				Matrix.multiplyMV(points, 4, tmpInverse, 0, points, 0); // TODO: Check this works!
				
				animX = points[0];
				animY = points[1];
			}
			
			animX *= 0.9f;
			animY *= 0.9f;
			if(animAngle < Math.PI) animAngle += 0.1f;
			float adjustedAngle = (float) (Math.cos(animAngle) * 360);
			
			float distFromDest = (float) Math.hypot(animX, animY);
			float opacity = 1;
			if(distFromDest < 128) opacity = CompuFuncs.TrimMin((int) ((distFromDest - 64) / 64f), 0);
			if(opacity == 0) {
				drawingP2 = false;
				sendFinishedStatus = true;
			}
			
			gl.glPushMatrix();
			gl.glTranslatef(animX, animY, 0);
			gl.glRotatef(adjustedAngle, 0, 0, 1);
			img.setAlpha(opacity);
			img.draw(gl);
			gl.glPopMatrix();
		}
	}
}
