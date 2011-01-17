package pennygame.lib.msg.data;

import java.io.Serializable;


/**
 * Represents a single user
 * @author william
 *
 */
public class User implements Serializable {
	private static final long serialVersionUID = -8823359246201248197L;
	
	private final int id;
	private String username, friendlyname;
	private int pennies, bottles;
	
	public User(String username, int pennies, int bottles) {
		setUsername(username);
		setFriendlyname(username);
		setPennies(pennies);
		setBottles(bottles);
		id = 0;
	}
	
	public User(int id, String username, String friendlyname, int pennies, int bottles) {
		setUsername(username);
		setFriendlyname(friendlyname);
		setPennies(pennies);
		setBottles(bottles);
		this.id = id;
	}
	
	public User(int id, String username, String friendlyname) {
		this.id = id;
		this.username = username;
		this.friendlyname = friendlyname;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setPennies(int pennies) {
		this.pennies = pennies;
	}

	public int getPennies() {
		return pennies;
	}

	public void setBottles(int bottles) {
		this.bottles = bottles;
	}

	public int getBottles() {
		return bottles;
	}

	public int getId() {
		return id;
	}

	public void setFriendlyname(String friendlyname) {
		this.friendlyname = friendlyname;
	}

	public String getFriendlyname() {
		return friendlyname;
	}
}
