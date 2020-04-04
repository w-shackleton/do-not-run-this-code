package pennygame.admin.dialogs;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.border.EmptyBorder;

import pennygame.admin.CommInterfaces.CreateNewUser;

/**
 * A JDialog to change the user's password
 * @author william
 *
 */
public class ChangePasswordDialog extends JDialog {

	JPasswordField password;
	public String result = null;

	private static final long serialVersionUID = -7829422924343861065L;

	public ChangePasswordDialog(Frame parent, final CreateNewUser createNewUserHandler, final int userID) {
		super(parent, "Change Password"); 
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		{
			JLabel label = new JLabel("Change Password");
			label.setBorder(new EmptyBorder(5, 15, 5, 5));
			add(label);
		}
		
		{
			JPanel passPanel = new JPanel();
			passPanel.setLayout(new BoxLayout(passPanel, BoxLayout.X_AXIS));
			passPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

			JLabel label = new JLabel("Password");
			passPanel.add(label);

			password = new JPasswordField(10);
			passPanel.add(password);

			add(passPanel);
		}
		
		{
			JPanel bPanel = new JPanel();
			bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));
			bPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

			bPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton change = new JButton("Change");
			change.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					createNewUserHandler.changePassword(userID, new String(password.getPassword()));
					setVisible(false);
					dispose();
				}
			});
			bPanel.add(change);

			bPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					setVisible(false);
					dispose();
				}
			});
			bPanel.add(cancel);

			add(bPanel);
		}

		pack();
	}

}
