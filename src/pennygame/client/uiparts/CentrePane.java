package pennygame.client.uiparts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.LinkedList;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import pennygame.client.PennyFrame;
import pennygame.client.queues.CSConn;
import pennygame.lib.msg.data.OpenQuote;

/**
 * The central pane of the client interface.
 * @author william
 *
 */
public class CentrePane extends JPanel {

	private static final long serialVersionUID = -6688129680757955374L;
	
	final PennyFrame parent;
	final CSConn serv;
	
	JScrollPane scrollPane;
	JTable openQTable;
	
	static final String LABEL_BUYING = "BUYING";
	static final String LABEL_SELLING = "SELLING";
	
	
	public CentrePane(PennyFrame parent, CSConn serv, LinkedList<JComponent> pausingItems) {
		super();
		this.parent = parent;
		this.serv = serv;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		{ // Open Quote list
			JLabel label = new JLabel("Quotes", SwingConstants.LEFT);
			label.setFont(label.getFont().deriveFont(Font.BOLD));
			add(label);
			
			openQTable = new JTable(openQuotesModel);
			
			openQTable.getColumnModel().getColumn(0).setPreferredWidth(90);
			openQTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			openQTable.getColumnModel().getColumn(2).setPreferredWidth(55);
			openQTable.getColumnModel().getColumn(3).setPreferredWidth(8);
			openQTable.getColumnModel().getColumn(4).setPreferredWidth(55);
			openQTable.getColumnModel().getColumn(5).setPreferredWidth(8);
			openQTable.getColumnModel().getColumn(6).setPreferredWidth(60);
			openQTable.getColumnModel().getColumn(7).setPreferredWidth(80);
			
			openQTable.getColumnModel().getColumn(1).setCellRenderer(openQuotesRenderer);
			
			openQTable.getTableHeader().setReorderingAllowed(false);
			openQTable.getTableHeader().setResizingAllowed(false);
			
			openQTable.setFont(Font.decode("Sans-9"));
			openQTable.setRowHeight(12);
			openQTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			openQTable.setFillsViewportHeight(true);
			openQTable.addMouseListener(openQuotesClickListener);
			
			openQTable.setMinimumSize(openQTable.getPreferredSize());
			
			scrollPane = new JScrollPane(openQTable);
			
			pausingItems.add(openQTable);
			
			// scrollPane.setPreferredSize(new Dimension(500, 400));
			// scrollPane.setMinimumSize(new Dimension(500, 400));
			// scrollPane.setMaximumSize(new Dimension(500, 400));
			Dimension spSize = scrollPane.getPreferredSize();
			spSize.height += 20;
			spSize.width = 416;
			scrollPane.setMinimumSize(spSize);
			scrollPane.setMaximumSize(new Dimension(spSize.width, 600));
			
			// scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			// openQTable.setRowHeight(12);
			
			add(scrollPane);
		}
	}
	
	protected AbstractTableModel openQuotesModel = new AbstractTableModel() {
		private static final long serialVersionUID = 7059296232064524813L;

		@Override
		public Object getValueAt(int row, int col) {
			if(row < openQuoteList.size() + getShiftLevel() && row >= getShiftLevel()) {
				row -= getShiftLevel();
				switch(col) {
				case 0:
					return openQuoteList.get(row).getFromName();
				case 1:
					return openQuoteList.get(row).getType() == OpenQuote.TYPE_BUY ? LABEL_BUYING : LABEL_SELLING;
				case 2:
					return Math.abs(openQuoteList.get(row).getBottles());
				case 3:
					return "@";
				case 4:
					return openQuoteList.get(row).getPennies();
				case 5:
					return "=";
				case 6:
					return Math.abs(openQuoteList.get(row).getValue());
				case 7:
					DateFormat df = DateFormat.getTimeInstance();
					return df.format(openQuoteList.get(row).getTime());
					// return openQuoteList.get(row).getTime();
				}
			}
			return "";
		}
		
		@Override
		public int getRowCount() {
			return openQuoteListTotal;
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
		
		// private int getShiftLevel() {
			// return (openQuoteListTotal - openQuoteList.size()) / 2;
		// }
	};
	
	/**
	 * Gets the amount to shift the list down the table to centre it in the table.
	 * @return
	 */
	private int getShiftLevel() {
		return (openQuoteListTotal - openQuoteList.size()) / 2;
	}
	
	protected DefaultTableCellRenderer openQuotesRenderer = new DefaultTableCellRenderer() {

		private static final long serialVersionUID = 3264586781621089278L;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			String s = table.getModel().getValueAt(row, column).toString();
			
			if(s.equalsIgnoreCase(LABEL_BUYING)) {
				c.setBackground(new Color(255, 200, 200));
			}
			else if(s.equalsIgnoreCase(LABEL_SELLING)) {
				c.setBackground(new Color(200, 255, 200));
			} else {
				c.setBackground(Color.WHITE);
			}
			
			return c;
		}
	};
		
	protected MouseAdapter openQuotesClickListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == MouseEvent.BUTTON1) {
				int listRow = openQTable.getSelectedRow() - getShiftLevel();
				if(listRow >= openQuoteList.size() || listRow < 0) return;
				
				System.out.println("Row " + listRow + " selected, processing.");
				
				int tradeId = openQuoteList.get(listRow).getId();
				serv.acceptQuote(tradeId);
			}
		}
	};
	
	protected LinkedList<OpenQuote> openQuoteList = new LinkedList<OpenQuote>();
	protected int					openQuoteListTotal = 30;
	
	public void updateOpenQuoteList(LinkedList<OpenQuote> list, int total) {
		this.openQuoteList = list;
		openQuoteListTotal = total;
		openQuotesModel.fireTableDataChanged();
		
		Dimension spSize = scrollPane.getPreferredSize();
		spSize.height += 20;
		scrollPane.setMinimumSize(spSize);
		scrollPane.setMaximumSize(new Dimension(spSize.width, 600));
		openQTable.setMinimumSize(openQTable.getPreferredSize());
	}
}
