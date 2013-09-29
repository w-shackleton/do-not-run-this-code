package uk.digitalsquid.remme.misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.SparseArray;

/**
 * Static utilities for transforming lists.
 * @author william
 *
 */
public final class ListUtils {
	private ListUtils() {}
	
	/**
	 * Selects <code>num</code> random elements from the given list.
	 * @param from
	 * @param num
	 * @return
	 */
	public static <T> ArrayList<T> selectRandomSet(List<T> from, int num) {
		if(num < 0) return new ArrayList<T>();
		if(num > from.size()) return new ArrayList<T>(from);
		
		final ArrayList<T> ret = new ArrayList<T>();
		final int size = from.size();
		
		/**
		 * Insert a bunch of random elements
		 */
		for(int i = 0; i < num; i++) {
			ret.add(from.get(Const.RAND.nextInt(size)));
		}
		
		return ret;
	}
	
	/**
	 * Selects <code>num</code> random elements from the given list, making sure that none are the given variable <code>excluding</code>,
	 * and that no two elements are the same.
	 * TODO: Unused
	 * @param from
	 * @param num
	 * @return
	 */
	public static <T> ArrayList<T> selectRandomExclusiveDistinctSet(List<T> from, final Comparator<T> comp, final T excluding, final int num) {
		if(num < 0) return new ArrayList<T>();
		if(num > from.size()) return new ArrayList<T>(from);
		
		final ArrayList<T> ret = new ArrayList<T>();
		final int size = from.size();
		
		// Is this necessary, considering the duplicate test below? It may reduce the test below's chance of being used.
		final int[] choices = generateRandomDistinctNumbers(size, num);
		
		for(int i = 0; i < num; i++) {
			final T elem = from.get(choices[i]);
			boolean ok = true;
			for(int t = 0; t < i; t++) { // t is the iterator for checking for duplicates
				if(comp.compare(elem, ret.get(t)) == 0 ||
						comp.compare(elem, excluding) == 0) { // Check if already added / is excluded one.
					// Try again, but don't decrease i. This could cause a loop in some rare cases. Just have 1 too less this time.
					ok = false;
					break;
				}
			}
			if(ok) ret.add(elem);
		}
		return ret;
	}
	
	/**
	 * Generates a random set of distinct ints between [0,max). If max &lt; count then some are repeated.
	 * @param max
	 * @param count
	 * @return
	 */
	public static int[] generateRandomDistinctNumbers(int max, int count) {
		int[] ret = new int[count];
		if(max < count) { // Not possible, must have some duplicates.
			for(int i = 0; i < count; i++) {
				ret[i] = Const.RAND.nextInt(max);
			}
		} else {
			for(int i = 0; i < count; i++) {
				ret[i] = Const.RAND.nextInt(max);
				for(int t = 0; t < i; t++) { // Iterate through checking
					if(ret[t] == ret[i]) {
						i--; // Try again
						break;
					}
				}
			}
		}
		return ret;
	}
	
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
	
	public static <T> Set<T> values(SparseArray<T> array) {
		Set<T> ret = new HashSet<T>(array.size());
		for(int i = 0; i < array.size(); i++) {
			T val = array.get(array.keyAt(i));
			if(val != null) ret.add(val);
		}
		return ret;
	}
}
