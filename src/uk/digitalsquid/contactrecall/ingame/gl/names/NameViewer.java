package uk.digitalsquid.contactrecall.ingame.gl.names;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.ingame.gl.RectMesh;
import uk.digitalsquid.contactrecall.ingame.gl.Text;
import uk.digitalsquid.contactrecall.misc.Config;

/**
 * Displays a name. This is an abstract type which is subclassed to be a question or a choice.
 * @author william
 *
 */
public class NameViewer implements Config {
	final float textHeight;
	public NameViewer(float textHeight) {
		this.textHeight = textHeight;
		display = new Text("A name (with a BG)", 0, 0, textHeight, 1, 1, 1, 0);
		display.setGravity(0.5f);
		bg = new RectMesh(0, 0, 4, 4, 1, 0, 0, 1);
	}
	
	Text display;
	RectMesh bg;

	private String text = "NAME";
	
	void setText(String text) {
		this.text = text;
		display.setText(text);
	}

	String getText() {
		return text;
	}
	
	public void draw(GL10 gl) {
		bg.setWH(display.getWidth(), 4);
		bg.draw(gl);
		display.draw(gl);
	}
}
