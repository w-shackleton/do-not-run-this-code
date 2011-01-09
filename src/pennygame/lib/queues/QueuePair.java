package pennygame.lib.queues;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import pennygame.lib.ext.Serialiser;

/**
 * This class is a pair of queues, ie. four threads (2 for the processing queues
 * and 2 for the data transfers)
 * 
 * @author william
 * 
 * @param <P>
 * @param <C>
 */
public abstract class QueuePair<P extends MainThread, C extends PushHandler> {
	public final MainThreadQueue<P> mainThread;
	public final PushHandlerQueue<C> pushQueue;
	public final Socket socket;

	public QueuePair() {
		socket = createSocket();
		BufferedWriter out = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), Serialiser.CHARSET));
			out = new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream(), Serialiser.CHARSET));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("ERROR: Couldn't initiate socket!");
			e.printStackTrace();
		}
		mainThread = new MainThreadQueue<P>(createMainThread(), out);
		NetReceiver nr = new NetReceiver(in);
		pushQueue = new PushHandlerQueue<C>(nr, createPushHandler(nr));
	}

	/**
	 * Creates the underlying socket of this class.
	 * 
	 * @return
	 */
	protected abstract Socket createSocket();

	/**
	 * Creates the implementation of the main thread for this class
	 * 
	 * @return The new Object derived from MainThread
	 */
	protected abstract P createMainThread();

	/**
	 * Creates the implementation of the push handler for this class
	 * 
	 * @param nr
	 *            The net receiver to pass to the creator
	 * @return
	 */
	protected abstract C createPushHandler(NetReceiver nr);
}
