package pennygame.client;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pennygame.client.queues.CSConn;
import pennygame.client.uiparts.CentrePane;
import pennygame.client.uiparts.LeftPane;
import pennygame.client.uiparts.RightPane;
import pennygame.lib.GlobalPreferences;
import pennygame.lib.clientutils.TimeoutDialog;
import pennygame.lib.msg.data.OpenQuote;
import pennygame.lib.msg.data.User;

public class PennyFrame extends JFrame implements WindowListener {
	
	private static final long serialVersionUID = -4773005041038467984L;
	
	protected final CSConn serv;
	protected final PennyClient applet;
	
	public RightPane rp;
	public CentrePane cp;
	public LeftPane lp;
	
	protected final User userInfo;
	
	protected final LinkedList<JComponent> pausingItems = new LinkedList<JComponent>();
	
	public PennyFrame(CSConn serv, PennyClient applet, User userInfo) {
		super("Pennybottle game");
		this.serv = serv;
		this.applet = applet;
		this.userInfo = userInfo;
		
		addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		initUI();
		
		serv.setParentFrame(this);
	}
	
	private void initUI() {
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		
		{ // LEFT PANE
			lp = new LeftPane(this, serv, pausingItems);
			container.add(lp, BorderLayout.LINE_START);
		}
		
		{ // RIGHT PANE
			rp = new RightPane(this, serv, userInfo, pausingItems);
			container.add(rp, BorderLayout.LINE_END);
		}
		
		{ // TOP PANE
			
		}
		
		{ // BOTTOM PANE
			
		}
		
		{ // CENTRE PANE
			cp = new CentrePane(this, serv, pausingItems);
			container.add(cp, BorderLayout.CENTER);
		}
		
		add(container);
		pack();
	}

	@Override public void windowActivated(WindowEvent arg0) { } 
	@Override public void windowClosed(WindowEvent arg0) { }

	@Override
	public void windowClosing(WindowEvent arg0) {
		int n = JOptionPane.showConfirmDialog(this, "Do you really want to close?", "Close Pennygame?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(n == JOptionPane.YES_OPTION)
		{
			serv.stop();
			setVisible(false);
			dispose();
			applet.frameFinished();
		}
	}

	@Override public void windowDeactivated(WindowEvent arg0) { } 
	@Override public void windowDeiconified(WindowEvent arg0) { } 
	@Override public void windowIconified(WindowEvent arg0) { } 
	@Override public void windowOpened(WindowEvent arg0) { } 
	
	public void pauseGame(boolean pause) {
		Iterator<JComponent> it = pausingItems.iterator();
		
		while(it.hasNext()) {
			it.next().setEnabled(!pause);
		}
	}
	
	public void notifyUserOfError(final String error) {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog(PennyFrame.this, error, "", JOptionPane.WARNING_MESSAGE);
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public void askIfQuoteAccept(final OpenQuote quote) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				TimeoutDialog t = new TimeoutDialog(PennyFrame.this, "Accept " + quote.getBottles() + "b @ " + quote.getPennies() + "ppb = " + quote.getValue() + "p\nFrom " + quote.getFromName() + "?", "Accept trade?", GlobalPreferences.getQuoteAcceptTimeout());
				t.setVisible(true);
				
				serv.confirmAcceptQuote(quote.getId(), t.isOk());
			}
		});
	}
}
