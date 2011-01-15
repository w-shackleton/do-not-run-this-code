package pennygame.lib.msg.adm;

import pennygame.lib.msg.PennyMessage;

public class MAUserModifyRequest extends PennyMessage {

	private static final long serialVersionUID = -4125765660852228104L;
	
	public static final int DELETE_USER = 1;
	
	/**
	 * Change the user's password. Data in this case is a byte[] with the encrypted hashed password.
	 */
	public static final int CHANGE_PASSWORD = 2;
	
	/**
	 * Change the user's friendlyname. Data in this case is a String with the new name.
	 */
	public static final int CHANGE_FRIENDLYNAME = 3;
	
	private final int id;
	private final int action;
	
	private final Object data;

	public MAUserModifyRequest(int id, int action) {
		this.id = id;
		this.action = action;
		data = "";
	}

	public MAUserModifyRequest(int id, int action, Object data) {
		this.id = id;
		this.action = action;
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public int getAction() {
		return action;
	}

	public Object getData() {
		return data;
	}
}
