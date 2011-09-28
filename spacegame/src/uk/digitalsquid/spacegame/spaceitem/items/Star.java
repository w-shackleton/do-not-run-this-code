package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.spaceitem.assistors.SoundManager;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Spherical;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Forceful;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.LevelAffectable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.StaticDrawable;
import android.graphics.Matrix;

public class Star extends Spherical implements LevelAffectable, Forceful, StaticDrawable
{
	private static final float STAR_RADIUS = 1.5f;
	
	private final RectMesh img;
	
	private boolean available = true;
	private boolean drawingP1 = true;
	private boolean drawingP2 = false;
	private boolean sendStatus = false;
	private boolean sendFinishedStatus = false;
	
	public Star(SimulationContext context, Vec2 coord)
	{
		super(context, coord, 10, STAR_RADIUS, BodyType.STATIC);
		
		img = new RectMesh(coord.x, coord.y, getRadius() * 2, getRadius() * 2, R.drawable.star);
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_NONE;
		fixture.getFilterData().maskBits = COLLISION_GROUP_NONE;
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
	public Vec2 calculateRF(Vec2 itemC, Vec2 itemV) {
		return null;
	}

	@Override
	public void calculateVelocityMutable(Vec2 itemC, Vec2 itemVC,
			float itemRadius) {
		if(available && VecHelper.dist(getPos(), itemC) < getRadius() + itemRadius) {
			available = false;
			sendStatus = true;
			SoundManager.get().playSound(SoundManager.SOUND_STAR);
		}
		
		if(!available && drawingP1) {
			drawingP1 = false;
			drawingP2 = true;
		}
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	private boolean pointsMapped = false;
	private float animX, animY;
	private float animDestX, animDestY;
	private float animAngle = 1;

	@Override
	public void drawStatic(GL10 gl, final float width, final float height, final Matrix matrix) {
		if(drawingP2) {
			
			if(!pointsMapped)
			{
				float[] points = {(float) getPos().x, (float) getPos().y};
				matrix.mapPoints(points);
				
				animX = points[0];
				animY = points[1];
				
				animDestX = -width / 2;
				animDestY = height / 2;
				
				pointsMapped = true;
			}
			
			animX -= (animX - animDestX) / 20f;
			animY -= (animY - animDestY) / 20f;
			if(animAngle < Math.PI) animAngle += 0.01f;
			float adjustedAngle = (float) (Math.cos(animAngle) * 360);
			
			float distFromDest = (float) Math.hypot(animX - animDestX, animY - animDestY);
			float opacity = 1;
			if(distFromDest < 12.8f) opacity = (float) CompuFuncs.TrimMin((distFromDest - 6.4f) / 6.4f, 0);
			if(opacity == 0) {
				drawingP2 = false;
				sendFinishedStatus = true;
			}
			
			img.setRotation(adjustedAngle);
			img.setXY(animX, animY);
			img.setAlpha(opacity);
			img.draw(gl);
		}
	}

	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemPos, Vec2 itemV, float itemRadius) {
		return null;
	}

	@Override
	public boolean isForceExclusive() {
		return false;
	}
}
