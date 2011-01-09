package pennygame.lib.queues;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

import pennygame.lib.ext.Serialiser;
import pennygame.lib.queues.handlers.OnConnectionLostListener;

/**
 * This class is a pair of queues, ie. four threads (2 for the processing queues
 * and 2 for the data transfers)
 * 
 * @author william
 * 
 * @param <P>
 * @param <C>
 */
public abstract class QueuePair<P extends MainThread, C extends PushHandler> implements OnConnectionLostListener  {
	protected final MainThreadQueue<P> mainThreadQueue;
	protected final PushHandlerQueue<C> pushHandlerQueue;
	
	protected final P mainThread; // Added for convenience
	protected final C pushHandler;
	
	protected final Socket socket;

	public QueuePair(Socket sock) {
		Writer out = null;
		Reader in = null;
		socket = sock;
		
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
		
		P mt = createMainThread();
		NetSender<P> ns = new NetSender<P>(mt, out, this);
		mainThreadQueue = new MainThreadQueue<P>(mt, ns);
		
		NetReceiver nr = new NetReceiver(in, this);
		pushHandlerQueue = new PushHandlerQueue<C>(nr, createPushHandler(nr));
		
		
		mainThread = mainThreadQueue.producer;
		pushHandler = pushHandlerQueue.consumer;
	}
	
	/**
	 * Connects the socket, starts the queues and starts the threads.
	 */
	public synchronized void start()
	{
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
	protected abstract P createMainThread();

	/**
	 * Creates the implementation of the push handler for this class, but doesn't start it.
	 * 
	 * @param nr
	 *            The net receiver to pass to the creator
	 * @return
	 */
	protected abstract C createPushHandler(NetReceiver nr);

	@Override
	public synchronized void onConnectionLost() {
		// Start shutting everything down.
		System.out.println("Error in connection, shutting down.");
		stop();
	}
	
	public synchronized void stop()
	{
		mainThreadQueue.producer.beginStopping();
		mainThreadQueue.consumer.beginStopping();
		
		pushHandlerQueue.producer.beginStopping();
		pushHandlerQueue.consumer.beginStopping();
		
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
