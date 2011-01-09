package pennygame.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import pennygame.lib.PennyMessage;
import pennygame.lib.ext.Base64;
import pennygame.lib.ext.Serialiser;

public class PennyServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Hello!");
		System.out.println(Base64.decode(Base64.encode("HELLO".getBytes())));
		try {
			PennyMessage pm = new PennyMessage();
			pm.message = "QWERTYUIOP!";

			Socket skt = new Socket("localhost", 1234);
			skt.setSoTimeout(3000);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					skt.getInputStream(), Serialiser.CHARSET));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					skt.getOutputStream(), Serialiser.CHARSET));

			Serialiser.encode(pm, out);
			PennyMessage pm2 = Serialiser.decode(in);
			System.out.println("Text: " + pm.message);
			System.out.println("Text2: " + pm2.message);

			in.close();
			out.close();
			skt.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConnectException e) {
			System.out.println("Error: could not connect!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
