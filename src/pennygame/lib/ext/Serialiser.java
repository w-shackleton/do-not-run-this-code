package pennygame.lib.ext;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import pennygame.lib.msg.PennyMessage;
import pennygame.lib.queues.NetReceiver;

public final class Serialiser {
	/**
	 * The character with which each encoded message should begin
	 */
	protected static final char BEGIN_CHAR = '^';
	protected static final String BEGIN_SEQ = "" + BEGIN_CHAR;

	/**
	 * The character with which each encoded message should end
	 */
	protected static final char END_CHAR = '$';
	protected static final String END_SEQ = "" + END_CHAR;
	public static final String CHARSET = "UTF-8";
	protected static final char DELIMITER = ',';

	/**
	 * Serialises, compresses, b64s and writes the Message to wr.
	 * @param msg
	 * @param wr
	 * @throws IOException if there is an error sending the data
	 */
	public static final void encode(PennyMessage msg, Writer wr)
			throws IOException {
		String s;
		try {
			s = compose(msg);
		} catch(IOException e) {
			System.out.println("Error encoding object into base64!");
			e.printStackTrace();
			return;
		}
		synchronized(wr)
		{
			wr.write(s);
			wr.write(DELIMITER); // Write afterwards so other end will accept
	
			wr.flush();
		}
	}
	
	/**
	 * Writes the preserialised message, ie the output from calling compose.
	 * This puts less strain on the server.
	 * @param encMsg Preserialised message to encode
	 * @param wr {@link Writer} to write to
	 * @throws IOException if there is an error sending the data
	 */
	public static final void encode(String encMsg, Writer wr) throws IOException {
		if (!encMsg.startsWith(BEGIN_SEQ) || !encMsg.endsWith(END_SEQ))
		{
			System.out.println("Someone tried to encode a preserialised message which was invalid!");
			return;
		}
		
		synchronized(wr)
		{
			wr.write(encMsg);
			wr.write(DELIMITER); // Write afterwards so other end will accept
	
			wr.flush();
		}
	}

	/**
	 * 'Composes' a message - this is used by the server so that each {@link pennygame.server.client.CConn} doesn't have to do this for multicast messages
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	public static final String compose(PennyMessage msg) throws IOException {
		return BEGIN_CHAR + Base64.encodeObject(msg, Base64.GZIP) + END_CHAR;
	}

	/**
	 * Reads a {@link PennyMessage} from the Reader (which should come
	 * from a network connection)
	 * 
	 * @param r
	 *            the reader to use
	 * @return The decoded {@link PennyMessage}, or null.
	 * @throws IOException
	 */
	public static final PennyMessage decode(Reader r, NetReceiver.NetReceiverStopper stopper)
			throws IOException {
		return decompose(NextString(r, stopper));
	}

	public static final PennyMessage decompose(String s) {
		if(s.charAt(0) == '*')
		{
			// System.out.println("keepalive...");
			return null; // Keepalive
		}
		if (!s.startsWith(BEGIN_SEQ) || !s.endsWith(END_SEQ))
		{
			System.out.println("Invalid packet!");
			return null; // Invalid message OR keepalive
		}
		
		s = s.substring(1, s.length() - 1);
		
		Object obj;
		try {
			obj = Base64.decodeToObject(s, Base64.GZIP, null);
		} catch (ClassNotFoundException e1) {
			System.out.println("Error: Invalid class types! (Perhaps differing client / server versions");
			e1.printStackTrace();
			return null;
		} catch (NullPointerException e2) {
			e2.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		PennyMessage pm;
		try {
			pm = (PennyMessage) obj;
		} catch (RuntimeException e) {
			System.out
					.println("Error: Invalid class types! (Perhaps differing client / server versions");
			return null;
		}
		return pm;
	}

	protected static final String NextString(Reader r, NetReceiver.NetReceiverStopper stopper)
			throws IOException {
		return NextString(r, DELIMITER, stopper);
	}

	protected static final String NextString(Reader r, char delimeter, NetReceiver.NetReceiverStopper stopper)
			throws IOException {
		if (r == null)
			throw new IOException();

		Writer writer = new StringWriter();

		int buf;
		synchronized(r)
		{
			while ((buf = r.read()) != delimeter) // Keep reading through and
													// putting in writer until
													// encounter delimeter
			{
				if (buf == -1) // If no more text left, must not be here yet...
				{
					// TODO: Change to use r.ready()?
					while ((buf = r.read()) == -1) // Keep waiting for more text...
					{
						if(stopper.stopping) // Stop requested by NetReceiver
							return "*";
						try {
							Thread.sleep(80);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				writer.write(buf);
			}
		}
		writer.close();
		return writer.toString();
	}
}
