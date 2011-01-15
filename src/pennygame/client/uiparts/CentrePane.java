package pennygame.client.uiparts;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import pennygame.client.PennyFrame;
import pennygame.client.queues.CSConn;
import pennygame.lib.msg.data.OpenQuote;

public class CentrePane extends JPanel {

	private static final long serialVersionUID = -6688129680757955374L;
	
	final PennyFrame parent;
	final CSConn serv;

	public CentrePane(PennyFrame parent, CSConn serv) {
		super();
		this.parent = parent;
		this.serv = serv;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		{ // Open Quote list
			JLabel label = new JLabel("Quotes", SwingConstants.LEFT);
			add(label);
			
			JTable openQTable = new JTable(openQuotesModel);
			
			openQTable.getColumnModel().getColumn(0).setPreferredWidth(100);
			openQTable.getColumnModel().getColumn(1).setPreferredWidth(50);
			openQTable.getColumnModel().getColumn(2).setPreferredWidth(40);
			openQTable.getColumnModel().getColumn(3).setPreferredWidth(10);
			openQTable.getColumnModel().getColumn(4).setPreferredWidth(40);
			openQTable.getColumnModel().getColumn(5).setPreferredWidth(10);
			openQTable.getColumnModel().getColumn(6).setPreferredWidth(50);
			// openQTable.getColumnModel().getColumn(7).setPreferredWidth(50);
			
			openQTable.setAutoCreateRowSorter(true);
			openQTable.setFillsViewportHeight(true);
			openQTable.addMouseListener(openQuotesClickListener);
			
			JScrollPane scrollPane = new JScrollPane(openQTable);
			
			scrollPane.setPreferredSize(new Dimension(500, 400));
			scrollPane.setMinimumSize(new Dimension(500, 400));
			scrollPane.setMaximumSize(new Dimension(500, 400));
			
			scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			// openQTable.setRowHeight(12);
			
			add(scrollPane);
		}
	}
	
	protected AbstractTableModel openQuotesModel = new AbstractTableModel() {
		private static final long serialVersionUID = 7059296232064524813L;

		@Override
		public Object getValueAt(int row, int col) {
			if(row < openQuoteList.size() + getShiftLevel() && row > getShiftLevel()) {
				row -= getShiftLevel();
				switch(col) {
				case 0:
					return openQuoteList.get(row).getFromName();
				case 1:
					return openQuoteList.get(row).getType() == OpenQuote.TYPE_BUY ? "BUYING" : "SELLING";
				case 2:
					return openQuoteList.get(row).getBottles();
				case 3:
					return "@";
				case 4:
					return openQuoteList.get(row).getPennies();
				case 5:
					return "=";
				case 6:
					return openQuoteList.get(row).getValue();
				case 7:
					return "";
					// return openQuoteList.get(row).getTime();
				}
			}
			return "";
		}
		
		@Override
		public int getRowCount() {
			return 30;
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
			case 1:
			case 3:
			case 5:
			case 7:
				return String.class;
			case 2:
			case 4:
			case 6:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = {"User", "Type", "Bottles", "@", "Pennies", "=", "Value", "At"};
		
		private int getShiftLevel() {
			return (openQuoteListTotal - openQuoteList.size()) / 2;
		}
	};
	
	protected MouseAdapter openQuotesClickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
	};
	
	protected LinkedList<OpenQuote> openQuoteList = new LinkedList<OpenQuote>();
	protected int					openQuoteListTotal = 30;
	
	public void updateOpenQuoteList(LinkedList<OpenQuote> list, int total) {
		this.openQuoteList = list;
		openQuoteListTotal = total;
		openQuotesModel.fireTableDataChanged();
	}
}
