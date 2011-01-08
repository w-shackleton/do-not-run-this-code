package pennygame.lib.ext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import pennygame.lib.PennyMessage;

public final class Serialiser
{
	public static final void encode(PennyMessage msg, OutputStream os)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
	        ObjectOutputStream oos = new ObjectOutputStream( baos );
	        oos.writeObject(msg);
	        oos.close();
		}
		catch(IOException e)
		{
			// TODO: Add proper catcher when interface created
		}
		
		String b64data = Base64.encode(baos.toByteArray());
		System.out.println(b64data);
	}
	
	public static final PennyMessage decode(InputStream is)
	{
		return null;
	}
}
