package uk.digitalsquid.spacegame.misc;

import java.util.Collection;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES11;
import android.opengl.GLUtils;

public class TextureManager {
	private static final HashMap<Integer, Integer> data = new HashMap<Integer, Integer>();
	private static boolean initialised = false;
	private static Resources resources;
	
	private TextureManager() {}

	public static final void init(Context context, GL10 gl) {
		resources = context.getResources();
		if(initialised) nullify(gl); // Not this GL?
		
		initialised = true;
	}
	
	public static final void nullify(GL10 gl) {
		initialised = false;
		
		// Free all previous textures
		Collection<Integer> textures = data.values();
		int[] glTextures = new int[data.size()];
		
		int i = 0;
		for(int t : textures) {
			glTextures[i++] = t;
		}
		gl.glDeleteTextures(1, glTextures, 0);
		
		System.gc();
	}
	
	public static final int getTexture(int resId) {
		if(!initialised) throw new IllegalStateException("Texture Manager not initialised");
		
		if(data.containsKey(resId)) {
			return data.get(resId);
		}
		
		// Create an int array with the number of textures we want,
		// in this case 1.
		int[] textures = new int[1];
		// Tell OpenGL to generate textures.
		GLES11.glGenTextures(1, textures, 0);
		
		GLES11.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_S,
                GL10.GL_CLAMP_TO_EDGE);
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D,
                GL10.GL_TEXTURE_WRAP_T,
                GL10.GL_CLAMP_TO_EDGE);
		// Scale up if the texture if smaller.
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MAG_FILTER,
		                   GL10.GL_NEAREST); // TODO: Use this as a performance / quality global option ( / GL_LINEAR)
		// scale linearly when image smaller than texture
		GLES11.glTexParameterf(GL10.GL_TEXTURE_2D,
		                   GL10.GL_TEXTURE_MIN_FILTER,
		                   GL10.GL_NEAREST);
		
		Bitmap bmp = BitmapFactory.decodeResource(resources, resId);
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		
		data.put(resId, textures[0]);
		
		System.gc(); // Needed?
		
		return textures[0];
	}
}
