package pennygame.admin;

import pennygame.admin.queues.AdminSConn;
import pennygame.lib.GlobalPreferences;
import pennygame.lib.clientutils.LoginApplet;

public class PennyAdmin extends LoginApplet<AdminSConn> {

	private static final long serialVersionUID = -3220289641005857212L;

	@Override
	protected AdminSConn createNewConnection() {
		return new AdminSConn(server, port, usernameField.getText(), new String(passwordField.getPassword()), loginHandler, connectionListener);
	}

	@Override
	protected int getPort() {
		return GlobalPreferences.getAdminport();
	}
	
	AdminFrame adminFrame;

	@Override
	protected void startMainWindow(AdminSConn serverConnection) {
		adminFrame = new AdminFrame(serverConnection, this);
		adminFrame.setVisible(true);
	}

	@Override
	protected void closeMainWindow() {
		if(adminFrame == null) return;
		adminFrame.setVisible(false);
	}
	
	protected void adminFrameFinished() {
		adminFrame = null;
		statusLabel.setText("");
		passwordField.setText("");
		enableLoginUI(true);
	}
}
