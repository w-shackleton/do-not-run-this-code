package pennygame.client.uiparts;

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
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import pennygame.client.PennyFrame;
import pennygame.client.queues.CSConn;
import pennygame.lib.msg.MPutQuote;
import pennygame.lib.msg.data.User;

public class RightPane extends JPanel {

	private static final long serialVersionUID = 7005036371205867048L;
	protected static final String FRIENDLYNAME_TEXT = "My name: ";

	final PennyFrame parent;
	final CSConn serv;

	final JTextField buyQPennies, buyQBottles;
	final JTextField sellQPennies, sellQBottles;
	
	public RightPane(PennyFrame parent, CSConn serv, final User userInfo, LinkedList<JComponent> pausingItems) {
		super();
		this.parent = parent;
		this.serv = serv;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		Box box = Box.createVerticalBox();
		add(box);

		setBorder(new EmptyBorder(10, 0, 10, 0));
		
		{ // Title
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));
			
			final JLabel myName = new JLabel(FRIENDLYNAME_TEXT + userInfo.getFriendlyname());
			myName.setFont(myName.getFont().deriveFont(14f).deriveFont(Font.BOLD));
			pan.add(myName);
			
			pan.add(Box.createRigidArea(new Dimension(10, 1)));
			
			JButton changeMyName = new JButton("Change");
			changeMyName.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String newName = JOptionPane.showInputDialog(RightPane.this.parent, "Please enter a new name", userInfo.getFriendlyname());
					if(newName.length() > 3) {
						RightPane.this.serv.changeMyName(newName); // Change my new name, this will trigger the quote list to refresh
						myName.setText(FRIENDLYNAME_TEXT + newName);
						userInfo.setFriendlyname(newName);
					}
				}
			});
			pan.add(changeMyName);
			pausingItems.add(changeMyName);
			
			box.add(pan);
			box.add(Box.createRigidArea(new Dimension(1, 10)));
		}

		{ // Sell box
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));

			JLabel label = new JLabel("Sell ");
			pan.add(label);

			sellQBottles = new JTextField(4);
			sellQBottles.setMaximumSize(sellQBottles.getPreferredSize());
			sellQBottles.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						sellQPennies.requestFocusInWindow();
						sellQPennies.selectAll();
					}
				}
			});
			pan.add(sellQBottles);
			pausingItems.add(sellQBottles);

			label = new JLabel(" bottles at ");
			pan.add(label);

			sellQPennies = new JTextField(4);
			sellQPennies.setMaximumSize(sellQPennies.getPreferredSize());
			sellQPennies.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						sellQBottles.requestFocusInWindow();
						onSell.actionPerformed(null);
						sellQBottles.selectAll();
					}
				}
			});
			pan.add(sellQPennies);
			pausingItems.add(sellQPennies);

			label = new JLabel(" ppb.");
			pan.add(label);

			box.add(pan);
		}

		{ // Sell button
			JButton sellQButton = new JButton("Offer to SELL");
			sellQButton.addActionListener(onSell);
			box.add(sellQButton);
			pausingItems.add(sellQButton);
		}

		add(Box.createVerticalStrut(30));

		{ // Buy box
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));

			JLabel label = new JLabel("Buy ");
			pan.add(label);

			buyQBottles = new JTextField(4);
			buyQBottles.setMaximumSize(buyQBottles.getPreferredSize());
			buyQBottles.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						buyQPennies.requestFocusInWindow();
						buyQPennies.selectAll();
					}
				}
			});
			pan.add(buyQBottles);
			pausingItems.add(buyQBottles);

			label = new JLabel(" bottles at ");
			pan.add(label);

			buyQPennies = new JTextField(4);
			buyQPennies.setMaximumSize(buyQPennies.getPreferredSize());
			buyQPennies.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						buyQBottles.requestFocusInWindow();
						onBuy.actionPerformed(null);
						buyQBottles.selectAll();
					}
				}
			});
			pan.add(buyQPennies);
			pausingItems.add(buyQPennies);

			label = new JLabel(" ppb.");
			pan.add(label);

			box.add(pan);
		}

		{ // Buy button
			JButton buyQButton = new JButton("Offer to BUY");
			buyQButton.addActionListener(onBuy);
			box.add(buyQButton);
			pausingItems.add(buyQButton);
		}
		
		box.add(Box.createVerticalGlue());
		
		setPreferredSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		setMaximumSize(getPreferredSize());
	}

	protected ActionListener onSell = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			try {
				serv.putQuote(MPutQuote.TYPE_SELL, Integer.parseInt(sellQPennies.getText()),
						Integer.parseInt(sellQBottles.getText()));
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(parent, "Incorrect number", "Please enter a proper number",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};

	protected ActionListener onBuy = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				serv.putQuote(MPutQuote.TYPE_BUY, Integer.parseInt(buyQPennies.getText()),
						Integer.parseInt(buyQBottles.getText()));
			} catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(parent, "Incorrect number", "Please enter a proper number",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	};
}
