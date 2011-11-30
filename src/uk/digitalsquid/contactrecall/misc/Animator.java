package uk.digitalsquid.contactrecall.misc;

/**
 * Given a value from 0 to 1, this class returns the 'animated' equivalent, ie. smoothly animating with a sine curve.
 * @author william
 *
 */
public final class Animator {
	private Animator() {}
	
	public static final int TYPE_SINE = 1;

	public static final float anim1d(int type, float in) {
		if(in > 1) in = 1;
		if(in < 0) in = 0;
		switch(type) {
		case TYPE_SINE:
			return (float) ((1-Math.cos(in * Math.PI)) / 2f);
		default:
			return in;
		}
	}
}
