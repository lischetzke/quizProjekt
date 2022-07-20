package schule.fdslimburg.quiz.server.scenes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import schule.fdslimburg.quiz.server.Game;
import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.events.NewClientEventArgs;
import schule.fdslimburg.quiz.server.events.NewClientEventListener;
import schule.fdslimburg.quiz.server.scenes.tableHelper.TableColumnClient;

import java.util.Dictionary;
import java.util.Map;

public class SceneJoin extends AScene implements NewClientEventListener {
	// Titel "Warte auf Spieler..."
	private Label lb_title;
	// Tabelle "alle Clients + aktuellen Namen"
	private TableView<TableColumnClient> tv_clients;
	private TableColumn<TableColumnClient, Integer> tc_cid;
	private TableColumn<TableColumnClient, String> tc_cname;
	// Eingabe "neuer Name"
	private Label lb_newname;
	private TextField tf_newname;
	// Button "Übernehmen vom neuen Namen"
	private Button btn_save;
	// Button "Nächste Szene" -> "Sperre neue Spieler" -> Setze Punkteverteilung -> Bereite Scoreboard vor
	private Button btn_nextscene;
	
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		this.backend.comm.addNewClientEventListener (this);
		this.backend.game = new Game (backend);
		Border border = new Border(new BorderStroke(Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		
		// Create
		root = new Pane();
		lb_title = new Label("Warte auf Spieler...");
		tv_clients = new TableView<> ();
		tc_cid = new TableColumn<> ("ID");
		tc_cname = new TableColumn<> ("Name");
		lb_newname = new Label ("Neuer Clientname:");
		tf_newname = new TextField ();
		btn_save = new Button ("Speichern");
		btn_nextscene = new Button("Fertig!");
		
		// Set
		root.setBackground (bg);
		
		lb_title.setTextFill (Style.FOREGROUND.value ());
		lb_title.setFont (Font.font ("Consolas", 48));
		
		tv_clients.setStyle ("-fx-background-color: black; -fx-text-fill: white; -fx-font-size: 24pt;");
		tv_clients.setBorder (border);
		tc_cid.setCellValueFactory (new PropertyValueFactory<> ("ID"));
		tc_cid.prefWidthProperty ().bind (tv_clients.widthProperty ().multiply (0.15));
		tc_cname.setCellValueFactory (new PropertyValueFactory<> ("Name"));
		tc_cname.prefWidthProperty ().bind (tv_clients.widthProperty ().multiply (0.80));
		tv_clients.setEditable (false);
		tv_clients.getSelectionModel ().setSelectionMode (SelectionMode.SINGLE);
		tv_clients.getColumns ().add (tc_cid);
		tv_clients.getColumns ().add (tc_cname);
		this.backend.clientNames.keys ().asIterator ().forEachRemaining (id -> {
			tv_clients.getItems ().add (new TableColumnClient (id, this.backend.clientNames.get (id)));
		});
		
		lb_newname.setTextFill (Style.FOREGROUND.value ());
		lb_newname.setFont (Font.font ("Consolas", 48));
		tf_newname.setStyle ("-fx-text-fill: #ffffff;");
		tf_newname.setBackground (bg);
		tf_newname.setBorder (border);
		tf_newname.setFont (Font.font ("Consolas", 48));
		int maxLength = 32;
		tf_newname.textProperty().addListener((ov, oldValue, newValue) -> {
			if (tf_newname.getText().length() > maxLength) {
				String s = tf_newname.getText().substring(0, maxLength);
				tf_newname.setText(s);
			}
		});
		
		btn_save.setBackground (bg);
		btn_save.setTextFill (Style.FOREGROUND.value ());
		btn_save.setBorder (border);
		btn_save.setFont (Font.font ("Consolas", 48));
		
		btn_nextscene.setBackground (bg);
		btn_nextscene.setTextFill (Style.FOREGROUND.value ());
		btn_nextscene.setBorder (border);
		btn_nextscene.setFont (Font.font ("Consolas", 48));
		
		// Actions
		btn_save.setOnAction (e -> {
			// Get selection
			TableColumnClient tcc = tv_clients.getSelectionModel ().getSelectedItem ();
			if(tcc == null || tf_newname.getText ().equals (""))
				return;
			
			this.backend.clientNames.put (tcc.getID (), tf_newname.getText ());
			tcc.setName (tf_newname.getText ());
		});
		
		btn_nextscene.setOnAction (e -> {
			// Save player
			this.backend.clientNames.keys ().asIterator ().forEachRemaining (key -> {
				this.backend.game.player.put (key, this.backend.clientNames.get (key));
			});
			
			sceneStatus = SceneStatus.SCENE_NEXT;
		});
		
		// Add
		root.getChildren ().add (lb_title);
		root.getChildren ().add (tv_clients);
		root.getChildren ().add (lb_newname);
		root.getChildren ().add (tf_newname);
		root.getChildren ().add (btn_save);
		root.getChildren ().add (btn_nextscene);
		
		// Positions
		lb_title.setAlignment (Pos.CENTER);
		lb_title.setMinWidth (1920);
		lb_title.setMinHeight (180);
		lb_title.setLayoutX (0);
		lb_title.setLayoutY (0);
		
		tv_clients.setMinWidth (480);
		tv_clients.setMinHeight (520);
		tv_clients.setLayoutX (360);
		tv_clients.setLayoutY (400);
		
		lb_newname.setMinWidth (480);
		lb_newname.setMinHeight (80);
		lb_newname.setLayoutX (1080);
		lb_newname.setLayoutY (400);
		
		tf_newname.setMinWidth (480);
		tf_newname.setMinHeight (80);
		tf_newname.setLayoutX (1080);
		tf_newname.setLayoutY (520);
		
		btn_save.setMinWidth (480);
		btn_save.setMinHeight (80);
		btn_save.setLayoutX (1080);
		btn_save.setLayoutY (660);
		
		btn_nextscene.setMinWidth (480);
		btn_nextscene.setMinHeight (80);
		btn_nextscene.setLayoutX (1080);
		btn_nextscene.setLayoutY (800);
	}
	
	@Override
	public void updateScene () {
		// Update Client table
	}
	
	@Override
	public void resetScene () {
		this.backend.game.reset();
	}
	
	@Override
	public void triggerEvent (NewClientEventArgs ea) {
		// Update UI with new clientNames list
		boolean exists = false;
		
		for(TableColumnClient tcc : tv_clients.getItems ()) {
			if(tcc.getID () == ea.clientId) {
				exists = true;
				break;
			}
		}
		
		if(!exists) {
			try {
				Thread.sleep (20);
			} catch (InterruptedException e) {
				System.out.println (e.getMessage ());
			}
			tv_clients.getItems ().add (new TableColumnClient (ea.clientId, "Client " + ea.clientId + " (" + ea.clientAddress.toString () + ")"));
		}
	}
}
