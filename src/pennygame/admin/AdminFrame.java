package pennygame.admin;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import pennygame.admin.queues.AdminSConn;
import pennygame.admin.uiparts.OtherTab;
import pennygame.admin.uiparts.PastTradesTab;
import pennygame.admin.uiparts.UserTab;

public final class AdminFrame extends JFrame implements WindowListener {
	
	protected final AdminSConn serv;
	protected final PennyAdmin applet;
	
	JTabbedPane tabs;
	
	public final UserTab userTab;
	public final OtherTab otherTab;
	public final PastTradesTab tradesTab;
	
	JTextField quoteTimeout, quoteNumber;

	private static final long serialVersionUID = -8102000347833420431L;

	public AdminFrame(AdminSConn serv, PennyAdmin applet) {
		super("Pennygame Admin");
		this.serv = serv;
		this.applet = applet;
		
		userTab = new UserTab(this, serv);
		otherTab = new OtherTab(this, serv);
		tradesTab = new PastTradesTab(this, serv);
		
		serv.setParentFrame(this);
		
		addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		initUI();
		
	}
	
	protected void initUI() {
		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		
		{
			final JToggleButton pausedButton = new JToggleButton("Pause Game");
			pausedButton.setBorder(new EmptyBorder(10, 10, 10, 10));
			pausedButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					serv.pause(pausedButton.isSelected());
					if(pausedButton.isSelected())
						pausedButton.setText("* PAUSED *");
					else
						pausedButton.setText("Pause Game");
				}
			});
			topPanel.add(pausedButton);
		}
		
		{
			topPanel.add(Box.createRigidArea(new Dimension(20, 1)));
			
			JLabel label = new JLabel("Quote Timeout: ");
			topPanel.add(label);
			
			quoteTimeout = new JTextField(3);
			topPanel.add(quoteTimeout);
			
			JButton quoteTimeoutSet = new JButton("Set timeout");
			quoteTimeoutSet.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int timeout;
					try {
						timeout = Integer.valueOf(quoteTimeout.getText());
					} catch(NumberFormatException e) {
						return;
					}
					serv.setQuoteTimeout(timeout);
				}
			});
			topPanel.add(quoteTimeoutSet);
		}
		
		{
			topPanel.add(Box.createRigidArea(new Dimension(20, 1)));
			
			JLabel label = new JLabel("Number of quotes (x2): ");
			topPanel.add(label);
			
			quoteNumber = new JTextField(3);
			topPanel.add(quoteNumber);
			
			JButton quoteTimeoutSet = new JButton("Set number");
			quoteTimeoutSet.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					int timeout;
					try {
						timeout = Integer.valueOf(quoteNumber.getText());
					} catch(NumberFormatException e) {
						return;
					}
					serv.setQuoteNumber(timeout);
				}
			});
			topPanel.add(quoteTimeoutSet);
		}
		
		content.add(topPanel);
		
		tabs = new JTabbedPane();
		content.add(tabs);
		
		// Initialise Tabs
		tabs.addTab("Users", null, userTab, "Manage Users in the game");
		tabs.addTab("Other", null, otherTab, "Other utilities");
		tabs.addTab("Trade history", null, tradesTab, "Manage past trades");
		
		add(content);
		
		pack();
		
		serv.getQuoteTimeout();
		serv.getQuoteNumber();
		serv.getGraphLength();
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
	
	public void setQuoteTimeout(int timeout) {
		quoteTimeout.setText("" + timeout);
	}
	
	public void setQuoteNumber(int num) {
		quoteNumber.setText("" + num);
	}
}
