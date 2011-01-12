package pennygame.lib.queues;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import pennygame.lib.ext.Serialiser;
import pennygame.lib.queues.handlers.OnConnectionListener;

/**
 * This class is a pair of queues, ie. four threads (2 for the processing queues
 * and 2 for the data transfers)
 * 
 * @author william
 * 
 * @param <P>
 * @param <C>
 */
public abstract class QueuePair<P extends MainThread, C extends PushHandler> implements OnConnectionListener  {
	protected MainThreadQueue<P> mainThreadQueue;
	protected PushHandlerQueue<C> pushHandlerQueue;
	
	protected P mainThread; // Added for convenience
	protected C pushHandler;
	
	protected final Socket socket;
	
	/**
	 * Unique identifier for this class. Purely used to debug threads
	 */
	private final int connectionId;

	/**
	 * 
	 * @param sock The socket to connect to
	 * @param id An identifier. Doesn't have to be anything, purely used to identify threads for easier debugging
	 */
	public QueuePair(Socket sock, int id) {
		super();
		socket = sock;
		connectionId = id;
	}
	
	/**
	 * Connects the socket, starts the queues and starts the threads.
	 */
	public synchronized void start()
	{
		Writer out = null;
		Reader in = null;
		
		try {
			in = new InputStreamReader(
					socket.getInputStream(), Serialiser.CHARSET);
			out = new OutputStreamWriter(
					socket.getOutputStream(), Serialiser.CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't initiate socket!");
			e.printStackTrace();
		}
		
		P mt = createMainThread("Cid: " + connectionId + ", MT");
		NetSender<P> ns = new NetSender<P>(mt, out, this, "Cid: " + connectionId + ", NS");
		mainThreadQueue = new MainThreadQueue<P>(mt, ns);
		mainThread = mainThreadQueue.producer;
		
		NetReceiver nr = new NetReceiver(in, this, "Cid: " + connectionId + ", NR");
		pushHandlerQueue = new PushHandlerQueue<C>(nr, createPushHandler(nr, "Cid: " + connectionId + ", PH"));
		pushHandler = pushHandlerQueue.consumer;
		
		mainThreadQueue.consumer.start(); // Start the consumer first
		mainThreadQueue.producer.start();
		
		pushHandlerQueue.consumer.start();
		pushHandlerQueue.producer.start();
	}

	/**
	 * Creates the implementation of the main thread for this class, but doesn't start it.
	 * 
	 * @return The new Object derived from MainThread
	 */
	protected abstract P createMainThread(String threadIdentifier);

	/**
	 * Creates the implementation of the push handler for this class, but doesn't start it.
	 * 
	 * @param nr
	 *            The net receiver to pass to the creator
	 * @return
	 */
	protected abstract C createPushHandler(NetReceiver nr, String threadIdentifier);

	@Override
	public synchronized void onConnectionLost() {
		// Start shutting everything down.
		System.out.println("Error in connection, shutting down.");
		stop();
	}
	
	public synchronized void stop()
	{
		if(mainThreadQueue != null)
		{
			mainThreadQueue.producer.beginStopping();
			mainThreadQueue.consumer.beginStopping();
		}
		
		if(pushHandlerQueue != null)
		{
			pushHandlerQueue.producer.beginStopping();
			pushHandlerQueue.consumer.beginStopping();
		}
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// TODO: move to implements?
	protected ConnectionEnder connectionEnder = new ConnectionEnder() {
		
		@Override
		public void endConnection() {
			stop();
		}
	};
	
	/**
	 * Used to let child threads notify the QueuePair implementation that it should / has died
	 * @author william
	 *
	 */
	public interface ConnectionEnder {
		public void endConnection();
	}
}
