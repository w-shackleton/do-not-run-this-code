package uk.digitalsquid.spacegame.spaceitem.items;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.gl.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import android.content.Context;
import android.preference.PreferenceManager;

public class BlackHole extends Gravitable implements TopDrawable, Moveable, Warpable, Messageable
{
	private static final int BLACK_HOLE_RADIUS = 70;
	private static final float BLACK_HOLE_DENSITY = .8f;
	private static final float BLACK_HOLE_ZOOM_SPEED = 1.01f;
	public static final float BLACK_HOLE_ZOOM_POWER = 1.10f;
	private static final int BLACK_HOLE_ZOOM_WAIT = 100;
	private static final float BLACK_HOLE_CAPTURE_DIST = 14;

	protected static final int BH_PULSES = 20;
	
	private final RectMesh bhImage, bhP2Image;
	protected float bhRotation = 0;
	
	protected boolean bhActivated = false, bhStarted = false;
	
	private float bhEndGameZoom = BLACK_HOLE_ZOOM_SPEED;
	private float bhEndGameFade = 0;
	private float bhEndGameFadeSpeed = 1.5f;
	private float bhEndGameWait = -BLACK_HOLE_ZOOM_WAIT;
	
	
	public BlackHole(Context context, Coord coord)
	{
		super(context, coord, 0.95f, BLACK_HOLE_DENSITY, BLACK_HOLE_RADIUS / 2);
		
		bhImage = new RectMesh((float)pos.x, (float)pos.y, (float)radius * 4, (float)radius * 4, R.drawable.bh);
		bhP2Image = new RectMesh((float)pos.x, (float)pos.y, (float)radius * 4, (float)radius * 4, R.drawable.bhp2);
	}
	
	@Override
	public void calculateVelocityMutable(Coord itemC, Coord itemVC, float itemRadius) {
		double currDist = Coord.getLength(pos, itemC);
		if(currDist < BLACK_HOLE_CAPTURE_DIST * ITEM_SCALE && !bhActivated) { // Start pulse
			bhActivated = true;
			bhStarted = true;
			if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("bhFirstTime", true))
			{
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
	public BallData calculateVelocityImmutable(Coord itemC, Coord itemVC, float itemRadius, boolean testRun) {
		BallData d = super.calculateVelocityImmutable(itemC, itemVC, itemRadius, testRun);
		
		if(bhActivated) {
			Coord vel = new Coord(0, 0);
			Coord pos = new Coord(this.pos);
			
			if(d == null) {
				d = new BallData(vel, pos);
			} else {
				d.itemC = pos;
				d.itemVC = vel;
			}
		}
		
		return d;
	}
}
