package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.StringTokenizer;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.StaticInfo;
import uk.digitalsquid.spacegame.spaceitem.BounceableRect;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Clickable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

public class InfoBox extends BounceableRect implements Messageable, Clickable
{
	protected String text = null;
	protected BitmapDrawable image;
	protected boolean showNow;
	
	/*
	 *  NOTE FOR CONVERSION:
	 * This image is now 128x128, but with vertical padding
	 */
	
	protected static final float BOUNCINESS = 0.8f;
	protected static final Coord SIZE = new Coord(50, 40);
	
	public InfoBox(Context context, Coord coord, float rotation, String text, boolean initialshow)
	{
		super(context, coord, SIZE, rotation, BOUNCINESS);
		this.text = text;
		image = (BitmapDrawable) context.getResources().getDrawable(R.drawable.message);
		showNow = initialshow;
		messageInfo = new MessageInfo(new StringTokenizer(this.text, "$"), initialshow);
	}
	
	public InfoBox(Context context, Coord coord, int textId, boolean initialshow)
	{
		super(context, coord, SIZE, 20, BOUNCINESS);
		this.text = context.getResources().getString(textId);
		if(text == null) text = "";
		showNow = initialshow;
		messageInfo = new MessageInfo(new StringTokenizer(this.text, "$"), initialshow);
	}

	@Override
	public void draw(Canvas c, float worldZoom)
	{
		/*c.drawRect(new Rect(
				(int)((pos.x - (size.x / 2)) * worldZoom),
				(int)((pos.y - (size.y / 2)) * worldZoom),
				(int)((pos.x + (size.x / 2)) * worldZoom),
				(int)((pos.y + (size.y / 2)) * worldZoom)
				), borderPaint);*/
		c.rotate(rotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom);
		image.setAntiAlias(StaticInfo.Antialiasing);
		image.setBounds(getRect());
		image.draw(c);
		c.rotate(-rotation, (float)pos.x * worldZoom, (float)pos.y * worldZoom);
		/*borderPaint.setARGB(255, 255, 0, 0);
		c.drawLine(
				(float)abcC.x * worldZoom,
				(float)abcC.y * worldZoom,
				(float)(abcC.x + (Math.cos(a) * 150) * worldZoom),
				(float)(abcC.y + (Math.sin(a) * 150) * worldZoom),
				borderPaint);
		borderPaint.setARGB(255, 0, 255, 0);
		c.drawLine(
				(float)abcC.x * worldZoom,
				(float)abcC.y * worldZoom,
				(float)(abcC.x + (Math.cos(B) * 150) * worldZoom),
				(float)(abcC.y + (Math.sin(B) * 150) * worldZoom),
				borderPaint);
		borderPaint.setARGB(255, 0, 0, 255);
		c.drawLine(
				(float)abcC.x * worldZoom,
				(float)abcC.y * worldZoom,
				(float)(abcC.x + (Math.cos(this.c) * 150) * worldZoom),
				(float)(abcC.y + (Math.sin(this.c) * 150) * worldZoom),
				borderPaint);*/
	}
	
	MessageInfo messageInfo;

	@Override
	public MessageInfo sendMessage()
	{
		//Log.v("SpaceGame", "showNow: " + showNow);
		// TODO: Delete showNow since message display setting reset in GameView?
		messageInfo.display = showNow; // Only display once (possibly obsoleted by code in Messageable caller in GameView)
		showNow = false;
		return messageInfo;
	}

	@Override
	public void onClick()
	{
		Log.v("SpaceGame", "Loading info box...");
		showNow = true;
	}
}
