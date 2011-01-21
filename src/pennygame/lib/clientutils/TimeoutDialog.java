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

// http://www.coderanch.com/t/341814/GUI/java/JOptionPane-Timeout :D
class CustomDialog extends JDialog implements ActionListener, Runnable {

	private static final long serialVersionUID = 8475338949913343386L;
	
	private JButton yesButton = null;
	private JButton noButton = null;
	private boolean OK = false;
	private Thread thread = null;
	private int seconds = 0;
	private final int max = 30;// max number of seconds

	public CustomDialog(Frame frame, String message, String title) {
		super(frame, title, true);// modal
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		Box hBox = Box.createHorizontalBox();

		yesButton = new JButton("OK");
		yesButton.addActionListener(this);

		noButton = new JButton("Cancel");
		noButton.addActionListener(this);

		JLabel label = new JLabel(message);

		Container cont = getContentPane();
		cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
		cont.add(label);
		hBox.add(Box.createGlue());
		hBox.add(yesButton);
		hBox.add(noButton);
		cont.add(hBox);

		pack();
		thread = new Thread(this);
		thread.start();
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == yesButton)
			OK = true;
		if (e.getSource() == noButton)
			OK = false;
		setVisible(false);
	}

	public void run() {
		while (seconds < max) {
			seconds++;
			setTitle("OK? " + seconds);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException exc) {
			}
			;
		}
		setVisible(false);
	}

	public boolean isOk() {
		return OK;
	}
}