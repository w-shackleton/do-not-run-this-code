package pennygame.client.uiparts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

import pennygame.client.PennyFrame;
import pennygame.client.queues.CSConn;
import pennygame.lib.msg.MMyInfo;
import pennygame.lib.msg.data.PB;

public class LeftPane extends JPanel {

	private static final long serialVersionUID = -4174173086819726643L;
	
	final PennyFrame parent;
	final CSConn serv;
	
	final JTable currentInfoTable, predictedInfoTable;
	
	final JTextField worthGuess;
	final JButton worthGuessSubmit;

	public LeftPane(PennyFrame parent, CSConn serv, LinkedList<JComponent> pausingItems) {
		super();
		this.parent = parent;
		this.serv = serv;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(new EmptyBorder(10, 10, 10, 0));
		
		{ // Worth guess
			Box hBox = Box.createHorizontalBox();
			hBox.add(Box.createGlue());
			JLabel label = new JLabel("My worth estimate: ");
			hBox.add(label);
			
			worthGuess = new JTextField("1", 3);
			worthGuess.setMaximumSize(worthGuess.getPreferredSize());
			worthGuess.addKeyListener(new KeyListener() {
				@Override public void keyTyped(KeyEvent e) { }
				@Override public void keyReleased(KeyEvent e) { }
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						worthGuessSubmitClicked.actionPerformed(null); // Submit on enter
					}
				}
			});
			hBox.add(worthGuess);
			
			worthGuessSubmit = new JButton("Update");
			worthGuessSubmit.addActionListener(worthGuessSubmitClicked);
			hBox.add(worthGuessSubmit);
			
			hBox.setMaximumSize(hBox.getPreferredSize());
			
