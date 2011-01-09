package pennygame.lib.ext;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import pennygame.lib.PennyMessage;

public final class Serialiser {
	/**
	 * The character with which each encoded message should begin
	 */
	protected static final char BEGIN_CHAR = '^';
	protected static final byte[] BEGIN_SEQ = { BEGIN_CHAR };

	/**
	 * The character with which each encoded message should end
	 */
	protected static final char END_CHAR = '$';
	protected static final byte[] END_SEQ = { END_CHAR };
	public static final String CHARSET = "UTF-8";
	protected static final char DELIMITER = ',';

	public static final void encode(PennyMessage msg, BufferedWriter wr)
			throws IOException {
		String b64data = compose(msg);

		wr.write(b64data);
		wr.write(DELIMITER); // Write afterwards so other end will accept

		wr.flush();
	}

	public static final String compose(PennyMessage msg) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		baos.write(BEGIN_SEQ); // Begin
		{
			//GZIPOutputStream gz = new GZIPOutputStream(baos); // Compress
			ByteArrayOutputStream gz = baos;
			{
				ObjectOutputStream oos = new ObjectOutputStream(gz); // Write
																		// object
				oos.writeObject(msg);

				oos.flush();
				oos.close();
			}
			gz.flush();
			gz.close();
		}
		baos.write(END_SEQ);
		baos.flush();

		String b64data = Base64.encode(baos.toByteArray());
		baos.close();
		return b64data;
	}

	/**
	 * Reads a {@link PennyMessage} from the BufferedReader (which should come
	 * from a network connection)
	 * 
	 * @param r
	 *            the reader to use
	 * @return The decoded {@link PennyMessage}, or null.
	 * @throws IOException
	 */
	public static final PennyMessage decode(BufferedReader r)
			throws IOException {
		return decompose(NextString(r));
	}

	public static final PennyMessage decompose(String s) throws IOException {
		byte[] data = Base64.decode(s);
		if (data[0] != BEGIN_CHAR || data[data.length - 1] != END_CHAR)
			return null; // Invalid message

		Object obj = null;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		{
			//GZIPInputStream gz = new GZIPInputStream(bais);
			ByteArrayInputStream gz = bais;
			{
				ObjectInputStream ois = new ObjectInputStream(gz);
				try {
					obj = ois.readObject();
				} catch (ClassNotFoundException e) {
					System.out
							.println("Error: Unknown class types! (Perhaps differing client / server versions");
					e.printStackTrace();
				}
				ois.close();
			}
			gz.close();
		}
		bais.close();

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

	protected static final String NextString(BufferedReader r)
			throws IOException {
		return NextString(r, DELIMITER);
	}

	protected static final String NextString(BufferedReader r, char delimeter)
			throws IOException {
		if (r == null)
			throw new IOException();

		Writer writer = new StringWriter();

		int buf;
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
		writer.close();
		return writer.toString();
	}
}
