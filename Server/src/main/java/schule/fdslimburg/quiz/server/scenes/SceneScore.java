package schule.fdslimburg.quiz.server.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import schule.fdslimburg.quiz.server.backend.Backend;

import java.util.ArrayList;
import java.util.List;

public class SceneScore extends AScene {
	
	// TODO: Auto point calculation
	//          Correct: +<amount of players>
	//          Wrong: everyone gets one point
	
	private Label lb_title;
	private List<Label> lb_clients;
	private List<Label> lb_scores;
	
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		Border border = new Border(new BorderStroke (Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		
		// Create
		root = new Pane ();
		lb_title = new Label ("Punkteübersicht!");
		lb_clients = new ArrayList<> ();
		lb_scores = new ArrayList<> ();
		
		// Set
		root.setBackground (bg);
		
		lb_title.setTextFill (Style.FOREGROUND.value ());
		lb_title.setFont (Font.font ("Consolas", 48));
		
		// Actions
		
		// Add
		root.getChildren ().add (lb_title);
		
		// Positions
		lb_title.setAlignment (Pos.CENTER);
		lb_title.setMinWidth (1920);
		lb_title.setMinHeight (180);
		lb_title.setLayoutX (0);
		lb_title.setLayoutY (0);
	}
	
	@Override
	public Pane getRoot () {
		this.backend.game.player.keys ().asIterator ().forEachRemaining (key -> {
			String playername = this.backend.game.player.get (key);
			Label player = new Label (playername);
			lb_clients.add (player);
			Label points = new Label ("" + this.backend.game.scores.get (key));
			lb_scores.add (points);
		});
		
		for(Label l : lb_clients) {
			l.setAlignment (Pos.CENTER_RIGHT);
			l.setTextFill (Style.FOREGROUND.value ());
			l.setFont (Font.font ("Consolas", 48));
		}
		for(Label l : lb_scores) {
			l.setAlignment (Pos.CENTER_LEFT);
			l.setTextFill (Style.FOREGROUND.value ());
			l.setFont (Font.font ("Consolas", 48));
		}
		
		root.getChildren ().addAll (lb_clients);
		root.getChildren ().addAll (lb_scores);
		
		// Y: 200 - 920
		for(int i = 0; i < lb_clients.size (); i++) {
			Label l = lb_clients.get (i);
			Label s = lb_scores.get (i);
			
			l.setMinWidth (950);
			l.setMinHeight (60);
			l.setLayoutX (0);
			l.setLayoutY (240 + (i * 80));
			
			s.setMinWidth (950);
			s.setMinHeight (40);
			s.setLayoutX (970);
			s.setLayoutY (240 + (i * 80));
		}
		
		return super.getRoot ();
	}
	
	@Override
	public void updateScene () {
	
	}
	
	@Override
	public void resetScene () {
	
	}
}
