package pennygame.lib.ext;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import pennygame.lib.PennyMessage;

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
	public static final PennyMessage decode(Reader r)
			throws IOException {
		return decompose(NextString(r));
	}

	public static final PennyMessage decompose(String s) {
		if(s.charAt(0) == '*')
		{
			System.out.println("keepalive...");
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

	protected static final String NextString(Reader r)
			throws IOException {
		return NextString(r, DELIMITER);
	}

	protected static final String NextString(Reader r, char delimeter)
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
						try {
							Thread.sleep(20);
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
