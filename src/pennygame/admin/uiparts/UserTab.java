package pennygame.admin.uiparts;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
	
	protected JScrollPane scrollPane;
	protected JTable userTable;
	
	protected JPopupMenu userTableContextMenu;

	public UserTab(AdminFrame parent, AdminSConn serv) {
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
		
		// Context menu
		userTableContextMenu = new JPopupMenu();
		userTable.addMouseListener(userListRightClickListener);
		
		JMenuItem deleteUser = new JMenuItem("Delete User");
		deleteUser.addActionListener(userListRightClickDeleteListener);
		userTableContextMenu.add(deleteUser);
		
		JMenuItem changePassword = new JMenuItem("Change Password");
		changePassword.addActionListener(userListRightClickChangePasswordListener);
		userTableContextMenu.add(changePassword);
		
		scrollPane = new JScrollPane(userTable);
		scrollPane.setPreferredSize(new Dimension(380, 300));
		userTable.setFillsViewportHeight(true);
		scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		add(scrollPane);
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
			System.out.println("Change!!");
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
}
