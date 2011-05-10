package uk.digitalsquid.spacegame.misc;

import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegame.misc.TextureManager.Letter;

public class Text extends RectMesh {
	
	private String text;
	
	private final LinkedList<RectMesh> letters = new LinkedList<RectMesh>();
	
	private boolean needsUpdating = false;
	
	private float height;

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
	public Text(String text, float x, float y, float height, int r, int g, int b, int a) {
		super(x, y, 128, height, -.5f, 0, r, g, b, a);
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

	private void updateLetters(GL10 gl) {
		letters.clear();
		
		char[] l = text.toCharArray();
		
		int width = 0;
		
		for(char letter : l) {
			Letter tmpLetter = TextureManager.getText128Texture(gl, letter);
			RectMesh tmpMesh = new RectMesh(width, 0, height, height, 0, 0, 1, 1, 1, 1); // Height twice here to draw square - font bmps are all square.
			tmpMesh.setTextureId(tmpLetter.id);
			tmpMesh.setTextureCoordinates(texCoords.clone());
			letters.add(tmpMesh);
			
			width += tmpLetter.width * height / TextureManager.TEXT_WIDTH;
		}
		
		setWH(width, height, -.5f, 0);
	}
	
	@Override
	protected void internalDraw(GL10 gl) {
		super.internalDraw(gl);
		
		if(needsUpdating) {
			needsUpdating = false;
			updateLetters(gl);
		}
		
		for(RectMesh mesh : letters) {
			mesh.draw(gl);
		}
	}
}
