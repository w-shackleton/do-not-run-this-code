package pennygame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import pennygame.server.admin.AdminServer;
import pennygame.server.client.CMulticaster;
import pennygame.server.client.Clients;
import pennygame.server.db.DBManager;
import pennygame.server.db.GameUtils;


public class PennyServer {
	static Clients clients;
	static AdminServer admin;
	static DBManager db;
	static CMulticaster multicast;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Pennygame Server starting");
		
		System.out.println("DB...");
		try {
			db = new DBManager();
		} catch (SQLException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("DB initialised");
		db.start();
		System.out.println("DB started");
		
		System.out.println("Multicaster...");
		multicast = new CMulticaster();
		System.out.println("Multicaster initialised");
		multicast.start();
		System.out.println("Multicaster started");
		
		System.out.println("Game Utils...");
		GameUtils gameUtils = null;
		try {
			gameUtils = new GameUtils(db.getConnection(), db.getQuoteAcceptingConnection(), multicast);
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		
		System.out.println("Game Utils initialised");
		System.out.println("Game Utils started");
		
		System.out.println("Clients...");
		try {
			clients = new Clients(gameUtils);
		} catch (IOException e) {
			System.out.println("Couldn't create socket. Bye!!");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Clients initialised");
		clients.start();
		System.out.println("Clients started");
		multicast.setClients(clients);
		
		System.out.println("Admin...");
		try {
			admin = new AdminServer(gameUtils);
		} catch (IOException e1) {
			System.out.println("Couldn't create admin socket. Bye!!");
			e1.printStackTrace();
			System.exit(2);
		}
		System.out.println("Admin initialised");
		admin.start();
		System.out.println("Admin started");
		
		System.out.println("Ready to go...");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String line = "";
		while(true)
		{
			try {
				line = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(line.equals("q"))
			{
				break;
			}
		}
		
		gameUtils.beginStopping();
		multicast.beginStopping();
		clients.beginStopping();
		admin.beginStopping();
		db.beginStopping();
	}
}
