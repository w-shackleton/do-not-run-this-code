package uk.digitalsquid.contactrecall.misc;

import java.util.Random;

/**
 * Mathematical constants
 * @author william
 *
 */
public final class Const {
	private Const() {}
	
	/**
	 * 2pi
	 */
	public static final float TAU = (float) (2 * Math.PI);
	/**
	 * Pi, as a float.
	 */
	public static final float PI = (float) Math.PI;
	
	/**
	 * A global random generator. Used only for user-seen randomness
	 * (ie. randomness that doesn't need to be especially random)
	 */
	public static final Random RAND = new Random();
}
