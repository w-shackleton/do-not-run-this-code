package uk.digitalsquid.contactrecall.misc;

public final class Utils {
	public static final int minMax(int num, int min, int max) {
		if(num < min) return min;
		if(num > max) return max;
		return num;
	}
	public static final float minMax(float num, float min, float max) {
		if(num < min) return min;
		if(num > max) return max;
		return num;
	}
}
