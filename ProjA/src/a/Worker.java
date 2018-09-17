package a;

import java.io.*;
import java.math.*;
import java.net.*;
import java.util.*;
import projA.*;
import javax.xml.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.google.gson.*;

public class Worker implements Runnable {
	Socket client;
	static TCPServer server;
	PrintWriter out;
	BufferedReader in;
	public final String bye = "(?i)bye";
	public final String time = "(?i)gettime";
	public final String punchIp = "(?i)punch\\s(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public final String plugIp = "(?i)plug\\s(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
	public final String primeDig = "(?i)prime\\s\\d+";
	public final String authorize = "((?i)auth)\\s(\\w+\\s\\w+)";
	public final String rosterReg = "(?i)roster\\s\\w+\\s(XML|JSON)";
	
	
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
		server.logIt((new Date()).toString()+"|"+"Connection"+"|"+client.getInetAddress()); //Client connection

		out = new PrintWriter(this.client.getOutputStream(), true); // output stream back to client
		in = new BufferedReader(new InputStreamReader(this.client.getInputStream())); // input stream from client
		String read; // store input for computations

		while (!(read = in.readLine()).matches(bye)) // Keep looking for commands until Bye is typed in,
		{
			if (read.matches(time)) // M1 get the time
			{
				out.println(Worker.time());
			} 
			else if (read.matches(punchIp)) // M3 Punch<IP>
			{
				punch(read);
				//out.println(server.runSet());
			}
			else if (read.matches(plugIp)) //M4 Plug<ip>
			{
				plug(read);
				//out.println(server.runSet());
			}
			else if (read.matches(primeDig)) //M5 return prime of length digit
			{
				out.println(prime(read));
			}
			else if (read.matches(authorize)) // M6 Authorization
			{
				if (Worker.auth(read)) {
					out.println("You are in!");
				} else {
					out.println("Auth Failure");
				}
			}
			else if (read.matches(rosterReg)) 
			{
				roster(read);
				//out.println("error");
			}
			else 
			{
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
		server.logIt((new Date()).toString() + "|" + "Disconnected" + "|" + client.getInetAddress()); // Client disconnection
		client.close();
	}

	private void punch(String ip) throws UnknownHostException // M3 add to IP to firewall
	{
		// String empty = ""; //empty string
		String[] split = (ip.split("\\s+")); // split string on black space
		String address = split[1];
		System.out.println(address);
		server.punchOrPlug(address);
	}

	private void plug(String ip) throws UnknownHostException // M4 remove IP from firewall
	{
		// String empty = ""; //empty string
		String[] split = (ip.split("\\s+")); // split string on black space
		String address = split[1];
		System.out.println(address);
		server.punchOrPlug(address);
	}

	private static BigInteger prime(String digits) // M5 return prime number length of digits
	{
			BigInteger result = null;
			String[] split = digits.split("\\s+");
			int digLen = Integer.parseInt(split[1]);
			digLen *= 3.33;
			if (!(digLen < 2)) {
				Random rnd = new Random();
				result = BigInteger.probablePrime(digLen, rnd);
			}else {
				result = BigInteger.ZERO;
			}


		return result;
	}

	private static boolean auth(String user) // M6 see if user and pass match
	{
		// String empty = ""; //empty string
		boolean result; // empty result
		String[] split = (user.split("\\s+")); // split string on black space
		String acc = split[1];
		String pass = split[2];
		// System.out.println("User: " + acc + " Pass: " + pass); // debugging
		if (accounts.get(acc).equals(pass)) // if the account key has the value password
		{
			result = true; // return true
		} else {
			result = false; // return false
		}
		return result; // return result
	}

	private void roster(String input) throws JAXBException // M7 return roster of students in course in either json||xml
	{
		String[] split = input.split("\\s+");
		String number = split[1];
		String format = split[2];
		Course course = Util.getCourse(number);
		
		
		if(format.matches("xml")) 
		{
			JAXBContext context = JAXBContext.newInstance(projA.Course.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(course, out);	
		}else if(format.matches("json")) {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(course);
			out.println(json);
		}
	}

	@Override
	public void run() {
		try {
			this.handle();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String exception = String.format("%s | Exception: %s | ", time(), e.toString());
			e.printStackTrace();
			server.logIt(exception);
		}

	}

}
