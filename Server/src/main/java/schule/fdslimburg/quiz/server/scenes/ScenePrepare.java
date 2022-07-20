package schule.fdslimburg.quiz.server.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.comm.NetStatus;
import schule.fdslimburg.quiz.server.events.ClientDataEventArgs;
import schule.fdslimburg.quiz.server.events.ClientDataEventListener;

import java.util.ArrayList;
import java.util.List;

public class ScenePrepare extends AScene implements ClientDataEventListener {
	
	// TODO: Select questionamount to use
	// TODO: Prepare Button (before Go! Button) start randomizer
	
	private Label lb_title;
	private List<Label> lb_clients;
	private List<Button> btn_clientstatus;
	private Label lb_questionamount;
	private Button btn_qless;
	private Label lb_qamount;
	private Button btn_qmore;
	private Button btn_prepare;
	private Button btn_nextscene;
	
	private boolean randomize = false;
	
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		this.backend.comm.addClientDataEventListener (this);
		Border border = new Border(new BorderStroke (Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		
		// Create
		root = new Pane ();
		lb_title = new Label ("Einstellungen");
		lb_clients = new ArrayList<> ();
		btn_clientstatus = new ArrayList<> ();
		lb_questionamount = new Label ("Fragen im Spiel:");
		btn_qless = new Button ("<");
		lb_qamount = new Label ("" + this.backend.game.questionAmount);
		btn_qmore = new Button (">");
		btn_prepare = new Button ("Spiel vorbereiten!");
		btn_nextscene = new Button ("Go!");
		
		// Set
		root.setBackground (bg);
		
		lb_title.setTextFill (Style.FOREGROUND.value ());
		lb_title.setFont (Font.font ("Consolas", 48));
		
		lb_questionamount.setTextFill (Style.FOREGROUND.value ());
		lb_questionamount.setFont (Font.font ("Consolas", 48));
		btn_qless.setBackground (bg);
		btn_qless.setTextFill (Style.FOREGROUND.value ());
		btn_qless.setBorder (border);
		btn_qless.setFont (Font.font ("Consolas", 48));
		lb_qamount.setTextFill (Style.FOREGROUND.value ());
		lb_qamount.setFont (Font.font ("Consolas", 48));
		btn_qmore.setBackground (bg);
		btn_qmore.setTextFill (Style.FOREGROUND.value ());
		btn_qmore.setBorder (border);
		btn_qmore.setFont (Font.font ("Consolas", 48));
		
		btn_prepare.setBackground (bg);
		btn_prepare.setTextFill (Style.FOREGROUND.value ());
		btn_prepare.setBorder (border);
		btn_prepare.setFont (Font.font ("Consolas", 48));
		
		btn_nextscene.setBackground (bg);
		btn_nextscene.setTextFill (Style.RED.value ());
		btn_nextscene.setBorder (border);
		btn_nextscene.setFont (Font.font ("Consolas", 48));
		btn_nextscene.setDisable (true);
		
		// Actions
		btn_qless.setOnAction (e -> {
			// Not lower than 5
			this.backend.game.questionAmount = Math.max(5, this.backend.game.questionAmount - 1);
		});
		
		btn_qmore.setOnAction (e -> {
			// Not more than questionamount
			this.backend.game.questionAmount = Math.min(this.backend.game.questionpack.questions.size (), this.backend.game.questionAmount + 1);
		});
		
		btn_prepare.setOnAction (e -> {
			// Start randomize
			btn_qless.setTextFill (Style.RED.value ());
			btn_qless.setDisable (true);
			btn_qmore.setTextFill (Style.RED.value ());
			btn_qmore.setDisable (true);
			btn_prepare.setTextFill (Style.RED.value ());
			btn_prepare.setDisable (true);
			this.backend.game.prepare ();
			this.randomize = true;
		});
		
		btn_nextscene.setOnAction (e -> {
			this.randomize = false;
			this.sceneStatus = SceneStatus.SCENE_NEXT;
		});
		
		// Add
		root.getChildren ().add (lb_title);
		root.getChildren ().add (lb_questionamount);
		root.getChildren ().add (btn_qless);
		root.getChildren ().add (lb_qamount);
		root.getChildren ().add (btn_qmore);
		root.getChildren ().add (btn_prepare);
		root.getChildren ().add (btn_nextscene);
		
		// Positions
		lb_title.setAlignment (Pos.CENTER);
		lb_title.setMinWidth (1920);
		lb_title.setMinHeight (180);
		lb_title.setLayoutX (0);
		lb_title.setLayoutY (0);
		
		// Y: 1000 - 1720
		lb_questionamount.setMinWidth (720);
		lb_questionamount.setMinHeight (80);
		lb_questionamount.setLayoutX (1000);
		lb_questionamount.setLayoutY (400);
		
		btn_qless.setTextAlignment (TextAlignment.CENTER);
		btn_qless.setMinWidth (100);
		btn_qless.setMinHeight (100);
		btn_qless.setLayoutX (1000);
		btn_qless.setLayoutY (500);
		
		lb_qamount.setAlignment (Pos.CENTER);
		lb_qamount.setMinWidth (520);
		lb_qamount.setMinHeight (100);
		lb_qamount.setLayoutX (1100);
		lb_qamount.setLayoutY (500);
		
		btn_qmore.setTextAlignment (TextAlignment.CENTER);
		btn_qmore.setMinWidth (100);
		btn_qmore.setMinHeight (100);
		btn_qmore.setLayoutX (1620);
		btn_qmore.setLayoutY (500);
		
		btn_prepare.setMinWidth (720);
		btn_prepare.setMinHeight (80);
		btn_prepare.setLayoutX (1000);
		btn_prepare.setLayoutY (660);
		
		btn_nextscene.setMinWidth (720);
		btn_nextscene.setMinHeight (80);
		btn_nextscene.setLayoutX (1000);
		btn_nextscene.setLayoutY (780);
	}
	
	@Override
	public Pane getRoot () {
		this.backend.game.player.keys ().asIterator ().forEachRemaining (key -> {
			String playername = this.backend.game.player.get (key);
			Label player = new Label (playername);
			lb_clients.add (player);
			Button playerstatus = new Button ("");
			btn_clientstatus.add (playerstatus);
		});
		
		for(Label l : lb_clients) {
			l.setTextFill (Style.FOREGROUND.value ());
			l.setFont (Font.font ("Consolas", 48));
		}
		for(Button b : btn_clientstatus) {
			b.setBackground (new Background (new BackgroundFill (Style.RED.value (), CornerRadii.EMPTY, Insets.EMPTY)));
			b.setBorder (Border.EMPTY);
			b.setDisable (true);
		}
		
		
		root.getChildren ().addAll (lb_clients);
		root.getChildren ().addAll (btn_clientstatus);
		
		// Y: 200 - 920
		for(int i = 0; i < lb_clients.size (); i++) {
			Label l = lb_clients.get (i);
			Button b = btn_clientstatus.get (i);
			
			l.setMinWidth (600);
			l.setMinHeight (80);
			l.setLayoutX (280);
			l.setLayoutY (400 + (i * 80));
			
			b.setMinWidth (40);
			b.setMinHeight (40);
			b.setLayoutX (200);
			b.setLayoutY (420 + (i * 80));
		}
		
		return super.getRoot ();
	}
	
	@Override
	public void updateScene () {
		lb_qamount.setText ("" + this.backend.game.questionAmount);
		
		boolean ready = true;
		for(Button b : btn_clientstatus) {
			if(b.isDisabled ()) {
				ready = false;
				break;
			}
		}
		if(btn_prepare.isDisabled () && ready) {
			btn_nextscene.setTextFill (Style.FOREGROUND.value ());
			btn_nextscene.setDisable (false);
		}
		
		if(this.randomize)
			this.backend.game.prepare ();
	}
	
	@Override
	public void resetScene () {
	
	}
	
	@Override
	public void triggerEvent (ClientDataEventArgs ea) {
		if(ea.data.status == NetStatus.PRESS) {
			// Get Client and set green
			String name = this.backend.clientNames.get (ea.clientId);
			if(name == null)
				name = "Unknown";
			
			for(int i = 0; i < lb_clients.size (); i++) {
				if(lb_clients.get (i).getText ().equals (name)) {
					btn_clientstatus.get (i).setBackground (new Background (new BackgroundFill (Style.GREEN.value (), CornerRadii.EMPTY, Insets.EMPTY)));
					btn_clientstatus.get (i).setDisable (false);
					break;
				}
			}
		}
	}
}
