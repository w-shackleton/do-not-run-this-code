package pennygame.lib.msg;

import java.security.PublicKey;

/**
 * Contains the RSA key, sent from SERVER
 * @author william
 *
 */
public class MLoginInitiate extends PennyMessage {
	public MLoginInitiate(PublicKey publicKey) {
		this.publicKey = publicKey;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8056724171640538446L;

	public PublicKey publicKey;
}
