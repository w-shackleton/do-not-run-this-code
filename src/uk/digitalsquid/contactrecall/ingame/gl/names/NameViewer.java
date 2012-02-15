package uk.digitalsquid.contactrecall.ingame.gl.names;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.ingame.gl.Positionable;
import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.ingame.gl.Text;
import uk.digitalsquid.contactrecall.misc.Config;

/**
 * Displays a name. This is an abstract type which is subclassed to be a question or a choice.
 * @author william
 *
 */
public abstract class NameViewer implements Config, Positionable {
	final float textHeight;
	public NameViewer(float textHeight) {
		this.textHeight = textHeight;
		display = new Text("", 0, 0, textHeight, 1, 1, 1, 0);
		display.setGravity(0.5f);
		// if(DEBUG) bg = new RectMesh(0, 0, requiredWidth, textHeight, 0, 1, 0, 0.2f);
	}
	
	Text display;
	RectMesh bg;

	private String text = "";
	
	void setText(String text) {
		this.text = text;
		display.setText(text);
		rescale();
	}

	String getText() {
		return text;
	}
	
	private float requiredWidth = 1;
	
	/**
	 * Scales text down when it is too big.
	 */
	private float compensationScale = 1;
	
	/**
	 * Scales the text down so it fits in the specified width
	 */
	private void rescale() {
		if(display.getWidth() > requiredWidth) {
			compensationScale = requiredWidth / display.getWidth();
		} else {
			compensationScale = 1;
		}
	}
	
	public void draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glTranslatef(x, y, z);
		gl.glRotatef(rx, 1, 0, 0);
		gl.glRotatef(ry, 0, 1, 0);
		gl.glRotatef(rz, 0, 0, 1);
		if(bg != null) bg.draw(gl);
		// Don't scale debug bg
		gl.glScalef(compensationScale, compensationScale, compensationScale);
		display.draw(gl);
		gl.glPopMatrix();
	}
	
	/**
	 * When true, 0 = out and 1 = in. When false, 0 = in and 1 = out.
	 * Note that although PhotoViewer defines the animations, it is up to the implementation to actually do any animating.
	 */
	protected boolean animatingIn = false;
	protected float animStage = 1;
	
	/**
	 * if in is <code>true</code>, start to animate in. Otherwise, start to animate out.
	 * @param in
	 */
	public void setAnimation(boolean in) {
		animatingIn = in;
		animStage = 0;
	}
	
	public void setAnimationStage(float stage) {
		animStage = stage;
	}
	
	private float x, y, z;
	private float rx, ry, rz;
	
	public void setXYZ(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Sets the X, Y, Z rotations in DEGREES
	 * @param x
	 * @param y
	 * @param z
	 */
	public void setRXYZ(float rx, float ry, float rz) {
		this.rx = rx;
		this.ry = ry;
		this.rz = rz;
	}
	
	@Override
	public float getWidth() {
		return display.getWidth();
	}

	@Override
	public float getHeight() {
		return textHeight;
	}
	
	@Override
	public void setWidth(float width) {
		requiredWidth = width;
		rescale();
		if(bg != null) bg.setWH(requiredWidth, textHeight);
	}
	
	@Override
	public void setHeight(float height) { }
}
