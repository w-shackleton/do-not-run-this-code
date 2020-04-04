package pennygame.lib;

/**
 * Generic utility things. Perhaps get rid of in future?
 * @author william
 *
 */
public final class Utils {
	
	public static final int trimMinMax(int num, int min, int max) {
		if(num < min) return min;
		if(num > max) return max;
		return num;
	}
	
	public static final double trimMinMax(double num, double min, double max) {
		if(num < min) return min;
		if(num > max) return max;
		return num;
	}
	
	public static final int trimMin(int num, int min) {
		if(num < min) return min;
		return num;
	}
}
