package pennygame.projector;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pennygame.projector.queues.PSConn;

public class ProjectorFrame extends JFrame implements WindowListener {
	
	private static final long serialVersionUID = -4773005041038467984L;
	
	protected final PSConn serv;
	protected final ProjectorClient applet;
	
	private TradeGraph graph;
	
	public ProjectorFrame(PSConn serv, ProjectorClient applet) {
		super("Pennybottle game");
		this.serv = serv;
		this.applet = applet;
		
		addWindowListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		initUI();
		
		serv.setParentFrame(this);
	}
	
	private void initUI() {
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		
		graph = new TradeGraph(serv);
		container.add(graph, BorderLayout.CENTER);
		
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
	
	public TradeGraph getGraph() {
		return graph;
	}
}
