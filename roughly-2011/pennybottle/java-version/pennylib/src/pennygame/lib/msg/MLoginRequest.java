package pennygame.lib.msg;

/**
 * {@link PennyMessage} containing the username and encrypted password.
 * @author william
 *
 */
public class MLoginRequest extends PennyMessage {
	
	private static final long serialVersionUID = 1287492396858755314L;
	
	public final String username;
	public final byte[] pass;
	
	public MLoginRequest(String username, byte[] pass) {
		this.username = username;
		this.pass = pass;
	}
}
