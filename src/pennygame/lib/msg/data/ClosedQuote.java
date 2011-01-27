package pennygame.lib.msg.data;

import java.io.Serializable;
import java.sql.Timestamp;

public class ClosedQuote implements Serializable {

	private static final long serialVersionUID = -5323649194093314637L;
	
	public static final int TYPE_BUY = 1;
	public static final int TYPE_SELL = 2;

	private final int id;
	
	/**
	 * The type of trade, either {@link #TYPE_BUY} or {@link #TYPE_SELL}.
	 */
	private final int type;
	
	private final int pennies, bottles, value;
	
	private final String fromName;
	private final String toName;
	
	private final Timestamp time;
	
	public ClosedQuote(int id, int type, String fromName, String toName, int pennies, int bottles, Timestamp time) {
		this.id = id;
		this.type = type;
		this.fromName = fromName;
		this.toName = toName;
		this.time = time;
		this.pennies = pennies;
		this.bottles = bottles;
		this.value = pennies * bottles;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public int getValue() {
		return value;
	}

	public int getBottles() {
		return bottles;
	}

	public int getPennies() {
		return pennies;
	}

	public String getFromName() {
		return fromName;
	}

	public Timestamp getTime() {
		return time;
	}

	public String getToName() {
		return toName;
	}
}
