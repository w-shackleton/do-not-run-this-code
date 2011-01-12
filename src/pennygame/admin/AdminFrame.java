package pennygame.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import pennygame.admin.dialogs.NewUserDialog;
import pennygame.admin.queues.AdminSConn;

public final class AdminFrame extends JFrame implements WindowListener {
	
	protected final AdminSConn serv;
	protected final PennyAdmin applet;

	private static final long serialVersionUID = -8102000347833420431L;

	public AdminFrame(AdminSConn serv, PennyAdmin applet) {
		super("Pennygame Admin");
		this.serv = serv;
		
		this.applet = applet;
		
		addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		initUI();
	}
	
	protected void initUI() {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		JTabbedPane tabs = new JTabbedPane();
		content.add(tabs);
		
		// Initialise Tabs
		{
			// Users tab
			JPanel userTab = new JPanel();
			userTab.setLayout(new BoxLayout(userTab, BoxLayout.Y_AXIS));
			
			tabs.addTab("Users", null, userTab, "Manage Users in the game");
			
			JButton addUser = new JButton("Add new User");
			addUser.addActionListener(addUserListener);
			userTab.add(addUser);
		}
		
		add(content);
		pack();
	}

	@Override
	public void windowActivated(WindowEvent arg0) { }

	@Override
	public void windowClosed(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) { 
		int n = JOptionPane.showConfirmDialog(this, "Do you really want to close?", "Close Penny Admin?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(n == JOptionPane.YES_OPTION)
		{
			serv.stop();
			setVisible(false);
			dispose();
			applet.adminFrameFinished();
		}
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) { }

	@Override
	public void windowDeiconified(WindowEvent arg0) { }

	@Override
	public void windowIconified(WindowEvent arg0) { }

	@Override
	public void windowOpened(WindowEvent arg0) { }
	
	protected ActionListener addUserListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			NewUserDialog d = new NewUserDialog(AdminFrame.this);
			d.setVisible(true);
		}
	};
}
