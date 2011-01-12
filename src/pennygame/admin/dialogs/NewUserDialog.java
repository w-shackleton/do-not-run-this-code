package pennygame.admin.dialogs;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class NewUserDialog extends JDialog {

	JPasswordField password;
	JTextField username, pennies, bottles;
	
	private static final long serialVersionUID = -7829422924343861065L;
	
	public NewUserDialog(JFrame parent) {
		super(parent, "Create new user");
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		{
			JLabel label = new JLabel("Create new User");
			label.setLayout(new BorderLayout());
			label.setBorder(new EmptyBorder(15, 15, 5, 5));
			add(label);
		}
		
		{
			JPanel userPanel = new JPanel();
			userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
			userPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
			
			JLabel label = new JLabel("Username");
			userPanel.add(label);
			
			username = new JTextField(80);
			userPanel.add(username);
			
			add(userPanel);
		}
		
		{
			JPanel passPanel = new JPanel();
			passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.X_AXIS));
			passPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
			
			JLabel label = new JLabel("Password");
			passPanel.add(label);
			
			password = new JPasswordField(80);
			passPanel.add(password);
			
			add(passPanel);
		}
		
		{
			JPanel valuePanel = new JPanel();
			valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));
			valuePanel.setBorder(new EmptyBorder(10, 20, 10, 20));
			
			JLabel label = new JLabel("Pennies");
			valuePanel.add(label);
			
			password = new JPasswordField(80);
			valuePanel.add(password);
			
			add(valuePanel);
		}
		
		pack();
	}

}
