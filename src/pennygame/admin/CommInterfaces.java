package pennygame.admin;

/**
 * This was a class of interfaces for various functions around the admin program,
 * but then I got lazy and just passed it the AdminSConn class.
 * 
 * @author william
 * 
 */
public final class CommInterfaces {
	private CommInterfaces() {
	}

	public interface CreateNewUser {
		public void createNewUser(String username, String password,
				int bottles, int pennies);
		
		public void changePassword(int id, String password);
	}
}
