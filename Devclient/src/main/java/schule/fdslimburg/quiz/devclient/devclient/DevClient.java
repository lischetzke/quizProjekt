package schule.fdslimburg.quiz.devclient.devclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.ZoneId;
import java.util.*;

public class DevClient extends Application {
	private static Font fontMonospaced;
	
	public static void main (String[] args) {
		String[] fontsMonospaced = { "Lucida Console", "Consolas", "Courier New", "Monospaced" };
		System.out.println ("FontFamilies:");
		List<String> fonts = Font.getFamilies ();
		for (String f : fonts) {
			System.out.println (f);
		}
		System.out.println ("====================");
		
		for (String s : fontsMonospaced) {
			for (String f : fonts) {
				if (s.equals (f)) {
					fontMonospaced = Font.font (s);
					System.out.println ("Using Monospaced: " + s);
					break;
				}
			}
			if (fontMonospaced != null)
				break;
		}
		
		launch ();
	}
	
	TextArea taComm;
	String taComm_text;
	Label lbServer;
	Label lbSendData;
	Label lbAllData;
	Label lbStatus;
	String lbStatus_text;
	Paint lbStatus_paint;
	TextField tfInputServer;
	TextField tfInputData;
	Button btnConnect;
	Button btnSendData;
	
	Socket client;
	PrintWriter out;
	BufferedReader in;
	
	Thread threadConnectionStatus;
	Thread threadIO;
	Thread threadUpdateUI;
	boolean updateUI = false;
	
