package pennygame.lib;

import java.io.Serializable;

/**
 * A message in the Pennygame system. This is the base class.
 * 
 * @author william
 * 
 */
public class PennyMessage implements Serializable {

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -5315670581578986435L;

	public int id;

	public String message = "Hello!";
}
