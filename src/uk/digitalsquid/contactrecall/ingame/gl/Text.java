package uk.digitalsquid.contactrecall.ingame.gl;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.contactrecall.ingame.gl.TextureManager.Letter;

public class Text extends RectMesh {
	
	private String text;
	
	private final LinkedList<RectMesh> letters = new LinkedList<RectMesh>();
	
	private boolean needsUpdating = false;
	
	/**
	 * The total width of the text, computed during text bmp update.
	 */
	private float totalWidth;
	private float height;
	private float gravity;

	/**
	 * 
	 * @param x X-pos of text
	 * @param y y-pos of text
	 * @param width
	 * @param height
	 * @param r | Background colour of text - usually set to transparent
	 * @param g |
	 * @param b |
	 * @param a |
	 */
	public Text(String text, float x, float y, float height, float r, float g, float b, float a) {
		super(x, y, 128, height, r, g, b, a);
		this.height = height;
		setText(text);
	}
	
	public Text(String text, float x, float y, float height) {
		this(text, x, y, height, 1, 1, 1, 0);
	}

	public void setText(String text) {
		this.text = text;
		needsUpdating = true;
	}

	public String getText() {
		return text;
	}
	
	/**
	 * Gets the text's total width. Takes one draw for this to get updated.
	 * @return
	 */
	public float getWidth() {
		return totalWidth;
	}
	
	private void updateLetters(GL10 gl) {
		letters.clear();
		
		char[] l = text.toCharArray();
		
		float width = 0;
		
		for(char letter : l) {
			Letter tmpLetter = TextureManager.getText128Texture(gl, letter);
			final float offset = tmpLetter.offset / TextureManager.TEXT_WIDTH * height;
			RectMesh tmpMesh = new RectMesh(width - offset, 0, height, height, .5f, 0, 1, 1, 1, 1); // Height twice here to draw square - font bmps are all square.
			tmpMesh.setTextureId(tmpLetter.id);
			tmpMesh.setTextureCoordinates(texCoords);
			letters.add(tmpMesh);
			
			width += tmpLetter.width / TextureManager.TEXT_WIDTH * height;
		}
		
		totalWidth = width;
		
		setWH(width, height);
	}
	
	@Override
	protected void internalDraw(GL10 gl) {
		super.internalDraw(gl);
		
		if(needsUpdating) {
			needsUpdating = false;
			updateLetters(gl);
		}
		
		gl.glPushMatrix();
		// Centre text according to gravity
		gl.glTranslatef(-totalWidth * gravity, 0, 0);
		
		for(RectMesh mesh : letters) {
			mesh.draw(gl);
		}
		gl.glPopMatrix();
	}

	/**
	 * Sets the 'gravity' of the text, where 0f is with the left axis aligned to the centre,
	 * and 1f is the right side
	 * @param gravity
	 */
	public void setGravity(float gravity) {
		this.gravity = gravity;
	}
}
