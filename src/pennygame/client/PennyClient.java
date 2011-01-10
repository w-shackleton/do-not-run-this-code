package pennygame.client;

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

import pennygame.client.ext.WindowUtilities;
import pennygame.lib.GlobalPreferences;

public class PennyClient extends JApplet {
	
	/**
	 * The server to connect to
	 */
	protected String server;
	
	/**
	 * The port to connect to
	 */
	protected int port;
	
	protected String imgDir;
	
	/**
	 * Field which contains the username
	 */
	JTextField usernameField;
	
	/**
	 * Field which contains the password
	 */
	JPasswordField passwordField;

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -4383710863273898699L;
	
	@Override
	public void init() {
		super.start();
		WindowUtilities.setNativeLookAndFeel();
		getGraphics().drawString("Loading...", 20, 30);
		try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
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
	        usernamePanel.setLayout(new BoxLayout(usernamePanel, BoxLayout.X_AXIS));
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
	        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
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
        	loginButtonPanel.setLayout(new BoxLayout(loginButtonPanel, BoxLayout.X_AXIS));
        	loginButtonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        	
        	JButton loginButton = new JButton("Login");
        	loginButtonPanel.add(loginButton);
        	loginButton.setMaximumSize(loginButton.getPreferredSize());
        	loginButton.addActionListener(onLoginButtonPressed);
        	
        	content.add(loginButtonPanel);
        }
        
        // content.setBackground(Color.white);
	}
	
	protected void loadAppletParameters() {
		String at = getParameter("server");
		server = (at != null) ? at : "ksa.uk.net";
		
		at = getParameter("port");
		port = (at != null) ? Integer.valueOf(at) : GlobalPreferences.getPort();
		
		at = getParameter("imgdir");
		imgDir = (at != null) ? at : "images";
	}
	
	@Override
	public void start() {
		super.start();
	}
	
	@Override
	public void stop() {
		super.stop();
	}
	
	protected ActionListener onLoginButtonPressed = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent evt) {
			System.out.println("Pressed!");
		}
	};
}
