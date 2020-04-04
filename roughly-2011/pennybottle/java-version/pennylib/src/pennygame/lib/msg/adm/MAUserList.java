package pennygame.lib.msg.adm;

import java.util.LinkedList;

import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.data.User;

/**
 * A message to the admin containing a list of users to display.
 * @author william
 *
 */
public class MAUserList extends PennyMessage {

	private static final long serialVersionUID = 421754893264581943L;
	
	private final LinkedList<User> users;

	public MAUserList(LinkedList<User> users) {
		this.users = users;
	}

	public LinkedList<User> getUsers() {
		return users;
	}
}
