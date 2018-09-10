package a;

import java.net.*;
import java.util.*;
import java.io.*;

public class TCPServer{
	
	private Set<InetAddress> firewall = new HashSet<InetAddress>();
	public TCPServer(int port, PrintStream log, File file) throws Exception
	{
		//this.firewall = firewall;
		ServerSocket server = new ServerSocket(port); //Created socket
		log.println("Listening on: " + server.getLocalPort()); //add to log that we are listening to given prt
		firewall.add(server.getInetAddress()); //add servers IP to firewall
		
		
		
		while(file.exists()) //listen of port online while file exists
		{
			Socket client = server.accept(); //listen to client to accept
			firewall.add(client.getInetAddress());
			log.println((new Date()).toString()+"|"+"Connection"+"|"+client.getInetAddress());
			
			if(firewall.contains(client.getInetAddress())) 
			{
				System.out.println("Firewall check");
				(new Worker(client)).handle();
			}
			log.println((new Date()).toString()+"|"+"Disconnected"+"|"+client.getInetAddress());
		}
		server.close();
		log.println((new Date()).toString()+"|"+"Server shut down|");
	}
	
	public static void main(String args[]) throws Exception {
		
		int port = 1024;
		PrintStream in = new PrintStream("log.txt");
		new TCPServer(port, in, new File("running.txt"));

	}


}