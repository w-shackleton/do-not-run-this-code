package pennygame.server;

import java.io.IOException;

import pennygame.server.client.Clients;


public class PennyServer {
	static Clients clients;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Pennygame Server starting");
		try {
			clients = new Clients();
		} catch (IOException e) {
			System.out.println("Couldn't create socket. Bye!!");
			e.printStackTrace();
			System.exit(1);
		}
		clients.start();
	}
}
