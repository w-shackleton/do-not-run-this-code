package pennygame.admin;

/**
 * This is a class of interfaces for various functions around the admin program
 * @author william
 *
 */
public final class CommInterfaces {
	private CommInterfaces() {}
	
	public interface CreateNewUser {
		public void createNewUser(String username, String password, int bottles, int pennies);
	}
}
