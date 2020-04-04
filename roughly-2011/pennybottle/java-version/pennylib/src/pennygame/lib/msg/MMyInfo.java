package pennygame.lib.msg;

import pennygame.lib.msg.data.PB;

/**
 * A {@link PennyMessage} containing the user's wealth and potential wealth.
 * @author william
 *
 */
public class MMyInfo extends PennyMessage {

	private static final long serialVersionUID = -361006050829383407L;

	private final PB current, potential, potential1, potential2;
	private final int estimatedWorth;
	
	public MMyInfo(PB current, PB potential, PB p1, PB p2, int estimatedWorth) {
		this.current = current;
		this.potential = potential;
		potential1 = p1;
		potential2 = p2;
		this.estimatedWorth = estimatedWorth;
	}

	public PB getCurrent() {
		return current;
	}

	public PB getPotential() {
		return potential;
	}

	/**
	 * Gets the potential worth with the most BOTTLES
	 * @return
	 */
	public PB getPotential1() {
		return potential1;
	}

	/**
	 * Gets the potential worth with the most PENNIES
	 */
	public PB getPotential2() {
		return potential2;
	}

	public int getEstimatedWorth() {
		return estimatedWorth;
	}
}
