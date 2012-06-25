package uk.digitalsquid.internetrestore.util;

import java.io.IOException;

/**
 * A feature is missing from the phone, preventing the app from starting
 * @author william
 *
 */
public class MissingFeatureException extends IOException {
	public MissingFeatureException(String message) {
		super(message);
	}

	private static final long serialVersionUID = 3192132852959378220L;

}
