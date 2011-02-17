package pennygame.lib.msg.data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * A quote which has been:
 * <ul>
 * 	<li>Closed ({@link #FINISH_CLOSED})</li>
 * 	<li>Cancelled ({@link #FINISH_CANCELLED})</li>
 * 	<li>Timeout'd ({@link #FINISH_TIMEOUTED})</li>
 * </ul>
 * @author william
 *
 */
public class ClosedQuote implements Serializable {

	private static final long serialVersionUID = -5323649194093314637L;
	
	public static final int TYPE_BUY = 1;
	public static final int TYPE_SELL = 2;
	
	public static final int FINISH_CLOSED = 1;
	public static final int FINISH_CANCELLED = 2;
	public static final int FINISH_TIMEOUTED = 3;

	private final int id;
	
	/**
	 * The type of trade, either {@link #TYPE_BUY} or {@link #TYPE_SELL}.
	 */
	private final int type;
	
	private final int pennies, bottles, value;
	
	private final String fromName;
	private final String toName;
	
	private final Timestamp timeAccepted, timeCreated;
	private final int finishReason;
	
	public ClosedQuote(int id, int type, String fromName, String toName, int pennies, int bottles, Timestamp time) {
		this.id = id;
		this.type = type;
		this.fromName = fromName;
		this.toName = toName;
		this.timeAccepted = time;
		timeCreated = time;
		this.pennies = pennies;
		this.bottles = bottles;
		this.value = pennies * bottles;
		finishReason = FINISH_CLOSED;
	}

	public ClosedQuote(int id, int type, String fromName, String toName, int pennies, int bottles, Timestamp timeAccepted, Timestamp timeCreated, int finishReason) {
		this.id = id;
		this.type = type;
		this.fromName = fromName;
		this.toName = toName;
		this.timeAccepted = timeAccepted;
		this.timeCreated = timeCreated;
		this.pennies = pennies;
		this.bottles = bottles;
		this.value = pennies * bottles;
		this.finishReason = finishReason;
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
		return timeAccepted;
	}
	
	/**
	 * Synonymous to {@link #getTime()}
	 * @return
	 */
	public Timestamp getTimeAccepted() {
		return timeAccepted;
	}

	public String getToName() {
		return toName;
	}

	public Timestamp getTimeCreated() {
		return timeCreated;
	}

	public int getFinishReason() {
		return finishReason;
	}
}
