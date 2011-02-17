package pennygame.client.uiparts;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.LinkedList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import pennygame.client.PennyFrame;
import pennygame.client.queues.CSConn;
import pennygame.lib.clientutils.TimeoutDialog;
import pennygame.lib.msg.MMyInfo;
import pennygame.lib.msg.MMyQuotesList;
import pennygame.lib.msg.data.ClosedQuote;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.lib.msg.data.PB;

public class LeftPane extends JPanel {

	private static final long serialVersionUID = -4174173086819726643L;
	
	final PennyFrame parent;
	final CSConn serv;
	
	final JTable currentInfoTable, predictedInfoTable;
	final JTable pastTradesTable, currentQuotesTable;
	
	final JTextField worthGuess;
	final JButton worthGuessSubmit;
	
	final JScrollPane pastTradesScrollPane; 
	
	static final String LABEL_BUYING = "B";
	static final String LABEL_SELLING = "S";

	public LeftPane(PennyFrame parent, CSConn serv, LinkedList<JComponent> pausingItems) {
		super();
		this.parent = parent;
		this.serv = serv;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// setBorder(new EmptyBorder(10, 10, 10, 0));
		
		{ // Worth guess
			Box hBox = Box.createHorizontalBox();
			hBox.add(Box.createGlue());
			JLabel label = new JLabel("My bottle price estimate: ");
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
		
		{ // Past trades (leading to current worth)
			pastTradesTable = new JTable(myPastTradesModel);
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
			// pastTradesTable.addMouseListener(openQuotesClickListener);
			
			pastTradesTable.setMinimumSize(pastTradesTable.getPreferredSize());
			
			pastTradesScrollPane = new JScrollPane(pastTradesTable);
			pastTradesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			pastTradesScrollPane.setWheelScrollingEnabled(true);
			
			pausingItems.add(pastTradesScrollPane);
			pausingItems.add(pastTradesTable);
			
			Dimension spSize = new Dimension(400, 100);
			pastTradesScrollPane.setMinimumSize(spSize);
			pastTradesScrollPane.setPreferredSize(spSize);
			
			spSize = new Dimension(spSize);
			spSize.height = pastTradesScrollPane.getMaximumSize().height;
			pastTradesScrollPane.setMaximumSize(spSize);
			
			// scrollPane.setBorder(new EmptyBorder(0, 10, 0, 10));
			
			// openQTable.setRowHeight(12);
			
			add(pastTradesScrollPane);
		}
		
		{ // Current worth table
			currentInfoTable = new JTable(myCurrentInfoModel);
			currentInfoTable.getColumnModel().getColumn(0).setPreferredWidth(60);
			currentInfoTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			currentInfoTable.getColumnModel().getColumn(2).setPreferredWidth(30);
			currentInfoTable.getColumnModel().getColumn(3).setPreferredWidth(40);
			currentInfoTable.getColumnModel().getColumn(4).setPreferredWidth(40);
			currentInfoTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			currentInfoTable.getColumnModel().getColumn(6).setPreferredWidth(60);
			
			currentInfoTable.setFont(Font.decode("Sans-9"));
			currentInfoTable.setRowHeight(12);
			currentInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			currentInfoTable.getTableHeader().setReorderingAllowed(false);
			currentInfoTable.getTableHeader().setResizingAllowed(false);
			
			// currentInfoTable.setFillsViewportHeight(true);
			
			currentInfoTable.setMinimumSize(currentInfoTable.getPreferredSize());
			
			JScrollPane scrollPane = new JScrollPane(currentInfoTable);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setWheelScrollingEnabled(true);
			
			pausingItems.add(scrollPane);
			pausingItems.add(currentInfoTable);
			
			Dimension spSize = new Dimension(400, 40);
			scrollPane.setMinimumSize(spSize);
			scrollPane.setPreferredSize(spSize);
			scrollPane.setMaximumSize(spSize);
			
			add(scrollPane);
			
			pausingItems.add(scrollPane);
		}
		
		{ // My Current trades (leading to predicted worth)
			currentQuotesTable = new JTable(myCurrentQuotesModel);
			currentQuotesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
			currentQuotesTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			currentQuotesTable.getColumnModel().getColumn(2).setPreferredWidth(30);
			currentQuotesTable.getColumnModel().getColumn(3).setPreferredWidth(40);
			currentQuotesTable.getColumnModel().getColumn(4).setPreferredWidth(40);
			currentQuotesTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			currentQuotesTable.getColumnModel().getColumn(6).setPreferredWidth(60);
			
			currentQuotesTable.getColumnModel().getColumn(2).setCellRenderer(pastTradesRenderer); // We'll use this for now
			
			currentQuotesTable.getTableHeader().setReorderingAllowed(false);
			currentQuotesTable.getTableHeader().setResizingAllowed(false);
			
			currentQuotesTable.setFont(Font.decode("Sans-9"));
			currentQuotesTable.setRowHeight(12);
			currentQuotesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			currentQuotesTable.setFillsViewportHeight(true);
			currentQuotesTable.addMouseListener(currentQuotesClickListener);
			
			Dimension size = currentQuotesTable.getMinimumSize();
			size.height = 200;
			currentQuotesTable.setMaximumSize(size);
			
			JScrollPane scrollPane = new JScrollPane(currentQuotesTable);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			pausingItems.add(scrollPane);
			pausingItems.add(currentQuotesTable);
			
			Dimension spSize = new Dimension(400, 100);
			scrollPane.setMinimumSize(spSize);
			scrollPane.setPreferredSize(spSize);
			
			spSize = new Dimension(spSize);
			spSize.height = scrollPane.getMaximumSize().height;
			scrollPane.setMaximumSize(spSize);
			
			add(scrollPane);
		}
		
		{ // Predicted worth table
			predictedInfoTable = new JTable(myPredictedInfoModel);
			predictedInfoTable.getColumnModel().getColumn(0).setPreferredWidth(60);
			predictedInfoTable.getColumnModel().getColumn(1).setPreferredWidth(60);
			predictedInfoTable.getColumnModel().getColumn(2).setPreferredWidth(30);
			predictedInfoTable.getColumnModel().getColumn(3).setPreferredWidth(40);
			predictedInfoTable.getColumnModel().getColumn(4).setPreferredWidth(40);
			predictedInfoTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			predictedInfoTable.getColumnModel().getColumn(6).setPreferredWidth(60);
			
			predictedInfoTable.setFont(Font.decode("Sans-9"));
			predictedInfoTable.setRowHeight(12);
			predictedInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			
			predictedInfoTable.getTableHeader().setReorderingAllowed(false);
			predictedInfoTable.getTableHeader().setResizingAllowed(false);
			
			// predictedInfoTable.setFillsViewportHeight(true);
			
			predictedInfoTable.setMinimumSize(predictedInfoTable.getPreferredSize());
			
			JScrollPane scrollPane = new JScrollPane(predictedInfoTable);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane.setWheelScrollingEnabled(true);
			
			pausingItems.add(scrollPane);
			pausingItems.add(predictedInfoTable);
			
			Dimension spSize = new Dimension(400, 80);
			scrollPane.setMinimumSize(spSize);
			scrollPane.setPreferredSize(spSize);
			scrollPane.setMaximumSize(spSize);
			
			add(scrollPane);
			
			pausingItems.add(scrollPane);
		}
	}
	
	protected AbstractTableModel myCurrentInfoModel = new AbstractTableModel() {
		private static final long serialVersionUID = 7059296232064524813L;

		@Override
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return "Current";
			case 1:
				return "wealth:";
			case 2:
				return "";
			case 3:
				return Math.abs(myCurrentInfo.getBottles());
			case 4:
				return Math.abs(myCurrentInfo.getPennies());
			case 5:
				return Math.abs(myCurrentInfo.getTotal());
			case 6:
				return "";
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
			case 3:
			case 4:
			case 5:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = {"Current", "Wealth", "", "Bottles", "Pennies", "Est. Total", ""};
	};
	
	protected AbstractTableModel myPastTradesModel = new AbstractTableModel() {

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
	
	protected AbstractTableModel myCurrentQuotesModel = new AbstractTableModel() {

		private static final long serialVersionUID = 2939739187727577540L;

		@Override
		public Object getValueAt(int row, int col) {
			switch(col) {
			case 0:
				return "";
			case 1:
				return "";
			case 2:
				return myCurrentQuotes.get(row).getType() == OpenQuote.TYPE_BUY ? LABEL_BUYING : LABEL_SELLING;
			case 3:
				return Math.abs(myCurrentQuotes.get(row).getBottles());
			case 4:
				return Math.abs(myCurrentQuotes.get(row).getPennies());
			case 5:
				return Math.abs(myCurrentQuotes.get(row).getValue());
			case 6:
				DateFormat df = DateFormat.getTimeInstance();
				return df.format(myCurrentQuotes.get(row).getTime());
			default:
				return "";
			}
		}
		
		@Override
		public int getRowCount() {
			return myCurrentQuotes.size();
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
		
		private String[] columnNames = {"Current", "Quotes", "Type", "Bottles", "PPB", "Total", "Time"};
	};
	
	protected AbstractTableModel myPredictedInfoModel = new AbstractTableModel() {
		private static final long serialVersionUID = 7059296232064524813L;

		@Override
		public Object getValueAt(int row, int col) {
			switch(row) {
			case 0: // Predicted
				switch(col) {
				case 0:
					return "Predicted";
				case 1:
					return "Wealth:";
				case 2:
					return "";
				case 3:
					return Math.abs(myPredictedInfo.getBottles());
				case 4:
					return Math.abs(myPredictedInfo.getPennies());
				case 5:
					return Math.abs(myPredictedInfo.getTotal());
				case 6:
					return "";
				default:
					return "";
				}
			case 1: // Predicted 1 (Most bottles)
				switch(col) {
				case 0:
					return "(Most";
				case 1:
					return "bottles)";
				case 2:
					return "...";
				case 3:
					return Math.abs(myPredicted1Info.getBottles());
				case 4:
					return Math.abs(myPredicted1Info.getPennies());
				case 5:
					return Math.abs(myPredicted1Info.getTotal());
				case 6:
					return "";
				default:
					return "";
				}
			case 2: // Predicted 2 (Most pennies)
				switch(col) {
				case 0:
					return "(Most";
				case 1:
					return "pennies)";
				case 2:
					return "...";
				case 3:
					return Math.abs(myPredicted2Info.getBottles());
				case 4:
					return Math.abs(myPredicted2Info.getPennies());
				case 5:
					return Math.abs(myPredicted2Info.getTotal());
				case 6:
					return "";
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
			case 3:
			case 4:
			case 5:
				return Integer.class;
			default:
				return String.class;
			}
		}
		
		private String[] columnNames = {"Predicted", "Wealth", "", "Bottles", "Pennies", "Est. Total", ""};
	};
	
	
	protected PB myCurrentInfo = new PB(0, 0, 0), myPredictedInfo = new PB(0, 0, 0), myPredicted1Info = new PB(0, 0, 0), myPredicted2Info = new PB(0, 0, 0);
	
	
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
	
	public void updateUserInfo(MMyInfo myInfo) {
		myCurrentInfo = myInfo.getCurrent();
		myPredictedInfo = myInfo.getPotential();
		myPredicted1Info = myInfo.getPotential1();
		myPredicted2Info = myInfo.getPotential2();
		
		myCurrentInfoModel.fireTableDataChanged();
		myPredictedInfoModel.fireTableDataChanged();
		
		worthGuess.setText(String.valueOf(myInfo.getEstimatedWorth()));
	}
	
	protected LinkedList<ClosedQuote> pastTrades = new LinkedList<ClosedQuote>();
	protected LinkedList<OpenQuote> myCurrentQuotes = new LinkedList<OpenQuote>();
	
	public void updateUserQuotes(MMyQuotesList quotes) {
		pastTrades = quotes.getPastQuotes();
		myCurrentQuotes = quotes.getCurrentQuotes();
		myPastTradesModel.fireTableDataChanged();
		myCurrentQuotesModel.fireTableDataChanged();
		SwingUtilities.invokeLater(new Runnable() {
			
			public void run() {
				pastTradesScrollPane.getVerticalScrollBar().setValue(
					pastTradesScrollPane.getVerticalScrollBar().getMaximum());
			}
		});
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
	
	protected MouseListener currentQuotesClickListener = new MouseListener() {
		@Override
		public void mouseClicked(MouseEvent e) {
			int row = currentQuotesTable.getSelectedRow();
			OpenQuote quote = myCurrentQuotes.get(row);
			if(quote == null) return;
			
			TimeoutDialog t = new TimeoutDialog(
					parent,
					"<html><p>Cancel quote with <b>" + Math.abs(quote.getBottles()) + "</b> bottles @ <b>" +
					quote.getPennies() + "</b> ppb = <b>" + Math.abs(quote.getValue()) + "</b> pennies?</p></html>", "Cancel quote?", 20);
			t.setVisible(true);
			
			if(t.isOk()) {
				serv.cancelQuote(quote.getId());
			}
		}

		@Override public void mouseEntered(MouseEvent e) { }

		@Override public void mouseExited(MouseEvent e) { }

		@Override public void mousePressed(MouseEvent e) { }

		@Override public void mouseReleased(MouseEvent e) { }
	};
}
