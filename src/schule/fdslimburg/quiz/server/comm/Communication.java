package schule.fdslimburg.quiz.server.comm;

import schule.fdslimburg.quiz.server.IControl;
import schule.fdslimburg.quiz.server.events.ClientDataEventArgs;
import schule.fdslimburg.quiz.server.events.ClientDataEventListener;
import schule.fdslimburg.quiz.server.events.NewClientEventArgs;
import schule.fdslimburg.quiz.server.events.NewClientEventListener;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
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

public class Communication implements Runnable, IControl {
	private ServerSocket ss;
	private Thread backgroundTask;
	private boolean stopThread = false;
	private boolean running = false;
	private List<Client> clients = new ArrayList<> ();
	private boolean waitingClients = true;
	public List<EventListener> eventListeners = new ArrayList<> ();
	
	public Communication (int port) {
		try {
			this.ss = new ServerSocket (port);
			this.ss.setSoTimeout (500);
			this.backgroundTask = new Thread (this);
		} catch (IOException e) {
			System.err.println ("Couldn't open port, abort!");
			System.err.println (e.getMessage ());
		}
	}
	
	@Override
	public void run () {
		while (!stopThread) {
			if (waitingClients) {
				try {
					Socket newClient = ss.accept ();
					Client c = new Client (newClient);
					clients.add (c);
					triggerNewClientEvent (c);
				} catch (IOException e) {
					System.err.println ("Something happened, that shouldn't happen, oops.");
					System.err.println (e.getMessage ());
				}
				continue;
			}
			
			// TODO: Serve clients
			for (Client c : clients) {
				try {
					if (c.input.available () < 1)
						continue;
					
					Thread.sleep (5);
					
					// TODO: Read all bytes from client and temporary save data
					/*
					 * Network data structure (same data as answer from server to client):
					 * 8 bit: Starting sequence
					 * 64 bit: timestamp in millis
					 * 8 bit: Data
					 *     4 bit: unused
					 *     1 bit: Answer wrong
					 *     1 bit: Answer correct
					 *     1 bit: Pressed enter
					 *     1 bit: Ping
					 * 8 bit: CRC8 of timestamp and data
					 * 8 bit: End sequence
					 */
					List<java.lang.Character> data = new ArrayList<> ();
					while(c.input.available () > 0) {
						data.add((char) c.input.read ());
					}
					char[] data2 = new char[data.size ()];
					for(int i = 0; i < data.size (); i++) {
						data2[i] = data.get (i);
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
		running = false;
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
	
	private void triggerClientDataEvent(Client c, char[] data) {
		ClientDataEventArgs args = new ClientDataEventArgs ();
		args.clientId = c.clientId;
		args.data = data;
		
		for(EventListener el : eventListeners) {
			if(el instanceof ClientDataEventListener) {
				((ClientDataEventListener) el).triggerEvent (args);
			}
		}
	}
	
	@Override
	public void startModule () {
		if(backgroundTask == null || backgroundTask.isAlive ())
			return;
		this.backgroundTask.start ();
		this.running = true;
	}
	
	@Override
	public void stopModule () {
		stopThread = true;
	}
	
	public boolean isRunning () {
		return running && backgroundTask.isAlive ();
	}
}
