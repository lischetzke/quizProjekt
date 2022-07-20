package schule.fdslimburg.quiz.server.comm;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import static schule.fdslimburg.quiz.server.Util.millis;

public class Client {
	private static int clientIdCounter = 0;
	private static Dictionary<InetAddress, Integer> clientIdMapping = new Hashtable<> ();
	
	protected Socket client;
	protected int clientId;
	public BufferedReader input;
	public BufferedWriter output;
	public long lastPingSent = 0L;
	public long lastPingReceived = 0L;
	public boolean runningHandlingClient = false;
	public boolean stopThread = false;
	private Thread clientHandler;
	
	public Client(Communication comm, Socket conn) throws IOException {
		this.client = conn;
		synchronized (this) {
			// Check if ID exists for IP
			Integer oldId = clientIdMapping.get (this.client.getInetAddress ());
			if(oldId == null) {
				this.clientId = clientIdCounter++;
				clientIdMapping.put (this.client.getInetAddress (), this.clientId);
				System.out.println ("New client connected: " + this.client.getInetAddress ().toString () + " / " + this.clientId);
			} else {
				this.clientId = oldId;
				System.out.println ("Existing client reconnected: " + this.client.getInetAddress ().toString () + " / " + this.clientId);
			}
		}
		this.input = new BufferedReader (new InputStreamReader (conn.getInputStream ()));
		this.output = new BufferedWriter (new PrintWriter(conn.getOutputStream ()));
		this.clientHandler = new Thread (new RunnableHandlingClient (comm, this));
		this.clientHandler.start ();
	}
	
	public void close() throws IOException, InterruptedException {
		stopThread = true;
		int count = 0;
		while(runningHandlingClient) {
			Thread.sleep(100);
			// Allow max 10s for closing
			if(++count > 100)
				break;
		}
		input.close();
		output.close();
		client.close();
	}
	
	private boolean sendData(Client c, NetStatus code) {
		String data = millis() + ";" + code.padValue () + "\n";
		
		try {
			c.output.write (data);
			c.output.flush();
		} catch (IOException ex) {
			System.err.println ("Connection error!");
			System.err.println (ex.getMessage ());
			ex.getStackTrace ();
			return false;
		}
		
		return true;
	}
	
	static class RunnableHandlingClient implements Runnable {
		private Communication comm;
		private Client c;
		
		public RunnableHandlingClient(Communication comm, Client c) {
			this.comm = comm;
			this.c = c;
		}
		
		@Override
		public void run () {
			c.runningHandlingClient = true;
			System.out.println ("Started RunnableHandlingClient");
			
			while (!c.stopThread) {
				try {
					boolean successfulPingSent = true;
					// Send Ping to client
					if((c.lastPingSent + 1000) < millis()) {
						successfulPingSent = c.sendData (c, NetStatus.PING);
						c.lastPingSent = millis();
						//System.out.println ("Send Ping to " + c.client.getInetAddress ().toString () + " at " + c.lastPingSent);
					}
					
					// Check if clients last ping is 2 seconds old
					if(c.lastPingReceived != 0 && (!successfulPingSent || (c.lastPingReceived + 5000) < millis())) {
						System.out.println ("Removing client " + c.client.getInetAddress ().toString () + " because timeout.");
						c.close();
						comm.addClientToRemove (c);
						continue;
					}
					
					if (!c.input.ready ()) {
						continue;
					}
					
					Thread.sleep (5);
					
					String clientData = c.input.readLine ();
					NetData data = NetData.parse (clientData);
					
					if(data == null)
						continue;
					
					if(data.status == NetStatus.PING) {
						c.lastPingReceived = millis();
						//System.out.println ("Ping received for " + c.client.getInetAddress ().toString () + " at " + c.lastPingReceived);
					} else {
						// Show Client data
						
						
						//String datastring1 = String.format("%8s", Long.toBinaryString(data.timestamp)).replace(' ', '0');
						//String datastring2 = String.format("%8s", Integer.toBinaryString(data.status.value)).replace(' ', '0');
						//System.out.println ("Client data received: " + datastring1 + " / " + datastring2);
					}
					
					// Fire ClientDataEvent
					comm.triggerClientDataEvent (c, data);
				} catch (IOException e) {
					System.err.println ("Client broken.");
					System.err.println (e.getMessage ());
				} catch (InterruptedException e) {
					System.err.println ("Thread is a workaholic.");
					System.err.println (e.getMessage ());
				}
			}
			System.out.println ("Stopping RunnableHandlingClient");
			
			c.runningHandlingClient = false;
		}
	}
	
}
