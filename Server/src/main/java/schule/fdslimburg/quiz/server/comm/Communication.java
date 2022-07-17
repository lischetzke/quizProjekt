package schule.fdslimburg.quiz.server.comm;

import schule.fdslimburg.quiz.server.IControl;
import schule.fdslimburg.quiz.server.events.ClientDataEventArgs;
import schule.fdslimburg.quiz.server.events.ClientDataEventListener;
import schule.fdslimburg.quiz.server.events.NewClientEventArgs;
import schule.fdslimburg.quiz.server.events.NewClientEventListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import static schule.fdslimburg.quiz.server.comm.Client.millis;

enum Character {
	ENTER ('\n');
	
	private char value;
	
	Character (char value) {
		this.value = value;
	}
	
	char val () {
		return value;
	}
}

public class Communication implements IControl {
	private ServerSocket ss;
	private Thread threadAcceptingClients;
	private Thread threadHandlingClients;
	private boolean stopThread = false;
	private boolean runningAcceptingClients = false;
	private boolean runningHandlingClients = false;
	private List<Client> clients = new ArrayList<> ();
	private List<Client> clientsToRemove = new ArrayList<> ();
	private boolean waitingClients = true;
	public List<EventListener> eventListeners = new ArrayList<> ();
	
	public Communication (int port) {
		try {
			this.ss = new ServerSocket (port);
			this.threadAcceptingClients = new Thread (new RunnableAcceptingClient());
			this.threadHandlingClients = new Thread (new RunnableHandlingClients ());
		} catch (IOException e) {
			System.err.println ("Couldn't open port, abort!");
			System.err.println (e.getMessage ());
		}
	}
	
	class RunnableAcceptingClient implements Runnable {
		@Override
		public void run () {
			runningAcceptingClients = true;
			
			while (!stopThread) {
				if (waitingClients) {
					try {
						Socket newClient = ss.accept ();
						System.out.println ("New client");
						Client c = new Client (newClient);
						clients.add (c);
						triggerNewClientEvent (c);
					} catch (IOException e) {
						if(e.getMessage ().equals ("Accept timed out"))
							continue;
						System.err.println ("Something happened, that shouldn't happen, oops.");
						System.err.println (e.getMessage ());
					}
				} else {
					try {
						Thread.sleep (1000);
					} catch (InterruptedException e) {
						throw new RuntimeException (e);
					}
				}
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException (e);
			}
			
			// Close everything
			for (Client c : clients) {
				try {
					c.close ();
				} catch (IOException e) {
					System.err.println ("Client broken.");
					System.err.println (e.getMessage ());
				}
			}
			try {
				ss.close ();
			} catch (IOException e) {
				System.err.println ("Client broken.");
				System.err.println (e.getMessage ());
			}
			
			runningAcceptingClients = false;
		}
	}
	
	class RunnableHandlingClients implements Runnable {
		@Override
		public void run () {
			runningHandlingClients = true;
			
			while (!stopThread) {
				for (Client c : clients) {
					try {
						// Send Ping to client
						if((c.lastPingSent + 500) < millis()) {
							sendData (c, MSG_PING);
							c.lastPingSent = millis();
							System.out.println ("Send Ping to " + c.client.getInetAddress ().toString () + " at " + c.lastPingSent);
						}
						
						// Check if clients last ping is 2 seconds old
						if(c.lastPingReceived != 0 && (c.lastPingReceived + 2000) < millis()) {
							System.out.println ("Removing client " + c.client.getInetAddress ().toString () + " because timeout.");
							c.close();
							clientsToRemove.add (c);
							continue;
						}
						
						if(clientsToRemove.size () > 0) {
							clients.removeAll (clientsToRemove);
							clientsToRemove.clear ();
							System.out.println ("Scheduled removal of clients finished.");
						}
						
						if (!c.input.ready ())
							continue;
						
						Thread.sleep (5);
						
						List<Byte> data = new ArrayList<> ();
						while(c.input.ready()) {
							data.add((byte) c.input.read ());
						}
						byte[] data2 = new byte[data.size ()];
						for(int i = 0; i < data.size (); i++) {
							data2[i] = data.get (i);
						}
						
						if(data2[13] == 0x01) {
							c.lastPingReceived = millis();
							System.out.println ("Ping received for " + c.client.getInetAddress ().toString () + " at " + c.lastPingReceived);
						} else {
							// Show Client data
							byte[] cTimestampBytes = new byte[8];
							System.arraycopy (data2, 4, cTimestampBytes, 0, 8);
							long cTimestamp = bytesToLong (cTimestampBytes);
							String datastring1 = String.format("%8s", Integer.toBinaryString(data2[12] & 0xFF)).replace(' ', '0');
							String datastring2 = String.format("%8s", Integer.toBinaryString(data2[13] & 0xFF)).replace(' ', '0');
							System.out.println ("Client data received: " + cTimestamp + " / " + datastring1 + " " + datastring2);
						}
						
						// Fire ClientDataEvent
						triggerClientDataEvent (c, data2);
					} catch (IOException e) {
						System.err.println ("Client broken.");
						System.err.println (e.getMessage ());
					} catch (InterruptedException e) {
						System.err.println ("Thread is a workaholic.");
						System.err.println (e.getMessage ());
					}
				}
			}
			
			// TODO: Disconnect all clients
			
			runningHandlingClients = false;
		}
	}
	
	public void addNewClientEventListener(NewClientEventListener el) {
		eventListeners.add (el);
	}
	
	public void addClientDataEventListener(ClientDataEventListener el) {
		eventListeners.add (el);
	}
	
	private void triggerNewClientEvent(Client c) {
		NewClientEventArgs args = new NewClientEventArgs ();
		args.clientId = c.clientId;
		
		for(EventListener el : eventListeners) {
			if(el instanceof NewClientEventListener) {
				((NewClientEventListener) el).triggerEvent (args);
			}
		}
	}
	
	private void triggerClientDataEvent(Client c, byte[] data) {
		ClientDataEventArgs args = new ClientDataEventArgs ();
		args.clientId = c.clientId;
		args.data = data;
		
		for(EventListener el : eventListeners) {
			if(el instanceof ClientDataEventListener) {
				((ClientDataEventListener) el).triggerEvent (args);
			}
		}
	}
	
	private static final byte[] MSG_PING = {0x00, 0x01};
	
	private void sendData(Client c, byte[] userbytes) {
		byte[] data = {
				(byte) 0xEE, (byte) 0xEE, (byte) 0xEE, (byte) 0xEE,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00,
				(byte) 0x77, (byte) 0x77, (byte) 0x77, (byte) 0x77};
		
		System.arraycopy (userbytes, 0, data, 12, Math.min (2, userbytes.length));
		
		byte[] millis = longToBytes(millis());
		System.arraycopy (millis, 0, data, 4, Math.min (8, millis.length));
		
		try {
			if(c.output == null)
				return;
			c.output.write (data);
			c.output.flush();
		} catch (IOException ex) {
			throw new RuntimeException (ex);
		}
	}
	
	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}
	
	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(bytes);
		buffer.flip();//need flip
		return buffer.getLong();
	}
	
	@Override
	public void startModule () {
		if(threadAcceptingClients == null || threadAcceptingClients.isAlive () || threadHandlingClients == null || threadHandlingClients.isAlive ())
			return;
		this.threadAcceptingClients.start ();
		this.threadHandlingClients.start ();
	}
	
	@Override
	public void stopModule () {
		stopThread = true;
	}
	
	public boolean isRunning () {
		return runningAcceptingClients || runningHandlingClients;
	}
}
