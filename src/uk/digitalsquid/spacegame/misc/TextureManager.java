package uk.digitalsquid.spacegame.misc;

import java.util.Collection;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class TextureManager {
	private static final HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
	private static boolean initialised = false;
	private static Resources resources;
	private static final BitmapFactory.Options bmpOpts;
	static {
		bmpOpts = new BitmapFactory.Options();
		bmpOpts.inScaled = false;
	}
	
	private TextureManager() {}

	public static final void init(Context context, GL10 gl) {
		resources = context.getResources();
		if(initialised) nullify(gl); // Not this GL?
		
		initialised = true;
	}
	
	public static final void nullify(GL10 gl) {
		initialised = false;
		
		if(data.size() == 0) return;
		
		// Free all previous textures
		Collection<Integer> textures = data.values();
		int[] glTextures = new int[data.size()];
		
		int i = 0;
		for(int t : textures) {
			glTextures[i++] = t;
		}
		gl.glDeleteTextures(glTextures.length, glTextures, 0);
		
		data.clear();
		
		System.gc();
	}
	
	public static final int getTexture(GL10 gl, int resId) {
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
                GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);
		// Scale up if the texture if smaller.
		gl.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MAG_FILTER,
		                   GL10.GL_LINEAR); // TODO: Use this as a performance / quality global option ( / GL_LINEAR)
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
}
