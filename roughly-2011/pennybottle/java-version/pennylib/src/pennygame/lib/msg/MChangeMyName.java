package pennygame.lib.msg;

/**
 * Allows the user to change their 'friendly name'
 * @author william
 *
 */
public class MChangeMyName extends PennyMessage {

	private static final long serialVersionUID = -3376944380887144871L;

	private final String newName;
	
	public MChangeMyName(String newName) {
		this.newName = newName;
	}

	public String getNewName() {
		return newName;
	}
}
