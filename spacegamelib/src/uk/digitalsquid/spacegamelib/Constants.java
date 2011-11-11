package uk.digitalsquid.spacegamelib;

import java.util.Random;

/**
 * Constants for general use. Probably shouldn't have a contants class.
 * @author william
 *
 */
public interface Constants {
	
	public static final boolean DEBUG = false;
	/**
	 * Multiply a number of degrees by this to convert it to radians
	 */
	public static final float DEG_TO_RAD = (float) (Math.PI / 180);
	
	/**
	 * Multiply a number of radians by this to convert it to degrees
	 */
	public static final float RAD_TO_DEG = (float) (180 / Math.PI);
	
	public static final String TAG = "SpaceGame";
	
	/**
	 * Pseudo-random numbers.
	 */
	public static final Random RAND = new Random();
}
