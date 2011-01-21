package pennygame.lib.msg.data;

import java.io.Serializable;

public class PB implements Serializable {

	private static final long serialVersionUID = -4457712869685528411L;

	private final int pennies, bottles, total;
	
	public PB(int pennies, int bottles, int estimatedWorth) {
		this.pennies = pennies;
		this.bottles = bottles;
		total = bottles * estimatedWorth + pennies;
	}

	/**
	 * Constructs a {@link PB} representing a quote
	 * @param pennies
	 * @param bottles
	 */
	public PB(int pennies, int bottles) {
		this.pennies = pennies;
		this.bottles = bottles;
		total = bottles * pennies;
	}

	public int getPennies() {
		return pennies;
	}

	public int getBottles() {
		return bottles;
	}

	public int getTotal() {
		return total;
	}
}
