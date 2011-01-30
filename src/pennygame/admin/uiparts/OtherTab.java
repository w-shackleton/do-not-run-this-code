package pennygame.admin.uiparts;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import pennygame.admin.AdminFrame;
import pennygame.admin.queues.AdminSConn;

public class OtherTab extends JPanel {

	private static final long serialVersionUID = -4225767893441384749L;
	
	protected final AdminFrame parent;
	
	protected final AdminSConn serv;
	
	protected final JTextField graphMinutes;

	public OtherTab(AdminFrame parent, AdminSConn serv) {
		super(); 
		
		this.serv = serv;
		this.parent = parent;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Box resetBox = Box.createHorizontalBox();
		JButton resetGame = new JButton("RESET GAME");
		resetGame.setBorder(new EmptyBorder(10, 10, 10, 10));
		resetGame.addActionListener(resetGameListener);
		resetBox.add(resetGame);
		resetBox.add(Box.createHorizontalGlue());
		add(resetBox);
		
		add(Box.createRigidArea(new Dimension(10, 10)));
		
		{ // Graph data changer
			Box box = Box.createHorizontalBox();
			
			JLabel label = new JLabel("Projector graph minutes to display: ");
			
			box.add(label);
			
			graphMinutes = new JTextField(4);
			graphMinutes.setPreferredSize(graphMinutes.getMinimumSize());
			graphMinutes.setMaximumSize(graphMinutes.getMinimumSize());
			box.add(graphMinutes);
			
			JButton button = new JButton("Update");
			button.addActionListener(graphMinutesSetListener);
			box.add(button);
			
			box.add(Box.createHorizontalGlue());
			
			add(box);
		}
	}
	
	protected ActionListener resetGameListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if(JOptionPane.showConfirmDialog(parent, "Are you sure you want to reset the game?", "Reset game?", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
				if(JOptionPane.showConfirmDialog(parent, "Are you really sure you want to reset the game?", "Reset game??", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
					if(JOptionPane.showConfirmDialog(parent, "Are you really really sure you want to reset the game?", "Reset game???", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
						serv.resetGame();
					}
				}
			}
		}
	};
	
	public void setGraphLength(final int minutes) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				graphMinutes.setText("" + minutes);
			}
		});
	}
	
	protected ActionListener graphMinutesSetListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			Integer minutes;
			try {
			minutes = Integer.valueOf(graphMinutes.getText());
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(parent, "Please enter a valid number", "Number error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			serv.setGraphLength(minutes);
		}
	};
}
