package pennygame.client;

import pennygame.client.queues.CSConn;
import pennygame.lib.GlobalPreferences;
import pennygame.lib.clientutils.LoginApplet;

public class PennyClient extends LoginApplet<CSConn> {

	private static final long serialVersionUID = -8605228048232940061L;

	@Override
	protected CSConn createNewConnection() {
		return new CSConn(server, port, usernameField.getText(), new String(passwordField.getPassword()), loginHandler, connectionListener);
	}

	@Override
	protected int getPort() {
		return GlobalPreferences.getPort();
	}
}