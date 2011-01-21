package pennygame.server.client;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import pennygame.lib.ext.Serialiser;
import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.MessageProducer;

/**
 * Sends messages out to all connected clients; inherits MessageProducer for its
 * message queueing capabilities; (it actually does the consuming part, but
 * meh.)
 * 
 * @author william
 * 
 */
public class CMulticaster extends MessageProducer {

	private Clients clients = null;

	public CMulticaster() {
		super("Multicaster");
		pauseTime = 15;
	}

	@Override
	protected void loop() {
		PennyMessage msg = (PennyMessage) getMessageNow();
		if(msg == null) return;
		try {
			String sMsg = Serialiser.compose(msg);
		
			Collection<CConn> cs = clients.clients.values();
			
			Iterator<CConn> it = cs.iterator();
			
			System.out.println("Multicasting message...");
			
			while(it.hasNext()) {
				CConn client = it.next();
				client.sendSerialisedMessage(sMsg);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setup() {
		while(clients == null)
			try {
				Thread.sleep(200); // Wait until received
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	}

	public void setClients(Clients clients) {
		this.clients = clients;
	}
	
	public void multicastMessage(PennyMessage msg) {
		putMessage(msg);
		System.out.println("Multicasting message...");
	}
	
	/**
	 * Sends a message to a specified list of clients; this doesn't get done on another thread
	 * @param clientIds A list of clients to send to
	 * @param msg The message to send
	 */
	public void sendMessageToClients(int[] clientIds, PennyMessage msg) {
		String sMsg;
		try {
			sMsg = Serialiser.compose(msg);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		for(int clientId : clientIds) {
			CConn client = clients.getClientForUserId(clientId);
			if(client == null) continue;
			
			client.sendSerialisedMessage(sMsg);
		}
	}
	
	public void sendMessageToClient(int clientId, PennyMessage msg) {
		String sMsg;
		try {
			sMsg = Serialiser.compose(msg);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		CConn client = clients.getClientForUserId(clientId);
		if(client == null) return;
		client.sendSerialisedMessage(sMsg);
	}
}
