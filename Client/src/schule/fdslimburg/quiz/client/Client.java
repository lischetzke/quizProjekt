package schule.fdslimburg.quiz.client;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.*;

public class Client {
	static Socket client;
	static BufferedWriter out;
	static BufferedReader in;
	
	static Thread threadConnectionStatus;
	static Thread threadIO;
	
	static long lastPingSent = 0L;
	static long lastPingReceived = 0L;
	
	public static void main (String[] args) {
		threadIO = new Thread (() -> {
			while (true) {
				try {
					if (client != null && in != null) {
						if(in.ready()) {
							Thread.sleep(10);
							
							while (in.ready ()) {
								String data = in.readLine ();
								//System.out.println ("Data received: " + data);
								processData(data, true);
							}
						}
					}
					
					if(client != null && out != null && (lastPingSent + 1000) < millis()) {
						sendData ("0001");
						lastPingSent = millis ();
					}
					
					if(lastPingReceived != 0 && (lastPingReceived + 5000) < millis()) {
						// Reconnect
						connect();
					}
					
					Thread.sleep (100);
				} catch (Exception e) {
					System.err.println (e.getMessage ());
					e.printStackTrace ();
				}
			}
		});
		// do your GUI stuff here
		threadIO.start ();
		
		connect();
		
		Scanner s = new Scanner (System.in);
		
		while(true) {
			if(s.hasNextLine ()) {
				s.nextLine ();
				buzz ();
			}
			
			try {
				Thread.sleep (5);
			} catch (InterruptedException ignored) {}
		}
	}
	
	private static void connect() {
		try {
			if(out != null)
				out.close ();
			if(in != null)
				in.close ();
			if(client != null)
				client.close ();
		} catch(Exception ignored) {}
		client = null;
		out = null;
		in = null;
		lastPingSent = 0L;
		lastPingReceived = 0L;
		
		try {
			String[] server = { "172.24.1.1", "5555" };
			//String[] server = { "127.0.0.1", "5555" };
			client = new Socket (server[0], Integer.parseInt (server[1]));
			out = new BufferedWriter (new PrintWriter (client.getOutputStream ()));
			in = new BufferedReader (new InputStreamReader (client.getInputStream ()));
		} catch (Exception ignored) {}
	}
	
	private static void buzz() {
		boolean sent = sendData ("0002");
		if(!sent)
			connect();
	}
	
	private static boolean sendData(String userdata) {
		String millis = millis() + "";
		
		if(client == null || out == null) {
			return false;
		}
		
		try {
			out.write (millis + ";" + userdata + "\n");
			out.flush ();
		} catch (IOException ignored) {
			return false;
		}
		
		return true;
	}
	
	private static void processData(String data, boolean server) {
		String[] split = data.split (";");
		if(server && split.length > 1) {
			if(Objects.equals (split[1], "0001")) {
				// Ping from server
				lastPingReceived = millis();
			}
		}
	}
	
	public static long millis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
}
