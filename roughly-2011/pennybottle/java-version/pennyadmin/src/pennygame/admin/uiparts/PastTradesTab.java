package pennygame.admin.uiparts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import pennygame.admin.AdminFrame;
import pennygame.admin.queues.AdminSConn;
import pennygame.lib.msg.data.ClosedQuote;

public class PastTradesTab extends JPanel {

	private static final long serialVersionUID = 769156959656301574L;

	protected final AdminFrame parent;
	
	protected final AdminSConn serv;
	
	private final JTable pastTradesTable;
	final JScrollPane pastTradesScrollPane; 
	
	static final String LABEL_BUYING = "B";
	static final String LABEL_SELLING = "S";

	public PastTradesTab(AdminFrame parent, final AdminSConn serv) {
		super(); 
		
		this.serv = serv;
		this.parent = parent;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Box buttonBox = Box.createHorizontalBox();
		
		JButton button = new JButton("Refresh");
		buttonBox.add(button);
		buttonBox.add(Box.createHorizontalGlue());
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serv.refreshPastTrades();
			}
		});
		
		add(buttonBox);
		
		{ // Past trades (leading to current worth)
			pastTradesTable = new JTable(pastTradesModel);
			pastTradesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
			pastTradesTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			pastTradesTable.getColumnModel().getColumn(2).setPreferredWidth(30);
			pastTradesTable.getColumnModel().getColumn(3).setPreferredWidth(40);
			pastTradesTable.getColumnModel().getColumn(4).setPreferredWidth(40);
			pastTradesTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			pastTradesTable.getColumnModel().getColumn(6).setPreferredWidth(60);
			
			pastTradesTable.getColumnModel().getColumn(2).setCellRenderer(pastTradesRenderer);
			
			pastTradesTable.getTableHeader().setReorderingAllowed(false);
			pastTradesTable.getTableHeader().setResizingAllowed(false);
			
			pastTradesTable.setFont(Font.decode("Sans-9"));
			pastTradesTable.setRowHeight(12);
			pastTradesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			pastTradesTable.setFillsViewportHeight(true);
			pastTradesTable.addMouseListener(pastTradesClickListener);
			
			pastTradesTable.setMinimumSize(pastTradesTable.getPreferredSize());
			
			pastTradesScrollPane = new JScrollPane(pastTradesTable);
			pastTradesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			pastTradesScrollPane.setWheelScrollingEnabled(true);
			
			// Dimension spSize = new Dimension(400, 100);
			// pastTradesScrollPane.setMinimumSize(spSize);
			// pastTradesScrollPane.setPreferredSize(spSize);
			// 
			// spSize = new Dimension(spSize);
			// spSize.height = pastTradesScrollPane.getMaximumSize().height;
			// pastTradesScrollPane.setMaximumSize(spSize);
			
			// scrollPane.setBorder(new EmptyBorder(0, 10, 0, 10));
			
			// openQTable.setRowHeight(12);
			
			add(pastTradesScrollPane);
		}
		
	}
	
	protected AbstractTableModel pastTradesModel = new AbstractTableModel() {

		private static final long serialVersionUID = 487825269459522891L;

		@Override
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return pastTrades.get(row).getFromName();
			case 1:
				return pastTrades.get(row).getToName();
			case 2:
				return pastTrades.get(row).getType() == ClosedQuote.TYPE_BUY ? LABEL_BUYING : LABEL_SELLING;
			case 3:
				return Math.abs(pastTrades.get(row).getBottles());
			case 4:
				return Math.abs(pastTrades.get(row).getPennies());
			case 5:
				return Math.abs(pastTrades.get(row).getValue());
			case 6:
				DateFormat df = DateFormat.getTimeInstance();
				return df.format(pastTrades.get(row).getTime());
			default:
				return "";
			}
		}
		
		@Override
		public int getRowCount() {
			return pastTrades.size();
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
			case 2:
			case 6:
				return String.class;
			case 3:
			case 4:
			case 5:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = {"From", "To", "Type", "Bottles", "PPB", "Total", "Time"};
	};
	
	protected DefaultTableCellRenderer pastTradesRenderer = new DefaultTableCellRenderer() {

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
	
	protected MouseListener pastTradesClickListener = new MouseListener() {
		@Override public void mouseReleased(MouseEvent e) { }
		@Override public void mousePressed(MouseEvent e) { }
		@Override public void mouseExited(MouseEvent e) { }
		@Override public void mouseEntered(MouseEvent e) { }
		
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = pastTradesTable.getSelectedRow();
			ClosedQuote quote = pastTrades.get(row);
			if(quote == null) return;
			
			if(JOptionPane.showConfirmDialog(parent, "<html><p>Undo quote with <b>" + Math.abs(quote.getBottles()) + "</b> bottles @ <b>" +
					quote.getPennies() + "</b> ppb = <b>" + Math.abs(quote.getValue()) + "</b> pennies,</p>" +
							"<p>From <b>" + quote.getFromName() + "</b> to <b>" + quote.getToName() + "</b>?</p></html>", "Confirm undo?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				serv.undoTrade(quote.getId());
			}
		}
	};
	
	protected LinkedList<ClosedQuote> pastTrades = new LinkedList<ClosedQuote>();
	
	public void setPastTrades(LinkedList<ClosedQuote> t) {
		pastTrades = t;
		pastTradesModel.fireTableDataChanged();
	}
}
