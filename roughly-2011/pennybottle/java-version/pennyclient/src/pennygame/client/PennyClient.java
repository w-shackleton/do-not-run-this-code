package pennygame.client;

import pennygame.client.queues.CSConn;
import pennygame.lib.GlobalPreferences;
import pennygame.lib.clientutils.LoginApplet;
import pennygame.lib.msg.data.User;

/**
 * The login applet of the client
 * @author william
 *
 */
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
	
	PennyFrame frame;

	@Override
	protected void startMainWindow(CSConn serverConnection, User userInfo, boolean paused) {
		frame = new PennyFrame(serverConnection, this, userInfo, paused);
		frame.setVisible(true);
	}

	@Override
	protected void closeMainWindow() {
		if(frame == null) return;
		frame.setVisible(false);
	}
	
	protected void frameFinished() {
		frame = null;
		statusLabel.setText("");
		passwordField.setText("");
		enableLoginUI(true);
	}
}