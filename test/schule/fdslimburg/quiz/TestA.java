package schule.fdslimburg.quiz;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TestA {
	@Test
	public void testExec() throws Exception {
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
