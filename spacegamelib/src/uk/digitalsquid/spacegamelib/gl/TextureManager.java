package uk.digitalsquid.spacegamelib.gl;

import java.util.Collection;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import uk.digitalsquid.spacegamelib.StaticInfo;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.opengl.GLUtils;

public class TextureManager {
	private static final HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
	private static final HashMap<Character, Letter> letters = new HashMap<Character, TextureManager.Letter>();
	private static boolean initialised = false;
	private static Resources resources;
	private static final BitmapFactory.Options bmpOpts;
	
	protected static final int TEXT_HEIGHT = 128;
	protected static final int TEXT_WIDTH = 128;
	private static final Paint txtPaint = new Paint();
	
	static {
		bmpOpts = new BitmapFactory.Options();
		bmpOpts.inScaled = false;
		
		txtPaint.setColor(0xFFFFFFFF /* White */);
		txtPaint.setTextSize(128);
		txtPaint.setTextAlign(Align.LEFT);
	}
	
	private TextureManager() {}

	public static final void init(Context context, GL10 gl) {
		resources = context.getResources();
		if(initialised) nullify(gl); // Not this GL?
		
		txtPaint.setTypeface(StaticInfo.Fonts.bangers);
		txtPaint.setAntiAlias(StaticInfo.Antialiasing);
		
		initialised = true;
	}
	
	public static final void nullify(GL10 gl) {
		initialised = false;
		
		if(data.size() != 0) {
			// Free all previous textures
			Collection<Integer> textures = data.values();
			int[] glTextures = new int[data.size()];
			
			int i = 0;
			for(int t : textures) {
				glTextures[i++] = t;
			}
			gl.glDeleteTextures(glTextures.length, glTextures, 0);
		}
		
		if(letters.size() != 0) {
			// Free all previous chars
			Collection<Letter> bLetters = letters.values();
			int[] glLetters = new int[letters.size()];
			
			int i = 0;
			for(Letter t : bLetters) {
				glLetters[i++] = t.id;
			}
			gl.glDeleteTextures(glLetters.length, glLetters, 0);
		}
		
		data.clear();
		letters.clear();
		
		System.gc();
	}
	
	/**
	 * Gets and loads a texture.
	 * @param gl
	 * @param resId
	 * @return
	 */
	public static final int getTexture(GL10 gl, int resId) {
		return getTexture(gl, resId, false);
	}
	
	/**
	 * Loads and gets a texture.
	 * @param gl
	 * @param resId
	 * @param repeatPattern If true, the created texture will repeat itself. Note that if the same texture is loaded twice, they will both have the same repeat property.
	 * @return
	 */
	public static final int getTexture(GL10 gl, int resId, boolean repeatPattern) {
		if(!initialised) throw new IllegalStateException("Texture Manager not initialised");
		
		if(data.containsKey(resId)) {
			return data.get(resId);
		}
		
		// Create an int array with the number of textures we want,
		// in this case 1.
		int[] textures = new int[1];
		// Tell OpenGL to generate textures.
		gl.glGenTextures(1, textures, 0);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_S,
                repeatPattern ? GL10.GL_REPEAT : GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_T,
                repeatPattern ? GL10.GL_REPEAT : GL10.GL_CLAMP_TO_EDGE);
		// Scale up if the texture if smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MAG_FILTER,
		                   GL10.GL_LINEAR); // TODO: Use this as a performance / quality global option ( / GL_NEAREST)
		// scale linearly when image smaller than texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MIN_FILTER,
		                   GL10.GL_LINEAR);
		
		Bitmap bmp = BitmapFactory.decodeResource(resources, resId, bmpOpts);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		
		data.put(resId, textures[0]);
		
		System.gc(); // Needed?
		
		return textures[0];
	}
	
	public static final Letter getText128Texture(GL10 gl, char letter) {
		if(!initialised) throw new IllegalStateException("Texture Manager not initialised");
		
		if(letters.containsKey(letter)) {
			return letters.get(letter);
		}
		
		// Create an int array with the number of textures we want,
		// in this case 1.
		int[] textures = new int[1];
		// Tell OpenGL to generate textures.
		gl.glGenTextures(1, textures, 0);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);
		// Scale up if the texture if smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MAG_FILTER,
		                   GL10.GL_LINEAR); // TODO: Use this as a performance / quality global option ( / GL_NEAREST)
		// scale linearly when image smaller than texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MIN_FILTER,
		                   GL10.GL_LINEAR);
		
		float width = txtPaint.measureText("" + letter);
		
		Bitmap bmp = Bitmap.createBitmap(TEXT_WIDTH, TEXT_HEIGHT, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		c.drawColor(0x00FFFFFF);
		c.drawText("" + letter, 10, 128, txtPaint);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		
		Letter ret = new Letter(textures[0], width);
		letters.put(letter, ret);
		
		return ret;
	}
	
	public static class Letter {
		public final int id;
		public final float width;
		
		public Letter(int id, float width) {
			this.id = id;
			this.width = width;
		}
	}
}