			add(hBox);
		}
		
		{ // Current worth table
			currentInfoTable = new JTable(myCurrentInfoModel);
			currentInfoTable.getColumnModel().getColumn(0).setPreferredWidth(120);
			currentInfoTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			currentInfoTable.getColumnModel().getColumn(2).setPreferredWidth(60);
			currentInfoTable.getColumnModel().getColumn(3).setPreferredWidth(60);
			
			currentInfoTable.setFont(Font.decode("Sans-9"));
			currentInfoTable.setRowHeight(12);
			currentInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			currentInfoTable.getTableHeader().setReorderingAllowed(false);
			currentInfoTable.getTableHeader().setResizingAllowed(false);
			
			// currentInfoTable.setFillsViewportHeight(true);
			
			currentInfoTable.setMinimumSize(currentInfoTable.getPreferredSize());
			
			Dimension spSize = currentInfoTable.getPreferredSize();
			// spSize.height += 20;
			spSize.width = 290;
			currentInfoTable.setMinimumSize(spSize);
			currentInfoTable.setMaximumSize(spSize);
			
			Dimension hSize = currentInfoTable.getTableHeader().getMinimumSize();
			hSize.width = 290;
			JTableHeader h = currentInfoTable.getTableHeader();
			h.setMaximumSize(hSize);
			
			add(h);
			add(currentInfoTable);
			
			pausingItems.add(h);
			pausingItems.add(currentInfoTable);
		}
		
		{ // Predicted worth table
			predictedInfoTable = new JTable(myPredictedInfoModel);
			predictedInfoTable.getColumnModel().getColumn(0).setPreferredWidth(120);
			predictedInfoTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			predictedInfoTable.getColumnModel().getColumn(2).setPreferredWidth(60);
			predictedInfoTable.getColumnModel().getColumn(3).setPreferredWidth(60);
			
			predictedInfoTable.setFont(Font.decode("Sans-9"));
			predictedInfoTable.setRowHeight(12);
			predictedInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			predictedInfoTable.getTableHeader().setReorderingAllowed(false);
			predictedInfoTable.getTableHeader().setResizingAllowed(false);
			
			Dimension spSize = predictedInfoTable.getPreferredSize();
			spSize.width = 290;
			predictedInfoTable.setMinimumSize(spSize);
			predictedInfoTable.setMaximumSize(spSize);
			
			Dimension hSize = predictedInfoTable.getTableHeader().getMinimumSize();
			hSize.width = 290;
			JTableHeader h = predictedInfoTable.getTableHeader();
			h.setMaximumSize(hSize);
			
			add(h);
			add(predictedInfoTable);
			
			pausingItems.add(h);
			pausingItems.add(predictedInfoTable);
		}
	}
	
	protected AbstractTableModel myCurrentInfoModel = new AbstractTableModel() {
		private static final long serialVersionUID = 7059296232064524813L;

		@Override
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return "Current worth:";
			case 1:
				return myCurrentInfo.getBottles();
			case 2:
				return myCurrentInfo.getPennies();
			case 3:
				return myCurrentInfo.getTotal();
			default:
				return "";
			}
		}
		
		@Override
		public int getRowCount() {
			return 1;
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
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
			case 2:
			case 3:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = {"", "Bottles", "Pennies", "Est. Total"};
	};
	
	protected AbstractTableModel myPredictedInfoModel = new AbstractTableModel() {
		private static final long serialVersionUID = 7059296232064524813L;

		@Override
		public Object getValueAt(int row, int col) {
			switch(row) {
			case 0: // Predicted
				switch(col) {
				case 0:
					return "Predicted worth:";
				case 1:
					return myPredictedInfo.getBottles();
				case 2:
					return myPredictedInfo.getPennies();
				case 3:
					return myPredictedInfo.getTotal();
				default:
					return "";
				}
			case 1: // Predicted 1 (Most bottles)
				switch(col) {
				case 0:
					return "... (most bottles):";
				case 1:
					return myPredicted1Info.getBottles();
				case 2:
					return myPredicted1Info.getPennies();
				case 3:
					return myPredicted1Info.getTotal();
				default:
					return "";
				}
			case 2: // Predicted 2 (Most pennies)
				switch(col) {
				case 0:
					return "... (most pennies):";
				case 1:
					return myPredicted2Info.getBottles();
				case 2:
					return myPredicted2Info.getPennies();
				case 3:
					return myPredicted2Info.getTotal();
				default:
					return "";
				}
			default:
				return "";
			}
		}
		
		@Override
		public int getRowCount() {
			return 3;
		}
		
		@Override
		public int getColumnCount() {
			return columnNames.length;
		}
		
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
			case 2:
			case 3:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = {"", "Bottles", "Pennies", "Est. Total"};
	};
	
	
	protected PB myCurrentInfo = new PB(0, 0, 0), myPredictedInfo = new PB(0, 0, 0), myPredicted1Info = new PB(0, 0, 0), myPredicted2Info = new PB(0, 0, 0);
	
	
	protected DefaultTableCellRenderer openQuotesRenderer = new DefaultTableCellRenderer() {

		private static final long serialVersionUID = 3264586781621089278L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			String s = table.getModel().getValueAt(row, column).toString();
			
			if(s.equalsIgnoreCase("B")) {
				c.setBackground(new Color(255, 200, 200));
			}
			else if(s.equalsIgnoreCase("S")) {
				c.setBackground(new Color(200, 255, 200));
			} else {
				c.setBackground(Color.WHITE);
			}
			
			return c;
		}
	};
	
	public void updateUserInfo(MMyInfo myInfo) {
		myCurrentInfo = myInfo.getCurrent();
		myPredictedInfo = myInfo.getPotential();
		myPredicted1Info = myInfo.getPotential1();
		myPredicted2Info = myInfo.getPotential2();
		
		myCurrentInfoModel.fireTableDataChanged();
		myPredictedInfoModel.fireTableDataChanged();
		
		worthGuess.setText(String.valueOf(myInfo.getEstimatedWorth()));
	}
	
	protected ActionListener worthGuessSubmitClicked = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int n = 0;
			try {
				n = Integer.valueOf(worthGuess.getText());
			} catch(NumberFormatException e1) {
			}
			
			if(n <= 0) {
				JOptionPane.showMessageDialog(parent, "Not a valid number!", "Number error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			serv.setWorthGuess(n);
		}
	};
}
