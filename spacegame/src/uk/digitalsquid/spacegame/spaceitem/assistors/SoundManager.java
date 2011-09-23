package uk.digitalsquid.spacegame.spaceitem.assistors;

import uk.digitalsquid.spacegame.R;
import uk.digitalsquid.spacegamelib.CompuFuncs;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.SparseArray;

/**
 * Singleton class for managing sounds
 * @author william
 *
 */
public final class SoundManager {
	private static SoundManager soundManager;
	
	public static final void initialise(Context c) {
		soundManager = new SoundManager(c);
	}
	
	public static final SoundManager get() {
		if(soundManager == null) throw new IllegalStateException("Not yet initialsed");
		return soundManager;
	}
	
	private SoundManager(Context c) {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
	    soundPoolMap = new SparseArray<Integer>();
	    
	    soundPoolMap.put(SOUND_STAR, soundPool.load(c, R.raw.snd_star, 1));
	    soundPoolMap.put(SOUND_BOUNCE, soundPool.load(c, R.raw.snd_bounce, 1));
	    soundPoolMap.put(SOUND_PORTAL, soundPool.load(c, R.raw.snd_portal, 1));
	    
	    mgr = (AudioManager)c.getSystemService(Context.AUDIO_SERVICE);
	}
	
	public static final int SOUND_STAR = 0;
	public static final int SOUND_BOUNCE = 1;
	public static final int SOUND_PORTAL = 2;

	private final AudioManager mgr;
	private final SoundPool soundPool;
	private final SparseArray<Integer> soundPoolMap;
	
	/**
	 * Plays a sound
	 * @param sound the sound ID to play
	 */
	public void playSound(int sound) {
		playSound(sound, 1);
	}
	
	/**
	 * Plays a sound
	 * @param sound the sound ID to play
	 * @param amount the volume, between 0 and 1
	 */
	public void playSound(int sound, float amount) {
		if(amount < 0.1) return; // Ignore quiet stuff
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax * CompuFuncs.TrimMinMax(amount, 0, 1);
	    
	    soundPool.play(soundPoolMap.get(sound), volume, volume, 1, 0, 1f);     
	}
}
