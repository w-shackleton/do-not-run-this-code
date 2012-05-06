package uk.digitalsquid.spacegamelib;

import java.util.Random;

/**
 * Constants for general use. Probably shouldn't have a contants class.
 * @author william
 *
 */
public interface Constants {
	
	public static final boolean DEBUG = true;
	/**
	 * Multiply a number of degrees by this to convert it to radians
	 */
	public static final float DEG_TO_RAD = (float) (Math.PI / 180);
	
	/**
	 * Multiply a number of radians by this to convert it to degrees
	 */
	public static final float RAD_TO_DEG = (float) (180 / Math.PI);
	
	public static final String TAG = "spacegame";
	
	public static final float PI = (float)Math.PI;
	
	/**
	 * Pseudo-random numbers.
	 */
	public static final Random RAND = new Random();
	
	public static final float LOAD_SCALE = 0.1f;
	
	/**
	 * The positioning block size of grid items
	 */
	public static final float GRID_SIZE = 16 * LOAD_SCALE;
	/**
	 * The size of grid items
	 */
	public static final float GRID_SIZE_2 = GRID_SIZE * 2;
	
}
