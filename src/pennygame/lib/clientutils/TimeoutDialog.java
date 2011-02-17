package pennygame.lib.clientutils;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

// http://www.coderanch.com/t/341814/GUI/java/JOptionPane-Timeout :D
/**
 * A message dialogue which timeouts after a set time
 */
public class TimeoutDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 8475338949913343386L;
	
	private JButton yesButton = null;
	private JButton noButton = null;
	private boolean OK = false;
	private Thread thread = null;
	private int seconds = 0;
	private final int max;// max number of seconds

	public TimeoutDialog(Frame frame, String message, String title, int timeout) {
		super(frame, title, true);// modal
		max = timeout;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		Box hBox = Box.createHorizontalBox();

		yesButton = new JButton("OK");
		yesButton.addActionListener(this);
		yesButton.setBorder(new EmptyBorder(10, 10, 10, 5));

		noButton = new JButton("Cancel");
		noButton.addActionListener(this);
		noButton.setBorder(new EmptyBorder(10, 5, 10, 10));

		JLabel label = new JLabel(message);
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		label.setMinimumSize(label.getPreferredSize());

		Container cont = getContentPane();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(label);
		hBox.add(Box.createGlue());
		hBox.add(yesButton);
		hBox.add(Box.createHorizontalStrut(10));
		hBox.add(noButton);
		cont.add(hBox);

		pack();
		setLocationRelativeTo(frame);
		thread = new Thread(r);
		thread.start();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesButton)
			OK = true;
		if (e.getSource() == noButton)
			OK = false;
		setVisible(false);
	}

	private Runnable r = new Runnable() {
		public void run() {
			while (seconds < max) {
				seconds++;
				// setTitle("OK? " + seconds);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException exc) {
				}
			}
			setVisible(false);
		}
	};

	public boolean isOk() {
		return OK;
	}
}