package pennygame.lib.msg.data;


public class User {
	private String username;
	
	public User(String username) {
		this.setUsername(username);
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}
}
