package uk.digitalsquid.contactrecall.misc;

/**
 * A generic function
 * @author william
 *
 * @param <R>
 * @param <A>
 */
public interface Function<R, A> {
	public R call(A arg);
}
