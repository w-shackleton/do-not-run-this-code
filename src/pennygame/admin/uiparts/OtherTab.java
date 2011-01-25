package pennygame.admin.uiparts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import pennygame.admin.AdminFrame;
import pennygame.admin.queues.AdminSConn;

public class OtherTab extends JPanel {

	private static final long serialVersionUID = -4225767893441384749L;
	
	protected final AdminFrame parent;
	
	protected final AdminSConn serv;

	public OtherTab(AdminFrame parent, AdminSConn serv) {
		super(); 
		
		this.serv = serv;
		this.parent = parent;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JButton resetGame = new JButton("RESET GAME");
		resetGame.setBorder(new EmptyBorder(10, 10, 10, 10));
		resetGame.addActionListener(resetGameListener);
		add(resetGame);
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
}
