package uk.digitalsquid.spacegame.spaceitem.items;

import java.util.StringTokenizer;

import javax.microedition.khronos.opengles.GL10;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegamelib.Constants;
import uk.digitalsquid.spacegamelib.SimulationContext;
import uk.digitalsquid.spacegamelib.gl.RectMesh;
import uk.digitalsquid.spacegamelib.spaceitem.Rectangular;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Clickable;
import uk.digitalsquid.spacegamelib.spaceitem.interfaces.Messageable;
import android.util.Log;

public class InfoBox extends Rectangular implements Messageable, Clickable, Constants
{
	protected String text = null;
	protected RectMesh image;
	protected boolean showNow;
	
	/*
	 *  NOTE FOR CONVERSION:
	 * This image is now 128x128, but with vertical padding
	 */
	
	protected static final float BOUNCINESS = 0.8f;
	protected static final Vec2 SIZE = new Vec2(6f * GRID_SIZE, 4f * GRID_SIZE);
	protected static final Vec2 IMG_SIZE = new Vec2(6f * GRID_SIZE, 6f * GRID_SIZE);
	
	public InfoBox(SimulationContext context, Vec2 coord, float rotation, String text, boolean initialshow) {
		super(context, coord, SIZE, 10, rotation, BOUNCINESS, BodyType.STATIC);
		if(text == null) text = "";
		this.text = text;
		
		image = new RectMesh((float)getPos().x, (float)getPos().y, (float)IMG_SIZE.x, (float)IMG_SIZE.y, R.drawable.message);
		image.setRotation(rotation);
		
		showNow = initialshow;
		messageInfo = new MessageInfo(new StringTokenizer(this.text, "$"), initialshow);
		
		fixture.getFilterData().categoryBits = COLLISION_GROUP_PLAYER;
		fixture.getFilterData().maskBits = COLLISION_GROUP_PLAYER;
	}
	
	public InfoBox(SimulationContext context, Vec2 coord, int textId, boolean initialshow) {
		this(context, coord, 0, context.getResources().getString(textId), initialshow);
	}

	@Override
	public void draw(GL10 gl, float worldZoom) {
		image.draw(gl);
	}
	
	MessageInfo messageInfo;

	@Override
	public MessageInfo sendMessage() {
		//Log.v(TAG, "showNow: " + showNow);
		// TODO: Delete showNow since message display setting reset in GameView?
		messageInfo.display = showNow; // Only display once (possibly obsoleted by code in Messageable caller in GameView)
		showNow = false;
		return messageInfo;
	}

	@Override
	public void onClick(float x, float y) {
		Log.v(TAG, "Loading info box...");
		showNow = true;
	}
}
