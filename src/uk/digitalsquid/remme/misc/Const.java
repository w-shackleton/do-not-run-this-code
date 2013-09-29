package uk.digitalsquid.remme.misc;

import java.util.Random;

/**
 * Mathematical constants
 * @author william
 *
 */
public final class Const {
	private Const() {}
	/**
	 * A global random generator. Used only for user-seen randomness
	 * (ie. randomness that doesn't need to be especially random)
	 */
	public static final Random RAND = new Random();
}
