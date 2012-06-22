package uk.digitalsquid.internetrestore.util;

/**
 * A feature is missing from the phone, preventing the app from starting
 * @author william
 *
 */
public class MissingFeatureException extends UnsupportedOperationException {
	public MissingFeatureException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 3192132852959378220L;

}
