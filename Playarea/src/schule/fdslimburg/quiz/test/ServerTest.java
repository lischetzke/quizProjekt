package schule.fdslimburg.quiz.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
	public static void main (String[] args) throws IOException, InterruptedException {
		ServerSocket ss = new ServerSocket (5555);
		Socket s = ss.accept ();
		System.out.println ("Client connected");
		
		PrintWriter out = new PrintWriter (s.getOutputStream ());
		BufferedReader in = new BufferedReader (new InputStreamReader (s.getInputStream ()));
		
		byte[] data = new byte[18];
		int counterdata = 0;
		
		while(true) {
			if(in.ready()) {
				Thread.sleep(5);
			}
			while(in.ready ()) {
				String datain = in.readLine();
				parseData (datain);
			}
		}
	}
	
	public static NetData parseData(String data) {
		System.out.println ("Data: " + data);
		String[] arrData = data.split(";");
		if(arrData.length < 2)
			return null;
		System.out.println (arrData[0]);
		System.out.println (arrData[1]);
		NetData n = new NetData();
		n.timestamp = Long.parseLong (arrData[0]);
		n.status = NetStatus.getValue (Integer.parseInt(arrData[1]));
		return n;
	}
	
	public static void processData(byte[] data) {
		System.out.println (bytesToHex (data));
		// start
		byte[] dataNew = new byte[10];
		int stage = 0;
		for(int i = 0; i < data.length; i++) {
			switch(stage) {
				case 0:
					// ignore 0xEE
					break;
				case 1:
					// Read 10 bytes
					break;
				case 2:
					// ignore 0x77
					break;
				default:
					// ignore
					break;
			}
		}
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
	
}
