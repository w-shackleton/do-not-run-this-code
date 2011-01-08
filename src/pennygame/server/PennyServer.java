package pennygame.server;

import pennygame.lib.PennyMessage;
import pennygame.lib.ext.Serialiser;


public class PennyServer
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Hello!");
		PennyMessage m = new PennyMessage();
		
		Serialiser.encode(m, null);
	}
}
