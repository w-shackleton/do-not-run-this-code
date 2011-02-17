package pennygame.lib.msg.adm;

import pennygame.lib.msg.PennyMessage;

/**
 * An admin request to modify a user's details
 * @author william
 *
 */
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

	/**
	 * Creates a new {@link MAUserModifyRequest} without any data
	 * @param id
	 * @param action
	 */
	public MAUserModifyRequest(int id, int action) {
		this.id = id;
		this.action = action;
		data = "";
	}

	/**
	 * Creates a new {@link MAUserModifyRequest} with data
	 * @param id
	 * @param action
	 * @param data The data to be passed
	 */
	public MAUserModifyRequest(int id, int action, Object data) {
		this.id = id;
		this.action = action;
		this.data = data;
	}

	/**
	 * Gets the userID for this request
	 * @return
	 */
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