	@Override
	public void start (Stage stage) throws IOException {
		threadConnectionStatus = new Thread (() -> {
			while (true) {
				try {
					if (client != null && lbStatus != null) {
						if (client.isConnected ()) {
							lbStatus_text = "Connected!";
							lbStatus_paint = Color.LIGHTGREEN;
							updateUI = true;
						} else {
							lbStatus_text = "Disconnected!";
							lbStatus_paint = Color.RED;
							updateUI = true;
						}
					} else if (lbStatus != null) {
						lbStatus_text = "Disconnected!";
						lbStatus_paint = Color.RED;
						updateUI = true;
					}
					
					Thread.sleep (500);
				} catch (Exception e) {
					System.err.println (e.getMessage ());
					e.printStackTrace ();
				}
			}
		});
		threadConnectionStatus.start ();
		threadIO = new Thread (() -> {
			while (true) {
				try {
					if (client != null && client.isConnected () && in != null) {
						if(in.ready()) {
							Thread.sleep(5);
							int status = 0;
							
							byte[] data = new byte[20];
							
							while (in.ready ()) {
								int raw = in.read ();
								if(raw == -1) // End of stream
									break;
								
								if((status == 0 || status == 1) && raw == 0xEEEE) {
									// Starting sequence
									data[status * 2] = (byte) ((raw & 0xFF00) >> 8);
									data[status * 2 + 1] = (byte) (raw & 0x00FF);
									status++;
								} else if((status == 8 || status == 9) && raw == 0x7777) {
									// Ending sequence
									data[status * 2] = (byte) ((raw & 0xFF00) >> 8);
									data[status * 2 + 1] = (byte) (raw & 0x00FF);
									status++;
								} else {
									// Data
									data[status * 2] = (byte) ((raw & 0xFF00) >> 8);
									data[status * 2 + 1] = (byte) (raw & 0x00FF);
									status++;
								}
								
								if(status == 10) {
									// Data received
									processData(data, true);
								}
							}
						}
					}
					
					Thread.sleep (50);
				} catch (Exception e) {
					System.err.println (e.getMessage ());
					e.printStackTrace ();
				}
			}
		});
		// do your GUI stuff here
		threadIO.start ();
		threadUpdateUI = new Thread (() -> {
			while(true) {
				if(updateUI) {
					updateUI = false;
					Platform.runLater(() -> {
						lbStatus.setText (lbStatus_text);
						lbStatus.setTextFill (lbStatus_paint);
						taComm.setText (taComm_text);
					});
				}
				try {
					Thread.sleep (100);
				} catch (InterruptedException e) {
					throw new RuntimeException (e);
				}
			}
		});
		threadUpdateUI.start ();
		
		lbServer = new Label ("Server:");
		lbServer.setFont (fontMonospaced);
		tfInputServer = new TextField ();
		tfInputServer.setFont (fontMonospaced);
		btnConnect = new Button ("Connect!");
		btnConnect.setFont (fontMonospaced);
		btnConnect.setOnAction (e -> {
			System.out.println ("Init connection");
			
			try {
				if (client != null) {
					System.out.println ("Disconnecting");
					client.close ();
					btnConnect.setText ("Connect!");
				} else {
					String[] server = { tfInputServer.getText ().split (":")[0], "5555" };
					if (tfInputServer.getText ().contains (":"))
						server[1] = tfInputServer.getText ().split (":")[1];
					System.out.println ("Connecting to " + server[0] + " : " + server[1]);
					client = new Socket (server[0], Integer.parseInt (server[1]));
					System.out.println ("Connected.");
					out = new PrintWriter (client.getOutputStream (), true);
					in = new BufferedReader (new InputStreamReader (client.getInputStream ()));
					
					btnConnect.setText ("Disconnect!");
				}
			} catch (IOException ex) {
				throw new RuntimeException (ex);
			}
		});
		
		lbStatus = new Label ("Not connected!");
		lbStatus.setFont (fontMonospaced);
		lbStatus.setTextFill (Color.RED);
		
		lbSendData = new Label ("Data:");
		lbSendData.setFont (fontMonospaced);
		tfInputData = new TextField ();
		tfInputData.setFont (fontMonospaced);
		List<Character> allowedChars = List.of ('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F');
		tfInputData.setTextFormatter (new TextFormatter<> ((change) -> {
			String text = change.getText ().toUpperCase ();
			StringBuilder out = new StringBuilder ();
			
			// only leave allowed character
			for (char s1 : text.toCharArray ()) {
				boolean allowed = false;
				for (char s2 : allowedChars) {
					if (s1 == s2) {
						allowed = true;
						break;
					}
				}
				if (allowed) {
					out.append (s1);
				}
			}
			
			change.setText (out.toString ());
			return change;
		}));
		btnSendData = new Button ("Send!");
		btnSendData.setFont (fontMonospaced);
		btnSendData.setOnAction (e -> {
			//byte[] data = {0xEE, 0xEE, };
		});
		
		lbAllData = new Label ("Communication:");
		lbAllData.setFont (fontMonospaced);
		
		taComm = new TextArea ();
		taComm.setFont (fontMonospaced);
		taComm.setEditable (false);
		
		//====================
		GridPane gridPane = new GridPane ();
		
		gridPane.setMinSize (520, 600);
		gridPane.setMaxSize (520, 600);
		gridPane.setPadding (new Insets (10, 10, 10, 10));
		gridPane.setVgap (5);
		gridPane.setHgap (5);
		gridPane.setAlignment (Pos.CENTER);
		
		for (int rowIndex = 0; rowIndex < 17; rowIndex++) {
			RowConstraints rc = new RowConstraints ();
			rc.setVgrow (Priority.ALWAYS);
			rc.setFillHeight (true);
			gridPane.getRowConstraints ().add (rc);
		}
		for (int colIndex = 0; colIndex < 6; colIndex++) {
			ColumnConstraints cc = new ColumnConstraints ();
			cc.setHgrow (Priority.ALWAYS);
			cc.setFillWidth (true);
			gridPane.getColumnConstraints ().add (cc);
		}
		
		gridPane.add (lbServer, 0, 0, 2, 1);
		gridPane.add (tfInputServer, 1, 0, 3, 1);
		btnConnect.setMaxWidth (Double.MAX_VALUE);
		gridPane.add (btnConnect, 4, 0, 2, 1);
		GridPane.setFillWidth (btnConnect, true);
		
		gridPane.add (lbStatus, 0, 1, 2, 1);
		
		gridPane.add (lbSendData, 0, 4, 2, 1);
		gridPane.add (tfInputData, 1, 4, 3, 1);
		btnSendData.setMaxWidth (Double.MAX_VALUE);
		gridPane.add (btnSendData, 4, 4, 2, 1);
		GridPane.setFillWidth (btnSendData, true);
		
		gridPane.add (lbAllData, 0, 7, 2, 1);
		gridPane.add (taComm, 0, 8, 6, 9);
		
		VBox container = new VBox ();
		//container.setMinSize (524, 600);
		container.setFillWidth (true);
		container.getChildren ().addAll (gridPane);
		
		Scene scene = new Scene (container);
		
		stage.setTitle ("Quiz: Devclient");
		stage.setResizable (false);
		//stage.setMinHeight (600);
		//stage.setMinWidth (524);
		//stage.setMaxHeight (600);
		//stage.setMaxWidth (524);
		stage.setScene (scene);
		stage.show ();
	}
	
	private void processData(byte[] data, boolean server) {
		String hex = bytesToHex(data);
		output(hex, server);
	}
	
	private void output(String hex, boolean server) {
		System.out.println ((server ? "Server: " : "Client: ") + hex);
		
		taComm_text = getDate() + " - " + (server ? "S: " : "C: ") + hex + "\n" + taComm.getText ();
		updateUI = true;
	}
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	private String getDate() {
		Date date = new Date();
		Calendar now = GregorianCalendar.getInstance();
		now.setTime (date);
		StringBuilder sb = new StringBuilder ();
		sb
				.append (now.get(Calendar.HOUR_OF_DAY))
				.append (":")
				.append (now.get(Calendar.MINUTE))
				.append (":")
				.append (now.get (Calendar.SECOND));
		return sb.toString ();
	}
}
