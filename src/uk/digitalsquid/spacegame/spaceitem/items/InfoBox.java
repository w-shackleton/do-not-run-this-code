package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.Coord;
import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegame.misc.RectMesh;
import uk.digitalsquid.spacegame.spaceitem.BounceableRect;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Clickable;
import uk.digitalsquid.spacegame.spaceitem.interfaces.Messageable;
import android.content.Context;
import android.util.Log;

public class InfoBox extends BounceableRect implements Messageable, Clickable
{
	protected String text = null;
	protected RectMesh image;
	protected boolean showNow;
	
	/*
	 *  NOTE FOR CONVERSION:
	 * This image is now 128x128, but with vertical padding
	 */
	
	protected static final float BOUNCINESS = 0.8f;
	protected static final Coord SIZE = new Coord(50, 40);
	protected static final Coord IMG_SIZE = new Coord(50, 50);
	
	public InfoBox(Context context, Coord coord, float rotation, String text, boolean initialshow)
	{
		super(context, coord, SIZE, rotation, BOUNCINESS);
		if(text == null) text = "";
		this.text = text;
		
		image = new RectMesh((float)pos.x, (float)pos.y, (float)IMG_SIZE.x, (float)IMG_SIZE.y, R.drawable.message);
		image.setRotation(rotation);
		
		showNow = initialshow;
		messageInfo = new MessageInfo(new StringTokenizer(this.text, "$"), initialshow);
	}
	
	public InfoBox(Context context, Coord coord, int textId, boolean initialshow)
	{
		this(context, coord, 0, context.getResources().getString(textId), initialshow);
	}

	@Override
	public void draw(GL10 gl, float worldZoom)
	{
		image.draw(gl);
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
