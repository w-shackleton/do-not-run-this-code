package pennygame.admin.uiparts;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import pennygame.admin.AdminFrame;
import pennygame.admin.dialogs.ChangePasswordDialog;
import pennygame.admin.dialogs.NewUserDialog;
import pennygame.admin.queues.AdminSConn;
import pennygame.lib.msg.adm.MAUserModifyRequest;
import pennygame.lib.msg.data.User;

/**
 * The tab containing user information. For use by {@link AdminFrame}
 * @author william
 *
 */
public class UserTab extends JPanel {

	private static final long serialVersionUID = 5439026269937152830L;
	
	protected final AdminFrame parent;
	
	protected final AdminSConn serv;
	
	/**
	 * Scroller containing this table
	 */
	protected JScrollPane scrollPane;
	protected JTable userTable;
	
	protected JPopupMenu userTableContextMenu;
	
	/**
	 * The TRUE value of a bottle
	 */
	protected final JPasswordField bottleValue;

	public UserTab(final AdminFrame parent, final AdminSConn serv) {
		super(); 
		
		this.serv = serv;
		this.parent = parent;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel userTabButtons = new JPanel();
		userTabButtons.setLayout(new BoxLayout(userTabButtons, BoxLayout.X_AXIS));
		
		JButton addUser = new JButton("Add new User");
		addUser.setBorder(new EmptyBorder(10, 10, 10, 10));
		addUser.addActionListener(addUserListener);
		userTabButtons.add(addUser);
		
		JButton refreshUsers = new JButton("Refresh");
		refreshUsers.setBorder(new EmptyBorder(10, 10, 10, 10));
		refreshUsers.addActionListener(refreshUsersListener);
		userTabButtons.add(refreshUsers);
		
		add(userTabButtons);
		
		userTable = new JTable(userTableModel);
		userTable.getColumnModel().getColumn(0).setPreferredWidth(140);
		userTable.getColumnModel().getColumn(1).setPreferredWidth(140);
		userTable.setAutoCreateRowSorter(true);
		
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Context menu
		userTableContextMenu = new JPopupMenu();
		userTable.addMouseListener(userListRightClickListener);
		
		JMenuItem deleteUser = new JMenuItem("Delete User");
		deleteUser.addActionListener(userListRightClickDeleteListener);
		userTableContextMenu.add(deleteUser);
		
		JMenuItem changePassword = new JMenuItem("Change Password");
		changePassword.addActionListener(userListRightClickChangePasswordListener);
		userTableContextMenu.add(changePassword);
		
		JMenuItem changeFriendlyName = new JMenuItem("Change Friendly Name");
		changeFriendlyName.addActionListener(userListRightClickChangeFriendlyNameListener);
		userTableContextMenu.add(changeFriendlyName);
		
		JMenuItem changePennies = new JMenuItem("Change Pennies");
		changePennies.addActionListener(userListRightClickChangePenniesListener);
		userTableContextMenu.add(changePennies);
		
		JMenuItem changeBottles = new JMenuItem("Change Bottles");
		changeBottles.addActionListener(userListRightClickChangeBottlesListener);
		userTableContextMenu.add(changeBottles);
		
		scrollPane = new JScrollPane(userTable);
		scrollPane.setPreferredSize(new Dimension(380, 300));
		userTable.setFillsViewportHeight(true);
		scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(scrollPane);
		
		{ // Bottom bar
			Box box = Box.createHorizontalBox();
			
			JLabel label = new JLabel("Set true bottle:");
			add(label);
			
			bottleValue = new JPasswordField(5);
			bottleValue.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					Integer value;
					try {
						value = Integer.valueOf(String.valueOf(bottleValue.getPassword()));
					} catch(NumberFormatException e) {
						JOptionPane.showMessageDialog(parent, "Entered value wasn't a number", "Number error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					serv.setBottleValue(value);
				}
			});
			
			add(box);
		}
	}
	
	protected ActionListener addUserListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			NewUserDialog d = new NewUserDialog(UserTab.this.parent, serv.createNewUserHandler, 10000, 1000);
			d.setVisible(true);
		}
	};
	
	protected ActionListener refreshUsersListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			serv.refreshUserList();
		}
	};
	
	/**
	 * Model for table userlist
	 */
	protected AbstractTableModel userTableModel = new AbstractTableModel() {
		
		private static final long serialVersionUID = -7372554700154555522L;

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			switch(columnIndex) {
			case 0: // Username
				return userList.get(rowIndex).getUsername();
			case 1: // Friendlyname
				return userList.get(rowIndex).getFriendlyname();
			case 2: // Pennies
				return userList.get(rowIndex).getPennies();
			case 3: // Bottles
				return userList.get(rowIndex).getBottles();
			default:
				return "";
			}
		}
		
		@Override
		public void setValueAt(Object value, int row, int col) {
			if(col == 1) { // Friendly name
				{
					userList.get(row).setFriendlyname((String) value);
					fireTableDataChanged();
					serv.modifyUser(userList.get(row).getId(), MAUserModifyRequest.CHANGE_FRIENDLYNAME, value);
				}
			}
		}
		
		@Override
		public int getRowCount() {
			return userList.size();
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
		@Override
		public String getColumnName(int col) {
			return columnNames[col];
		}
		
		public boolean isCellEditable(int row, int col) {
			return false;
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int col) {
			switch(col) {
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			case 3:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = new String[] {"Username", "Friendly name", "Pennies", "Bottles"};
	};
	
	protected LinkedList<User> userList = new LinkedList<User>();
	
	/**
	 * Updates the list of users with <code>users</code>
	 * @param users New userlist
	 */
	public synchronized void updateUserList(LinkedList<User> users) {
		userList = users;
		userTableModel.fireTableDataChanged();
		System.out.println("Received list!");
	}
	
	protected MouseAdapter userListRightClickListener = new MouseAdapter() {
		public void mousePressed(MouseEvent e) {
			if(e.isPopupTrigger()) {
				userTableContextMenu.show(e.getComponent(), e.getX(), e.getY());
			}
			else if(e.getButton() == MouseEvent.BUTTON1) {
				userTable.editCellAt(userTable.getSelectedRow(), userTable.getSelectedColumn());
			}
		}
		public void mouseReleased(MouseEvent e) {
			mousePressed(e);
		}
	};
	
	protected ActionListener userListRightClickDeleteListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			User user;
			try {
			user = userList.get(userTable.getSelectedRow());
			} catch(IndexOutOfBoundsException e1) {
				return;
			}
			int n = JOptionPane.showConfirmDialog(parent, "Do you really want delete this user?", "Delete user?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if(n == JOptionPane.YES_OPTION)
				serv.modifyUser(user.getId(), MAUserModifyRequest.DELETE_USER);
		}
	};
	
	protected ActionListener userListRightClickChangePasswordListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			User user;
			try {
			user = userList.get(userTable.getSelectedRow());
			} catch(IndexOutOfBoundsException e1) {
				return;
			}
			
			ChangePasswordDialog passwordDialog = new ChangePasswordDialog(parent, serv.createNewUserHandler, user.getId());
			passwordDialog.setVisible(true);
		}
	};
	
	protected ActionListener userListRightClickChangeFriendlyNameListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			User user;
			try {
			user = userList.get(userTable.getSelectedRow());
			} catch(IndexOutOfBoundsException e1) {
				return;
			}
			
			String newFriendlyName = JOptionPane.showInputDialog(parent, "New Username", "New Username", JOptionPane.OK_CANCEL_OPTION);
			
			serv.modifyUser(user.getId(), MAUserModifyRequest.CHANGE_FRIENDLYNAME, newFriendlyName);
		}
	};
	
	protected ActionListener userListRightClickChangePenniesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			User user;
			try {
			user = userList.get(userTable.getSelectedRow());
			} catch(IndexOutOfBoundsException e1) {
				return;
			}
			
			Integer pennies;
			try {
			pennies = Integer.valueOf(JOptionPane.showInputDialog(parent, "New number of pennies", "Change pennies", JOptionPane.OK_CANCEL_OPTION));
			} catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(parent, "Entered value wasn't a number", "Number error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			serv.modifyUser(user.getId(), MAUserModifyRequest.CHANGE_PENNIES, pennies);
		}
	};
	
	protected ActionListener userListRightClickChangeBottlesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			User user;
			try {
			user = userList.get(userTable.getSelectedRow());
			} catch(IndexOutOfBoundsException e1) {
				return;
			}
			
			Integer bottles;
			try {
			bottles = Integer.valueOf(JOptionPane.showInputDialog(parent, "New number of bottles", "Change bottles", JOptionPane.OK_CANCEL_OPTION));
			} catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(parent, "Entered value wasn't a number", "Number error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			serv.modifyUser(user.getId(), MAUserModifyRequest.CHANGE_BOTTLES, bottles);
		}
	};
	
	public synchronized void setBottleValue(int pennies) {
		bottleValue.setText("" + pennies);
	}
}
