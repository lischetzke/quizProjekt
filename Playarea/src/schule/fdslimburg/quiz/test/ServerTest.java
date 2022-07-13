package schule.fdslimburg.quiz.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTest {
	public static void main (String[] args) throws IOException {
		ServerSocket ss = new ServerSocket (5555);
		Socket s = ss.accept ();
		System.out.println ("Client connected");
		
		PrintWriter out = new PrintWriter (s.getOutputStream ());
		BufferedReader in = new BufferedReader (new InputStreamReader (s.getInputStream ()));
		while(true) {
			if(in.ready ()) {
				out.println (in.readLine ());
			}
		}
	}
}
