package schule.fdslimburg.quiz.client;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

public class Client {
	static Socket client;
	static BufferedWriter out;
	static BufferedReader in;
	
	static Thread threadConnectionStatus;
	static Thread threadIO;
	
	static boolean killConn = false;
	static long lastPingSent = 0L;
	static long lastPingReceived = 0L;
	
	public static void main (String[] args) {
		threadConnectionStatus = new Thread (() -> {
			while (true) {
				try {
					if (client != null) {
						if (client.isConnected () && !killConn) {
							if ((lastPingSent + 1000) < millis ()) {
								sendData ("0001");
								lastPingSent = millis ();
							}
						}
					}
					
					if (client != null && lastPingReceived != 0 && (lastPingReceived + 5000) < millis ()) {
						killConn = true;
						try {
							if (in != null) {
								in.close ();
								in = null;
							}
							if (out != null) {
								out.close ();
								out = null;
							}
							if (client != null) {
								client.close ();
								client = null;
							}
						} catch (Exception ignored) {}
						connect();
					}
					
					Thread.sleep (200);
				} catch (Exception ignored) {}
			}
		});
		threadConnectionStatus.start ();
		threadIO = new Thread (() -> {
			while (true) {
				try {
					if (client != null && in != null) {
						if (in.ready ()) {
							Thread.sleep (5);
						}
						
						while (in.ready ()) {
							String data = in.readLine ();
							processData (data, true);
						}
					}
					
					Thread.sleep (200);
				} catch (Exception ignored) {}
			}
		});
		// do your GUI stuff here
		threadIO.start ();
		connect();
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
		killConn = false;
		lastPingSent = 0L;
		lastPingReceived = 0L;
		
		try {
			String[] server = { "172.24.1.1", "5555" };
			client = new Socket (server[0], Integer.parseInt (server[1]));
			out = new BufferedWriter (new PrintWriter (client.getOutputStream ()));
			in = new BufferedReader (new InputStreamReader (client.getInputStream ()));
		} catch (Exception ignored) {}
	}
	
	private static void buzz() {
		boolean successSent = sendData ("0002");
		if(!successSent) {
			killConn = true;
			try {
				if(in != null) {
					in.close ();
					in = null;
				}
				if(out != null) {
					out.close ();
					out = null;
				}
				if(client != null) {
					client.close ();
					client = null;
				}
			} catch(Exception e2) {}
			connect();
		}
	}
	
	private static boolean sendData(String userdata) {
		String millis = millis() + "";
		
		processData(millis + ";" + userdata, false);
		
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
	
	private static String getDate() {
		Date date = new Date();
		Calendar now = GregorianCalendar.getInstance();
		now.setTime (date);
		StringBuilder sb = new StringBuilder ();
		sb
				.append (pad(now.get(Calendar.HOUR_OF_DAY)))
				.append (":")
				.append (pad(now.get(Calendar.MINUTE)))
				.append (":")
				.append (pad(now.get (Calendar.SECOND)));
		return sb.toString ();
	}
	
	public static String pad(int v) {
		StringBuilder sb = new StringBuilder ();
		if(v < 10)
			sb.append ("0");
		sb.append (v);
		return sb.toString ();
	}
	
	public static long millis() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTimeInMillis();
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	
	public static String bytesToHex (byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static byte[] hexToBytes (String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
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
}
