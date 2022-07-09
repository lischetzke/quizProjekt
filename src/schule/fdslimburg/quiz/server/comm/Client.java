package schule.fdslimburg.quiz.server.comm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	private static int clientIdCounter = 0;
	protected Socket client;
	protected int clientId;
	public BufferedInputStream input;
	public BufferedOutputStream output;
	
	public Client(Socket conn) throws IOException {
		this.client = conn;
		synchronized (this) {
			this.clientId = clientIdCounter++;
		}
		this.input = new BufferedInputStream (conn.getInputStream ());
		this.output = new BufferedOutputStream (conn.getOutputStream ());
	}
	
	public void close() throws IOException {
		input.close();
		output.close();
		client.close();
	}
}
