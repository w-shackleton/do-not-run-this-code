package pennygame.lib.msg.adm;

import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.data.User;

public class MAUserAdd extends PennyMessage {

	private static final long serialVersionUID = 3560377697026290394L;
	
	public User user;
	
	public byte[] newPassword;
	
	public MAUserAdd(String username, byte[] encPassword, int pennies, int bottles) {
		user = new User(username, pennies, bottles);
		newPassword = encPassword;
	}
}
