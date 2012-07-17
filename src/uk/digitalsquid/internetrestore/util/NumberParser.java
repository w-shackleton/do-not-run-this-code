package uk.digitalsquid.internetrestore.util;

/**
 * Simple number parsing class that doesn't throw exceptions
 * @author william
 *
 */
public class NumberParser {
	private NumberParser() { }
	/**
	 * Parses an integer safely. Returns 0 on error.
	 * @param number
	 * @return
	 */
	public static final int parseIntSafe(String number) {
		try {
			return Integer.parseInt(number);
		} catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public static final boolean parseBoolSafe(String bool) {
		String val = bool.toLowerCase();
		if("true".equals(val)) return true;
		if("false".equals(val)) return false;
		if("1".equals(val)) return true;
		if("0".equals(val)) return false;
		return false;
	}
}
