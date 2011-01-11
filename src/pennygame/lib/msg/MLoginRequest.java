package pennygame.lib.msg;

public class MLoginRequest extends PennyMessage {
	
	private static final long serialVersionUID = 1287492396858755314L;
	
	public final String username;
	public final byte[] pass;
	
	public MLoginRequest(String username, byte[] pass) {
		this.username = username;
		this.pass = pass;
	}
}
