package a;

import java.io.*;
import java.net.*;
import java.util.*;

public class Worker {
	Socket client;
	TCPServer server;
	PrintWriter out;
	BufferedReader in;
	private static final HashMap<String, String> accounts = new HashMap<String, String>() {
		{
			put("Daniel", "password");
			put("User", "password");
			put("daniel", "pAssword");
		}
	};

	public Worker(Socket client, TCPServer server) {
		this.client = client;
		this.server = server;
	}

	public Worker handle() throws Exception {
		// System.out.println("worker connected"); //worker connection worked;

		out = new PrintWriter(this.client.getOutputStream(), true); // output stream back to client
		in = new BufferedReader(new InputStreamReader(this.client.getInputStream())); // input stream from client
		String read; // store input for computations

		while (!(read = in.readLine()).equals("Bye")) // Keep looking for commands until Bye is typed in,
		{
			if (read.equals("getTime")) // M1 get the time
			{
				out.println(Worker.time());
			} 
			else if (read.matches("Punch\\s<[0-2]?[0-5]?[0-5].[0-2]?[0-5]?[0-5].[0-2]?[0-5]?[0-5].[0-2]?[0-5]?[0-5]>")) // M3 Punch<IP>
			{
				punch(read);
				out.println(server.runSet());
				out.println("you've been punched");
				
			}
			else if(read.matches("Plug\\s<[0-2]?[0-5]?[0-5].[0-2]?[0-5]?[0-5].[0-2]?[0-5]?[0-5].[0-2]?[0-5]?[0-5]>"))
			{
				push(read);
				out.println(server.runSet());
				out.println("you've been pluged");
			}
			else if (read.matches("Auth\\s<\\w+>\\s<\\w+>")) // M6 Authorization
			{
				if (Worker.auth(read)) {
					out.println("You are in!");
				} else {
					out.println("Auth Failure");
				}
			}
			else {
				out.println("Don't understand <" + read +">"); //Error message
			}

		}
		// out.println("You've been disconnected.");
		in.close(); //close input stream
		out.close(); //close output stream
		this.bye(); //close client socket

		return null;

	}

	private static String time() // M1
	{
		return new Date().toString();
	}

	private void bye() throws IOException // M2
	{
		client.close();
	}
	
	private void punch(String ip) throws UnknownHostException {
		String empty = ""; //empty string
		String[] split = (ip.split("\\s+")); //split string on black space
		String address = split[1].replaceAll("[^A-Z,a-z,0-9,.]", empty); //replace "<>" with ""
		System.out.println(address);
		server.push(address);
	}
	private void push(String ip) throws UnknownHostException {
		String empty = ""; //empty string
		String[] split = (ip.split("\\s+")); //split string on black space
		String address = split[1].replaceAll("[^A-Z,a-z,0-9,.]", empty); //replace "<>" with ""
		System.out.println(address);
		server.pull(address);
	}
	
	private static boolean auth(String user) // M6
	{
		String empty = ""; //empty string
		boolean result; //empty result
		String[] split = (user.split("\\s+")); //split string on black space
		String acc = split[1].replaceAll("[^A-Z,a-z,0-9]", empty); //replace "<" with ""
		String pass = split[2].replaceAll("[^A-Z,a-z,0-9]", empty); // replace ">" with ""
		//System.out.println("User: " + acc + " Pass: " + pass); // debugging
		if (accounts.get(acc).equals(pass)) // if the account key has the value password
		{
			result = true; // return true
		} else {
			result = false; // return false
		}
		return result; // return result
	}

}
