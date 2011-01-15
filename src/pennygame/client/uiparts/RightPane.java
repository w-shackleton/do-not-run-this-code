package pennygame.client.uiparts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import pennygame.client.PennyFrame;
import pennygame.client.queues.CSConn;
import pennygame.lib.msg.MPutQuote;

public class RightPane extends JPanel {

	private static final long serialVersionUID = 7005036371205867048L;

	final PennyFrame parent;
	final CSConn serv;

	final JTextField buyQPennies, buyQBottles;
	final JTextField sellQPennies, sellQBottles;

	public RightPane(PennyFrame parent, CSConn serv) {
		super();
		this.parent = parent;
		this.serv = serv;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		setBorder(new EmptyBorder(10, 10, 10, 10));

		{ // Sell box
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));

			JLabel label = new JLabel("Sell ");
			pan.add(label);

			sellQBottles = new JTextField(5);
			sellQBottles.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
						sellQPennies.requestFocusInWindow();
				}
			});
			pan.add(sellQBottles);

			label = new JLabel(" bottles at ");
			pan.add(label);

			sellQPennies = new JTextField(5);
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
					}
				}
			});
			pan.add(sellQPennies);

			label = new JLabel(" pence per bottle");
			pan.add(label);

			add(pan);
		}

		{ // Sell button
			JButton sellQButton = new JButton("Offer to SELL");
			sellQButton.addActionListener(onSell);
			add(sellQButton);
		}

		add(Box.createVerticalStrut(30));

		{ // Buy box
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.X_AXIS));

			JLabel label = new JLabel("Buy ");
			pan.add(label);

			buyQBottles = new JTextField(5);
			buyQBottles.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER)
						buyQPennies.requestFocusInWindow();
				}
			});
			pan.add(buyQBottles);

			label = new JLabel(" bottles at ");
			pan.add(label);

			buyQPennies = new JTextField(5);
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
					}
				}
			});
			pan.add(buyQPennies);

			label = new JLabel(" pence per bottle");
			pan.add(label);

			add(pan);
		}

		{ // Buy button
			JButton buyQButton = new JButton("Offer to BUY");
			buyQButton.addActionListener(onBuy);
			add(buyQButton);
		}
		add(Box.createVerticalGlue());
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
