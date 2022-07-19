package schule.fdslimburg.quiz.server.comm;

import javafx.application.Platform;
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
	private final List<Client> clients = new ArrayList<> ();
	public List<EventListener> eventListeners = new ArrayList<> ();
	
	public Communication (int port) {
		try {
			this.ss = new ServerSocket (port);
			this.threadAcceptingClients = new Thread (new RunnableAcceptingClient(this));
		} catch (IOException e) {
			System.err.println ("Couldn't open port, abort!");
			System.err.println (e.getMessage ());
		}
	}
	
	class RunnableAcceptingClient implements Runnable {
		private Communication comm;
		
		public RunnableAcceptingClient(Communication comm) {
			this.comm = comm;
		}
		
		@Override
		public void run () {
			runningAcceptingClients = true;
			System.out.println ("Started RunnableAcceptingClient");
			
			while (!stopThread) {
				try {
					Socket newClient = ss.accept ();
					System.out.println ("New client");
					Client c = new Client (comm, newClient);
					synchronized (clients) {
						clients.add (c);
					}
					System.out.println ("Client added");
					triggerNewClientEvent (c);
				} catch (IOException e) {
					if(e.getMessage ().equals ("Accept timed out"))
						continue;
					System.err.println ("Something happened, that shouldn't happen, oops.");
					System.err.println (e.getMessage ());
				}
				try {
					Thread.sleep (1000);
				} catch (InterruptedException e) {
					throw new RuntimeException (e);
				}
			}
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				throw new RuntimeException (e);
			}
			System.out.println ("Stopping RunnableAcceptingClient");
			
			// Close everything
			synchronized (clients) {
				for (Client c : clients) {
					try {
						c.close ();
					} catch (IOException | InterruptedException e) {
						System.err.println ("Client broken.");
						System.err.println (e.getMessage ());
					}
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
	
	public void addNewClientEventListener(NewClientEventListener el) {
		eventListeners.add (el);
	}
	
	public void addClientDataEventListener(ClientDataEventListener el) {
		eventListeners.add (el);
	}
	
	public void triggerNewClientEvent(Client c) {
		NewClientEventArgs args = new NewClientEventArgs ();
		args.clientId = c.clientId;
		
		for(EventListener el : eventListeners) {
			if(el instanceof NewClientEventListener) {
				Platform.runLater (() -> ((NewClientEventListener) el).triggerEvent (args));
			}
		}
	}
	
	public void triggerClientDataEvent(Client c, NetData data) {
		ClientDataEventArgs args = new ClientDataEventArgs ();
		args.clientId = c.clientId;
		args.data = data;
		
		for(EventListener el : eventListeners) {
			if(el instanceof ClientDataEventListener) {
				Platform.runLater (() -> ((ClientDataEventListener) el).triggerEvent (args));
			}
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
	
	public void addClientToRemove(Client c) {
		// synchronized
		synchronized (clients) {
			clients.remove (c);
		}
	}
	
	@Override
	public void startModule () {
		if(threadAcceptingClients == null || threadAcceptingClients.isAlive ())
			return;
		this.threadAcceptingClients.start ();
	}
	
	@Override
	public void stopModule () {
		stopThread = true;
	}
	
	public boolean isRunning () {
		boolean runningHandlingClients = false;
		
		for (Client c : clients) {
			if(!c.runningHandlingClient)
				continue;
			
			runningHandlingClients = true;
			break;
		}
		
		return runningAcceptingClients || runningHandlingClients;
	}
}
