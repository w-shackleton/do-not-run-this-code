package pennygame.projector;

import pennygame.lib.GlobalPreferences;
import pennygame.lib.clientutils.LoginApplet;
import pennygame.lib.msg.data.User;
import pennygame.projector.queues.PSConn;

public class ProjectorClient extends LoginApplet<PSConn> {

	private static final long serialVersionUID = -8605228048232940061L;

	@Override
	protected PSConn createNewConnection() {
		return new PSConn(server, port, usernameField.getText(), new String(passwordField.getPassword()), loginHandler, connectionListener);
	}

	@Override
	protected int getPort() {
		return GlobalPreferences.getProjectorPort();
	}
	
	@Override
	public void init() {
		super.init();
		usernameField.setText("projector");
	}
	
	ProjectorFrame frame;

	@Override
	protected void startMainWindow(PSConn serverConnection, User userInfo, boolean paused) {
		frame = new ProjectorFrame(serverConnection, this);
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