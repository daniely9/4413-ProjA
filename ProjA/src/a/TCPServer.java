package a;

import java.net.*;
import java.util.*;
import java.io.*;

public class TCPServer{
	
	private Set<InetAddress> firewall = new HashSet<InetAddress>();
	private PrintStream log; 

	public TCPServer(int port, PrintStream log, File file, File whiteList) throws Exception  
	{
		setLog(log);
		ServerSocket server = new ServerSocket(port); //Created socket
		logIt("Listening on: " + server.getLocalPort()); //add to log that we are listening to given prt
		
		
		firewall.add(server.getInetAddress()); //add servers IP to firewall
		FileWriter write = new FileWriter(whiteList);
		FileReader read = new FileReader(whiteList);
		
		
		
		while(file.exists()) //listen of port online while file exists
		{
			
			Socket client = server.accept(); //listen to client to accept
			firewall.add(client.getInetAddress()); //add server to firewall
			write.write(firewall.toString());
			System.out.println(read.read());
			
			if(firewall.contains(client.getInetAddress())) 
			{
				Worker worker = new Worker(client, this);
				Thread t = new Thread(worker); //Passing new thread for new Client to worker 
				t.start();
			}else {
				logIt((new Date()).toString()+"|"+"Firewall violation"+"|"+client.getInetAddress()); //Firewall fail
			}
		}
		server.close();
		logIt((new Date()).toString()+"|"+"Server shut down|");
	}
	
	public static Boolean fireCheck(InetAddress ip) {
		return null;
	}
	
	
	public synchronized void punchOrPlug(String type) throws UnknownHostException{ 
	
		if(type.matches("(?i)punch")) 
		{
			InetAddress ip = InetAddress.getByName(type);
			firewall.add(ip);
		}else{
			InetAddress ip = InetAddress.getByName(type);
			firewall.remove(ip);
			}
	}
		
	public String runSet() {
		return firewall.toString();
	}
	
	public void logIt(String toLog) {
		getLog().println(toLog);
	
	}
	
	
	public Set<InetAddress> getFirewall() {
		return firewall;
	}

	public void setFirewall(Set<InetAddress> firewall) {
		this.firewall = firewall;
	}

	public PrintStream getLog() {
		return log;
	}

	public void setLog(PrintStream log) { //lot setter
		this.log = log;
	}

	public static void main(String args[]) throws Exception {
		
		int port = 1024;
		PrintStream in = new PrintStream("log.txt");
		File run = new File("running.txt");
		File list = new File("whitelist.txt");
		new TCPServer(port, in, run, list);

	}


}