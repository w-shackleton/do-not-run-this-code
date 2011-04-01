package uk.digitalsquid.spacegame.spaceitem.items;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.Gravitable;
import uk.digitalsquid.spacegame.spaceitem.assistors.BhPulseInfo;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Moveable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.TopDrawable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Warpable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;

public class BlackHole extends Gravitable implements TopDrawable, Moveable, Warpable, Messageable
{
	private static final int BLACK_HOLE_RADIUS = 70;
	private static final float BLACK_HOLE_DENSITY = 0.5f;
	private static final float BLACK_HOLE_ZOOM_SPEED = 1.01f;
	public static final float BLACK_HOLE_ZOOM_POWER = 1.1f;
	private static final int BLACK_HOLE_ZOOM_WAIT = 100;

	protected static final int BH_PULSES = 20;
	
	protected static BitmapDrawable bhImage, bhP2Image;
	protected float bhRotation = 0;
	
	protected BhPulseInfo[] bhPulses;
	protected boolean bhActivated = false, bhStarted = false;
	
	private float bhEndGameZoom = BLACK_HOLE_ZOOM_SPEED;
	private float bhEndGameFade = 0;
	private float bhEndGameFadeSpeed = 1;
	private float bhEndGameWait = -BLACK_HOLE_ZOOM_WAIT;
	
	
	public BlackHole(Context context, Coord coord)
	{
		super(context, coord, 0.95f, BLACK_HOLE_DENSITY, BLACK_HOLE_RADIUS / 2);
		
		bhPulses = new BhPulseInfo[BH_PULSES]; // Initiate random pulses
		for(int i = 0; i < BH_PULSES; i++)
		{
			bhPulses[i] = new BhPulseInfo(pos, (float) (i * 360 / BH_PULSES));
		}

		bhImage = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bh);
		bhP2Image = (BitmapDrawable) context.getResources().getDrawable(R.drawable.bhp2);
	}
	
	@Override
	public Coord calculateRF(Coord itemC, Coord itemVC)
	{
		double currDist = Coord.getLength(pos, itemC);
		if(currDist < 8f * ITEM_SCALE && !bhActivated) // Start pulse
		{
			bhActivated = true;
			bhStarted = true;
			if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("bhFirstTime", true))
			{
				PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("bhFirstTime", false);
				messageInfo.display = true;
			}
			// TODO: Disable for release version
			messageInfo.display = true;
		}
		return super.calculateRF(itemC, itemVC);
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		bhP2Image.setAntiAlias(StaticInfo.Antialiasing);
		bhP2Image.setBounds(
				(int)((pos.x - (radius * 2)) * worldZoom),
				(int)((pos.y - (radius * 2)) * worldZoom),
				(int)((pos.x + (radius * 2)) * worldZoom),
				(int)((pos.y + (radius * 2)) * worldZoom));
		bhP2Image.draw(c);
	}

	@Override
	public void drawTop(Canvas c, float worldZoom)
	{
		if(bhActivated)
		{
			for(int i = 0; i < bhPulses.length; i++)
			{
				bhPulses[i].draw(c, worldZoom);
			}
		}
		
		c.rotate(-bhRotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom);
		bhImage.setAntiAlias(StaticInfo.Antialiasing);
		bhImage.setBounds(
				(int)((pos.x - (radius * 2)) * worldZoom),
				(int)((pos.y - (radius * 2)) * worldZoom),
				(int)((pos.x + (radius * 2)) * worldZoom),
				(int)((pos.y + (radius * 2)) * worldZoom));
		bhImage.draw(c);
		c.rotate(bhRotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom);
	}

	@Override
	public void move(float millistep, float speedScale)
	{
		if(bhActivated)
		{
			//boolean allFinished = true;
			for(int i = 0; i < bhPulses.length; i++)
			{
				bhPulses[i].move(millistep / 40);
				//if(!bhPulses[i].move(millistep / 40)) allFinished = false;
			}
			//if(allFinished) bhActivated = false;

			//Log.v("SpaceGame", "bhEndGameWait: " + bhEndGameWait);
			bhEndGameWait += millistep / 50;
		}
		bhRotation += millistep / 50;
		if(bhStarted && bhEndGameWait > 0)
		{
			//WarpData data = new WarpData(1, 0, bhEndGameFade);
			bhEndGameZoom = (float) Math.pow(bhEndGameZoom, BLACK_HOLE_ZOOM_POWER);
			bhEndGameFadeSpeed += 1 / 5;
			bhEndGameFade += bhEndGameFadeSpeed;
		}
	}

	@Override
	public WarpData sendWarpData()
	{
		if(bhStarted && bhEndGameWait > 0)
		{
			WarpData data;
			if(bhEndGameFade > 300)
			{
				data = new WarpData(bhEndGameZoom, bhEndGameFade, bhEndGameFade * 3f, true);
			}
			else
				data = new WarpData(bhEndGameZoom, bhEndGameFade, bhEndGameFade * 3f, false);
			return data;
		}
		return null;
	}
	
	MessageInfo messageInfo = new MessageInfo(context.getResources().getStringArray(R.array.bhfirsttimemessage), false);
	
	@Override
	public MessageInfo sendMessage()
	{
		return messageInfo;
	}
}
