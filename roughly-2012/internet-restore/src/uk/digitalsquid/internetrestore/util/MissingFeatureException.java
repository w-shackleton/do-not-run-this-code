package uk.digitalsquid.internetrestore.util;

import java.io.IOException;

import android.content.Context;

/**
 * A feature is missing from the phone, preventing the app from starting
 * @author william
 *
 */
public class MissingFeatureException extends IOException {

	private static final long serialVersionUID = 3192132852959378220L;
	
	public MissingFeatureException(String message, int localisedMessage) {
		super(message);
		setLocalisedMessage(localisedMessage);
	}
	
	public MissingFeatureException(String message, int localisedMessage, Object... formatArgs) {
		super(message);
		setLocalisedMessage(localisedMessage);
		this.formatArgs = formatArgs;
	}

	private int localisedMessage;
	
	private Object[] formatArgs;
	
	public int getLocalisedMessageId() {
		return localisedMessage;
	}
	
	public String getLocalisedMessage(Context context) {
		if(formatArgs == null) return context.getString(getLocalisedMessageId());
		else return context.getString(getLocalisedMessageId(), formatArgs);
	}

	public void setLocalisedMessage(int localisedMessage) {
		this.localisedMessage = localisedMessage;
	}
}
