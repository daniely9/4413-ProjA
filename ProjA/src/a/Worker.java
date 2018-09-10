package a;

import java.io.*;
import java.net.*;
import java.util.*;

public class Worker {
	Socket client;
	TCPServer server;
	PrintWriter out;
	BufferedReader in;
	
	public Worker(Socket client) {
		this.client = client;
		//this.server = server;
	}
	
	public Worker handle() throws Exception
	{
		System.out.println("worker connected");
		
		//Scanner reader = new Scanner(System.in);
		//
		
		out = new PrintWriter(this.client.getOutputStream(), true);
		
		in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
		
		String read;
		
		while(!(read = in.readLine()).equals("Bye")) 
		{
		
			if(read.equals("getTime")) //M1 Method
			{
				out.println(Worker.time());
				//read = in.readLine();
				
			}
			else if(read.equals("Punch")) //M3 Method
			{
				out.println("you've been punched");
				//read = in.readLine();
			}
			
		}
		out.println("You've been disconnected.");
		in.close();
		out.close();
		this.bye();
		
		return null;

	}
	
	public static String time() //M1
	{
		return new Date().toString();
	}
	
	public void bye() throws IOException //M2
	{
		client.close();
	}
	
	
}

