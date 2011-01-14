package pennygame.lib.clientutils;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import pennygame.lib.ext.WindowUtilities;
import pennygame.lib.queues.handlers.OnConnectionListener;
import pennygame.lib.queues.handlers.OnLoginHandler;

public abstract class LoginApplet<T extends SConn<? extends SConnMainThread, ? extends SConnPushHandler>> extends JApplet {

	/**
	 * The server to connect to
	 */
	protected String server;

	/**
	 * The port to connect to
	 */
	protected int port;

	/**
	 * Field which contains the username
	 */
	protected JTextField usernameField;

	/**
	 * Field which contains the password
	 */
	protected JPasswordField passwordField;
	
	/**
	 * Login Button
	 */
	JButton loginButton;

	/**
	 * Status label at the bottom
	 */
	protected JLabel statusLabel;

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4383710863273898699L;

	@Override
	public void init() {
		super.init();
		WindowUtilities.setNativeLookAndFeel();
		getGraphics().drawString("Loading...", 20, 30);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					loadAppletParameters();
					initUI();
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't successfully complete");
		}
	}

	protected void initUI() {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		add(content);

		{
			JPanel usernamePanel = new JPanel();
			usernamePanel.setLayout(new BoxLayout(usernamePanel,
					BoxLayout.X_AXIS));
			usernamePanel.setBorder(new EmptyBorder(20, 5, 10, 5));

			JLabel usernameText = new JLabel();
			usernameText.setText("Username: ");
			usernamePanel.add(usernameText);

			usernameField = new JTextField(80);
			usernameField.setMaximumSize(usernameField.getPreferredSize());
			usernamePanel.add(usernameField);

			content.add(usernamePanel);
		}

		{
			JPanel passwordPanel = new JPanel();
			passwordPanel.setLayout(new BoxLayout(passwordPanel,
					BoxLayout.X_AXIS));
			passwordPanel.setBorder(new EmptyBorder(20, 5, 10, 5));

			JLabel passwordText = new JLabel();
			passwordText.setText("Password: ");
			passwordPanel.add(passwordText);

			passwordPanel.add(Box.createRigidArea(new Dimension(5, 0)));

			passwordField = new JPasswordField(80);
			passwordField.setMaximumSize(passwordField.getPreferredSize());
			passwordPanel.add(passwordField);

			content.add(passwordPanel);
		}

		content.add(Box.createVerticalGlue());

		{
			JPanel loginButtonPanel = new JPanel();
			loginButtonPanel.setLayout(new BoxLayout(loginButtonPanel,
					BoxLayout.X_AXIS));
			loginButtonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

			loginButton = new JButton("Login");
			loginButtonPanel.add(loginButton);
			loginButton.setMaximumSize(loginButton.getPreferredSize());
			loginButton.addActionListener(onLoginButtonPressed);

			content.add(loginButtonPanel);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.X_AXIS));
			labelPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

			statusLabel = new JLabel(" ");
			labelPanel.add(statusLabel);

			content.add(labelPanel);
		}

		// content.setBackground(Color.white);
	}

	protected void loadAppletParameters() {
		String at = getParameter("server");
		server = (at != null) ? at : "ksa.uk.net";

		at = getParameter("port");
		port = (at != null) ? Integer.valueOf(at) : getPort();
	}
	
	protected abstract int getPort();

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		super.stop();
	}

	T serverConnection;

	protected final OnConnectionListener connectionListener = new OnConnectionListener() {

		@Override
		public void onConnectionLost() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(!connectionIsAvailable) // So that this doesn't happen when closing window
						statusLabel.setText("Couldn't connect!");
					enableLoginUI(true);
					closeMainWindow();
				}
			});
		}

		@Override
		public void onConnected() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					statusLabel.setText("Logging in...");
				}
			});
		}
	};
	
	private boolean connectionIsAvailable = false;

	protected final OnLoginHandler loginHandler = new OnLoginHandler() {

		@Override
		public void onLoginFailed() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					statusLabel.setText("Login failed, please try again.");
					enableLoginUI(true);
				}
			});
		}

		@Override
		public void onLoginCompleted() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					statusLabel.setText("Ready!");
					connectionIsAvailable = true;
					startMainWindow(serverConnection);
				}
			});
		}
	};
	
	/**
	 * Starts the main window of the client with the specified connection to the server
	 * @param serverConnection
	 */
	protected abstract void startMainWindow(T serverConnection);
	
	protected abstract void closeMainWindow();

	protected ActionListener onLoginButtonPressed = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			enableLoginUI(false);
			System.out.println("Starting to log in...");
			statusLabel.setText("Loading...");
			serverConnection = createNewConnection();
			statusLabel.setText("Connecting...");
			serverConnection.asyncStart();
		}
	};
	
	protected abstract T createNewConnection();
	
	protected void enableLoginUI(boolean enabled) {
		loginButton.setEnabled(enabled);
		usernameField.setEnabled(enabled);
		passwordField.setEnabled(enabled);
	}
}
