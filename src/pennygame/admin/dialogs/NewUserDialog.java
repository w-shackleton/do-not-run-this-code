package pennygame.admin.dialogs;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import pennygame.admin.CommInterfaces.CreateNewUser;
import pennygame.lib.Utils;

public class NewUserDialog extends JDialog {

	JPasswordField password;
	JTextField username, pennies, bottles;

	final CreateNewUser creator;
	final JFrame parent;

	private static final long serialVersionUID = -7829422924343861065L;

	public NewUserDialog(JFrame parent, CreateNewUser creator,
			final int pennyNum, final int bottleNum) {
		super(parent, "Create new user");
		this.parent = parent;
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		this.creator = creator;

		{
			JLabel label = new JLabel("Create new User");
			label.setBorder(new EmptyBorder(5, 15, 5, 5));
			add(label);
		}

		{
			JPanel userPanel = new JPanel();
			userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
			userPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

			JLabel label = new JLabel("Username");
			userPanel.add(label);

			username = new JTextField(20);
			userPanel.add(username);

			add(userPanel);
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
			JPanel valuePanel = new JPanel();
			valuePanel.setLayout(new BoxLayout(valuePanel, BoxLayout.X_AXIS));
			valuePanel.setBorder(new EmptyBorder(10, 20, 10, 20));

			JLabel label = new JLabel("Pennies");
			valuePanel.add(label);

			pennies = new JTextField("" + pennyNum, 5);
			valuePanel.add(pennies);

			label = new JLabel("Bottles");
			valuePanel.add(label);

			bottles = new JTextField("" + bottleNum, 5);
			valuePanel.add(bottles);

			add(valuePanel);
		}

		{
			JPanel bPanel = new JPanel();
			bPanel.setLayout(new BoxLayout(bPanel, BoxLayout.X_AXIS));
			bPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

			JButton createNew = new JButton("Create & Open new");
			createNew.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						NewUserDialog.this.creator.createNewUser(
								username.getText(),
								new String(password.getPassword()),
								Utils.trimMin(
										Integer.valueOf(bottles.getText()), 1),
								Utils.trimMin(
										Integer.valueOf(pennies.getText()), 1));
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null, "Please enter valid numbers", "Number error", JOptionPane.ERROR_MESSAGE);
					}
					setVisible(false);
					dispose();

					new NewUserDialog(
							NewUserDialog.this.parent,
							NewUserDialog.this.creator,
							Utils.trimMin(Integer.valueOf(pennies.getText()), 1),
							Utils.trimMin(Integer.valueOf(bottles.getText()), 1))
							.setVisible(true);
				}
			});
			bPanel.add(createNew);

			bPanel.add(Box.createRigidArea(new Dimension(10, 0)));

			JButton create = new JButton("Create");
			create.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						NewUserDialog.this.creator.createNewUser(
								username.getText(),
								new String(password.getPassword()),
								Utils.trimMin(
										Integer.valueOf(bottles.getText()), 1),
								Utils.trimMin(
										Integer.valueOf(pennies.getText()), 1));
					} catch (NumberFormatException e1) {
						JOptionPane.showMessageDialog(null, "Please enter valid numbers", "Number error", JOptionPane.ERROR_MESSAGE);
					}
					setVisible(false);
					dispose();
				}
			});
			bPanel.add(create);

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
