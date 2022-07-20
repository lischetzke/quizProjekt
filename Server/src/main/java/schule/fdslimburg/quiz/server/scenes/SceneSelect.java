package schule.fdslimburg.quiz.server.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.backend.data.Data;

public class SceneSelect extends AScene {
	private Label lb_title;
	private ComboBox<String> cb_questionpacks;
	private Label lb_questionspacks;
	private Label lb_questionspacksamount;
	private Label lb_questions;
	private Label lb_questionsamount;
	private Button btn_save;
	private Button btn_nextscene;
	
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		Border border = new Border(new BorderStroke(Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		
		// Create
		root = new Pane();
		lb_title = new Label ("Fragenpaket auswählen...");
		cb_questionpacks = new ComboBox<> ();
		lb_questionspacks = new Label ("Anzahl Fragenpakete:");
		lb_questionspacksamount = new Label ("" + this.backend.allQuestions.size ());
		lb_questions = new Label ("Anzahl Fragen:");
		lb_questionsamount = new Label ("< Erst Pack auswählen >");
		btn_save = new Button ("Auswählen");
		btn_nextscene = new Button ("Fertig!");
		
		// Set
		root.setBackground (bg);
		
		lb_title.setTextFill (Style.FOREGROUND.value ());
		lb_title.setFont (Font.font ("Consolas", 48));
		
		cb_questionpacks.setStyle("-fx-font: 48px \"Consolas\";");
		cb_questionpacks.setBackground (bg);
		cb_questionpacks.setBorder (border);
		for(Data d : this.backend.allQuestions) {
			cb_questionpacks.getItems ().add (d.name);
		}
		
		lb_questionspacks.setTextFill (Style.FOREGROUND.value ());
		lb_questionspacks.setFont (Font.font ("Consolas", 48));
		lb_questionspacksamount.setTextFill (Style.FOREGROUND.value ());
		lb_questionspacksamount.setFont (Font.font ("Consolas", 48));
		lb_questions.setTextFill (Style.FOREGROUND.value ());
		lb_questions.setFont (Font.font ("Consolas", 48));
		lb_questionsamount.setTextFill (Style.FOREGROUND.value ());
		lb_questionsamount.setFont (Font.font ("Consolas", 48));
		
		btn_save.setBackground (bg);
		btn_save.setTextFill (Style.FOREGROUND.value ());
		btn_save.setBorder (border);
		btn_save.setFont (Font.font ("Consolas", 48));
		
		btn_nextscene.setBackground (bg);
		btn_nextscene.setTextFill (Style.RED.value ());
		btn_nextscene.setBorder (border);
		btn_nextscene.setFont (Font.font ("Consolas", 48));
		btn_nextscene.setDisable (true);
		
		// Actions
		cb_questionpacks.setOnAction (e -> {
			String pack = cb_questionpacks.getSelectionModel ().getSelectedItem ();
			if(pack == null) {
				lb_questionsamount.setText ("< Erst Pack auswählen >");
			} else {
				for(Data d : this.backend.allQuestions) {
					if(d.name.equals (pack)) {
						lb_questionsamount.setText ("" + d.questions.size ());
						break;
					}
				}
			}
		});
		
		btn_save.setOnAction (e -> {
			String pack = cb_questionpacks.getSelectionModel ().getSelectedItem ();
			Data questionpack = null;
			
			if(pack == null)
				return;
			
			for(Data d : this.backend.allQuestions) {
				if(d.name.equals (pack)) {
					questionpack = d;
					break;
				}
			}
			
			if(questionpack == null)
				return;
			
			this.backend.game.questionpack = questionpack;
			btn_nextscene.setTextFill (Style.FOREGROUND.value ());
			btn_nextscene.setDisable (false);
		});
		
		btn_nextscene.setOnAction (e -> {
			sceneStatus = SceneStatus.SCENE_NEXT;
		});
		
		// Add
		root.getChildren ().add (lb_title);
		root.getChildren ().add (cb_questionpacks);
		root.getChildren ().add (lb_questionspacks);
		root.getChildren ().add (lb_questionspacksamount);
		root.getChildren ().add (lb_questions);
		root.getChildren ().add (lb_questionsamount);
		root.getChildren ().add (btn_save);
		root.getChildren ().add (btn_nextscene);
		
		// Positions
		lb_title.setAlignment (Pos.CENTER);
		lb_title.setMinWidth (1920);
		lb_title.setMinHeight (180);
		lb_title.setLayoutX (0);
		lb_title.setLayoutY (0);
		
		cb_questionpacks.setMinWidth (1380);
		cb_questionpacks.setMinHeight (80);
		cb_questionpacks.setLayoutX (300);
		cb_questionpacks.setLayoutY (400);
		
		lb_questionspacks.setMinWidth (540);
		lb_questionspacks.setMinHeight (80);
		lb_questionspacks.setLayoutX (300);
		lb_questionspacks.setLayoutY (520);
		
		lb_questionspacksamount.setMinWidth (540);
		lb_questionspacksamount.setMinHeight (80);
		lb_questionspacksamount.setLayoutX (300);
		lb_questionspacksamount.setLayoutY (600);
		
		lb_questions.setMinWidth (540);
		lb_questions.setMinHeight (80);
		lb_questions.setLayoutX (1140);
		lb_questions.setLayoutY (520);
		
		lb_questionsamount.setMinWidth (540);
		lb_questionsamount.setMinHeight (80);
		lb_questionsamount.setLayoutX (1140);
		lb_questionsamount.setLayoutY (600);
		
		btn_save.setMinWidth (540);
		btn_save.setMinHeight (80);
		btn_save.setLayoutX (300);
		btn_save.setLayoutY (720);
		
		btn_nextscene.setMinWidth (540);
		btn_nextscene.setMinHeight (80);
		btn_nextscene.setLayoutX (1140);
		btn_nextscene.setLayoutY (720);
	}
	
	@Override
	public void updateScene () {
	}
	
	@Override
	public void resetScene () {
	}
}
