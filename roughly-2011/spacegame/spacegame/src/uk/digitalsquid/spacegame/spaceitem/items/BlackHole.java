package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.VecHelper;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Gravitable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Messageable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Warpable;
import android.preference.PreferenceManager;

public class BlackHole extends Gravitable implements TopDrawable, Moveable, Warpable, Messageable
{
	private static final float BLACK_HOLE_RADIUS = 7f;
	private static final float BLACK_HOLE_DENSITY = 2f;
	private static final float BLACK_HOLE_ZOOM_SPEED = 1.01f;
	public static final float BLACK_HOLE_ZOOM_POWER = 1.10f;
	private static final int BLACK_HOLE_ZOOM_WAIT = 100;
	private static final float BLACK_HOLE_CAPTURE_DIST = BLACK_HOLE_RADIUS;

	protected static final int BH_PULSES = 20;
	
	private final RectMesh bhImage, bhP2Image;
	protected float bhRotation = 0;
	
	protected boolean bhActivated = false, bhStarted = false;
	
	private float bhEndGameZoom = BLACK_HOLE_ZOOM_SPEED;
	private float bhEndGameFade = 0;
	private float bhEndGameFadeSpeed = 1.5f;
	private float bhEndGameWait = -BLACK_HOLE_ZOOM_WAIT;
	
	
	public BlackHole(SimulationContext context, Vec2 coord)
	{
		super(context, coord, 100, BLACK_HOLE_DENSITY, BLACK_HOLE_RADIUS, BodyType.STATIC);
		
		bhImage = new RectMesh(getPosX(), getPosY(), (float)getRadius() * 2, (float)getRadius() * 2, R.drawable.bh);
		bhP2Image = new RectMesh(getPosX(), getPosY(), (float)getRadius() * 2, (float)getRadius() * 2, R.drawable.bhp2);
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_NONE;
		fixture.getFilterData().maskBits = COLLISION_GROUP_NONE;
	}
	
	@Override
	public void calculateVelocityMutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
		double currDist = VecHelper.dist(getPos(), itemC);
		if(currDist < BLACK_HOLE_CAPTURE_DIST && !bhActivated) { // Start pulse
			bhActivated = true;
			bhStarted = true;
			if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("bhFirstTime", true)) {
				PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("bhFirstTime", false).commit();
				messageInfo.display = true;
			}
			// TODO: Disable for release version
			// messageInfo.display = true;
		}
	}

	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		bhP2Image.draw(gl);
	}

	@Override
	public void drawTop(GL10 gl, float worldZoom)
	{
		bhImage.setRotation(-bhRotation);
		bhImage.draw(gl);
	}

	@Override
	public void move(float millistep, float speedScale) { }

	@Override
	public void drawMove(float millistep, float speedscale) {
		if(bhActivated)
		{
			bhEndGameWait += millistep / 10;
		}
		bhRotation += millistep / 50;
		if(bhStarted && bhEndGameWait > 0)
		{
			//WarpData data = new WarpData(1, 0, bhEndGameFade);
			bhEndGameZoom = (float) Math.pow(bhEndGameZoom, BLACK_HOLE_ZOOM_POWER);
			bhEndGameFadeSpeed += 1;
			bhEndGameFade += bhEndGameFadeSpeed;
		}
	}

	@Override
	public WarpData sendWarpData() {
		if(bhStarted && bhEndGameWait > 0)
		{
			WarpData data;
			if(bhEndGameFade > 300)
			{
				data = new WarpData(bhEndGameZoom, bhEndGameFade, bhEndGameFade * 2f, true);
			}
			else
				data = new WarpData(bhEndGameZoom, bhEndGameFade, bhEndGameFade * 2f, false);
			return data;
		}
		return null;
	}
	
	private MessageInfo messageInfo = new MessageInfo(context.getResources().getStringArray(R.array.bhfirsttimemessage), false);
	
	@Override
	public MessageInfo sendMessage()
	{
		return messageInfo;
	}
	
	@Override
	public Vec2 calculateVelocityImmutable(Vec2 itemC, Vec2 itemVC, float itemRadius) {
		return super.calculateVelocityImmutable(itemC, itemVC, itemRadius);
	}

	@Override
	public boolean isForceExclusive() {
		if(bhActivated) return true;
		return false;
	}

	@Override
	public void drawBelow(GL10 gl, float worldZoom) {
	}
}
