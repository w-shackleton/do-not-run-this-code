package pennygame.lib.msg.adm;

import java.util.LinkedList;

import pennygame.lib.msg.PennyMessage;
import pennygame.lib.msg.data.User;

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
