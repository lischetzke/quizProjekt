package schule.fdslimburg.quiz.server.comm;

import java.io.*;
import java.net.Socket;
import java.util.Calendar;

public class Client {
	private static int clientIdCounter = 0;
	protected Socket client;
	protected int clientId;
	public BufferedReader input;
	public DataOutputStream output;
	public long lastPingSent = 0L;
	public long lastPingReceived = 0L;
	
	public Client(Socket conn) throws IOException {
		this.client = conn;
		synchronized (this) {
			this.clientId = clientIdCounter++;
		}
		this.input = new BufferedReader (new InputStreamReader (conn.getInputStream ()));
		this.output = new DataOutputStream (conn.getOutputStream ());
	}
	
	public void close() throws IOException {
		input.close();
		output.close();
		client.close();
	}
	
	public static long millis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
}
