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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class DevClientV2 extends Application {
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
	Label lbAllData;
	Label lbStatus;
	String lbStatus_text;
	Paint lbStatus_paint;
	TextField tfInputServer;
	Button btnConnect;
	Button btnBuzzer;
	
	Socket client;
	DataOutputStream out;
	BufferedReader in;
	
	Thread threadConnectionStatus;
	Thread threadIO;
	Thread threadUpdateUI;
	boolean updateUI = false;
	
	long lastPing = 0;
	
	@Override
	public void start (Stage stage) throws IOException {
		threadConnectionStatus = new Thread (() -> {
			while (true) {
				try {
					if (client != null && lbStatus != null) {
						if (client.isConnected () && (lastPing + 1000) >= millis()) {
							lbStatus_text = "Connected!";
							lbStatus_paint = Color.LIGHTGREEN;
							updateUI = true;
							sendData ("0001");
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
					
					if(client != null && (lastPing + 2000) < millis()) {
						System.out.println ("Connection seems dead, closing now");
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
						} catch(Exception e) {}
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
					out = new DataOutputStream (client.getOutputStream ());
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
		
		btnBuzzer = new Button ("BUZZ");
		btnBuzzer.setFont (fontMonospaced);
		btnBuzzer.setOnAction (e -> {
			/*
			 * Network data structure (same data as answer from server to client):
			 * 32 bit: Starting sequence
			 *          1110 1110 1110 1110 / 0xEEEE
			 * 64 bit: timestamp in millis
			 * 16 bit: Data
			 *    10 bit: unused
			 *     1 bit: Answer wrong
			 *     1 bit: Answer correct
			 *     1 bit: Reset light
			 *     1 bit: Set Light
			 *     1 bit: Pressed enter
			 *     1 bit: Ping
			 * 32 bit: End sequence
			 *          0111 0111 0111 0111 / 0x7777
			 */
			sendData ("0002");
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
		
		btnBuzzer.setMaxWidth (Double.MAX_VALUE);
		gridPane.add (btnBuzzer, 0, 4, 2, 1);
		GridPane.setFillWidth (btnBuzzer, true);
		
		gridPane.add (lbAllData, 0, 7, 2, 1);
		gridPane.add (taComm, 0, 8, 6, 9);
		
		VBox container = new VBox ();
		container.setFillWidth (true);
		container.getChildren ().addAll (gridPane);
		
		Scene scene = new Scene (container);
		
		stage.setTitle ("Quiz: Devclient v2");
		stage.setResizable (false);
		stage.setScene (scene);
		stage.show ();
	}
	
	private void sendData(String userdata) {
		byte[] data = {
				(byte) 0xEE, (byte) 0xEE, (byte) 0xEE, (byte) 0xEE,
				(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
				(byte) 0x00, (byte) 0x00,
				(byte) 0x77, (byte) 0x77, (byte) 0x77, (byte) 0x77};
		
		byte[] userbytes = hexToBytes(userdata);
		System.arraycopy (userbytes, 0, data, 12, Math.min (2, userbytes.length));
		
		byte[] millis = longToBytes(millis());
		System.arraycopy (millis, 0, data, 4, Math.min (8, millis.length));
		
		try {
			out.write (data);
		} catch (IOException ex) {
			throw new RuntimeException (ex);
		}
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
