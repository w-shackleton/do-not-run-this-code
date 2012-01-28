package uk.digitalsquid.contactrecall.ingame.gl.names;

import javax.microedition.khronos.opengles.GL10;

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
		display = new Text("TEXT. gfhMqt", 0, 0, 2);
	}
	
	Text display;

	private String text = "NAME";
	
	void setText(String text) {
		this.text = text;
		display.setText(text);
	}

	String getText() {
		return text;
	}
	
	public void draw(GL10 gl) {
		display.draw(gl);
	}
}
