package uk.digitalsquid.remme.misc;

import java.util.Arrays;
import java.util.List;

/**
 * Static utilities for transforming lists.
 * @author william
 *
 */
public final class ListUtils {
	private ListUtils() {}
	
	/**
	 * Concatenates all given lists into empty
	 * @param empty An empty list
	 * @param lists
	 * @return
	 */
	public static <T extends List<E>, E> T concat(T empty, T...lists) {
		for(T list : lists) {
			if(list != null)
				empty.addAll(list);
		}
		return empty;
	}
	
	public static <T> T[] concat(T[] first, T[]... rest) {
		int totalLength = first.length;
		for (T[] array : rest) {
			totalLength += array.length;
		}
		T[] result = Arrays.copyOf(first, totalLength);
		int offset = first.length;
		for (T[] array : rest) {
			System.arraycopy(array, 0, result, offset, array.length);
			offset += array.length;
		}
		return result;
	}
}
