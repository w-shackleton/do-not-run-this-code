package pennygame.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import pennygame.server.admin.AdminServer;
import pennygame.server.client.Clients;
import pennygame.server.db.DBManager;


public class PennyServer {
	static Clients clients;
	static AdminServer admin;
	static DBManager db;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Pennygame Server starting");
		
		try {
			db = new DBManager();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("DB Initialised");
		db.start();
		System.out.println("DB Started");
		
		try {
			clients = new Clients();
		} catch (IOException e) {
			System.out.println("Couldn't create socket. Bye!!");
			e.printStackTrace();
			System.exit(1);
		}
		clients.start();
		
		try {
			admin = new AdminServer();
		} catch (IOException e1) {
			System.out.println("Couldn't create admin socket. Bye!!");
			e1.printStackTrace();
			System.exit(2);
		}
		admin.start();
		
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
		
		db.beginStopping();
		clients.beginStopping();
		admin.beginStopping();
	}
}
