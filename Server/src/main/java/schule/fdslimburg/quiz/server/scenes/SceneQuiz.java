package schule.fdslimburg.quiz.server.scenes;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import schule.fdslimburg.quiz.server.backend.Backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static schule.fdslimburg.quiz.server.Util.millis;

public class SceneQuiz extends AScene {
	
	// TODO: Stil und Fragen von WwM?
	// TODO: Multiple tries allowed
	// TODO: Tastenkombinationen!
	//          T -> Start timer after reading question
	//          1 - 4 -> Answer A - D after buzz
	//          U -> Unlock
	//          C -> Close question
	//          N -> next question
	
	private Label lb_title;
	private Label lb_progress;
	private Label lb_question;
	private List<Button> btn_answers;    // Text size 32
	private Label lb_buzzed;
	private Label lb_playerbuzzed;
	private Label lb_answertime;
	private Label lb_timeleft;
	private Button btn_unlock;
	private Button btn_next;
	private List<Label> lb_clients;
	private List<Label> lb_clientPoints;
	private long answerSubmitted = 0L;
	
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		Border border = new Border(new BorderStroke (Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		this.backend.game.start ();
		
		// Create
		root = new Pane ();
		lb_title = new Label ("this.backend.game.questionpack.name");
		lb_progress = new Label("0 / " + "this.backend.game.questionAmount");
		lb_question = new Label("this.backend.game.getQuestion ()");
		btn_answers = new ArrayList<> ();
		for(int i = 0; i < 4; i++) {
			Button b = new Button ("[A] -----");
			btn_answers.add(b);
		}
		lb_buzzed = new Label ("this.backend.game.buzzed () != \"\" ? \"Gedrückt:\" : \"\"");
		lb_playerbuzzed = new Label("this.backend.game.buzzed ()");
		lb_answertime = new Label("this.backend.game.buzzed () != \"\" ? \"Restliche Antwortzeit:\" : \"\"");
		lb_timeleft = new Label("this.backend.game.answerTimeLeft()");
		btn_unlock = new Button ("Unlock!");
		btn_next = new Button ("Nächste Frage");
		lb_clients = new ArrayList<> ();
		lb_clientPoints = new ArrayList<> ();
		
		// Set
		root.setBackground (bg);
		root.setFocusTraversable(true);
		
		lb_title.setTextFill (Style.FOREGROUND.value ());
		lb_title.setFont (Font.font ("Consolas", 36));
		lb_progress.setTextFill (Style.FOREGROUND.value ());
		lb_progress.setFont (Font.font ("Consolas", 20));
		
		lb_question.setWrapText (true);
		lb_question.setAlignment (Pos.CENTER);
		lb_question.setTextFill (Style.WHITE.value ());
		lb_question.setFont (Font.font ("Consolas", 42));
		
		for(Button b : btn_answers) {
			b.setTextAlignment (TextAlignment.CENTER);
			b.setBackground (bg);
			b.setTextFill (Style.FOREGROUND.value ());
			b.setBorder (border);
			b.setFont (Font.font ("Consolas", 24));
		}
		
		lb_buzzed.setTextFill (Style.FOREGROUND.value ());
		lb_buzzed.setFont (Font.font ("Consolas", 32));
		lb_playerbuzzed.setTextFill (Style.FOREGROUND.value ());
		lb_playerbuzzed.setFont (Font.font ("Consolas", 32));
		
		lb_answertime.setTextFill (Style.FOREGROUND.value ());
		lb_answertime.setFont (Font.font ("Consolas", 32));
		lb_timeleft.setTextFill (Style.FOREGROUND.value ());
		lb_timeleft.setFont (Font.font ("Consolas", 32));
		
		btn_unlock.setBackground (bg);
		btn_unlock.setTextFill (Style.FOREGROUND.value ());
		btn_unlock.setBorder (border);
		btn_unlock.setFont (Font.font ("Consolas", 48));
		
		btn_next.setBackground (bg);
		btn_next.setTextFill (Style.FOREGROUND.value ());
		btn_next.setBorder (border);
		btn_next.setFont (Font.font ("Consolas", 48));
		
		// Actions
		root.setOnKeyReleased(t -> {
			// TODO!
		});
		
		for(int i = 0; i < btn_answers.size (); i++) {
			int finalI = i;
			btn_answers.get (i).setOnAction (e -> {
				if(this.backend.game.buzzed ().equals(""))
					return;
				boolean correct = this.backend.game.answer (finalI);
				btn_answers.get (finalI).setTextFill (correct ? Style.GREEN.value () : Style.RED.value ());
				
				if(correct) {
					// Block all answers
					for(Button b : btn_answers) {
						b.setDisable (true);
					}
					btn_unlock.setDisable (true);
				}
				
				answerSubmitted = millis ();
			});
		}
		
		btn_unlock.setOnAction (e -> {
			this.backend.game.unlock ();
		});
		
		// TODO: Next question => enable Button
		btn_next.setOnAction (e -> {
			for(Button b : btn_answers) {
				b.setDisable (false);
				b.setTextFill (Style.FOREGROUND.value ());
			}
			btn_unlock.setDisable (false);
			this.backend.game.unlock ();
			if(this.backend.game.questionId + 1 >= this.backend.game.questionAmount) {
				this.sceneStatus = SceneStatus.SCENE_NEXT;
			} else {
				this.backend.game.next ();
			}
		});
		
		// Add
		root.getChildren ().add (lb_title);
		root.getChildren ().add (lb_progress);
		root.getChildren ().add (lb_question);
		root.getChildren ().addAll (btn_answers);
		root.getChildren ().add (lb_buzzed);
		root.getChildren ().add (lb_playerbuzzed);
		//root.getChildren ().add (lb_answertime);
		//root.getChildren ().add (lb_timeleft);
		root.getChildren ().add (btn_unlock);
		root.getChildren ().add (btn_next);
		
		// Positions
		lb_title.setAlignment (Pos.CENTER);
		lb_title.setMinWidth (1920);
		lb_title.setMinHeight (80);
		lb_title.setLayoutX (0);
		lb_title.setLayoutY (0);
		lb_progress.setAlignment (Pos.CENTER);
		lb_progress.setMinWidth (1920);
		lb_progress.setMinHeight (80);
		lb_progress.setLayoutX (0);
		lb_progress.setLayoutY (48);
		
		lb_question.setAlignment (Pos.CENTER);
		lb_question.setMinWidth (1600);
		lb_question.setMaxWidth (1600);
		lb_question.setMinHeight (100);
		lb_question.setLayoutX (160);
		lb_question.setLayoutY (120);
		
		for(int i = 0; i < btn_answers.size (); i++) {
			int x = i % 2;
			int y = (int) Math.floor(i / 2.0);
			Button b = btn_answers.get (i);
			
			b.setAlignment (Pos.CENTER_LEFT);
			b.setWrapText (true);
			b.setMinWidth (560);
			b.setMaxWidth (560);
			b.setMinHeight (160);
			b.setMaxHeight (160);
			b.setLayoutX (380 + x * 600);
			b.setLayoutY (280 + y * 180);
		}
		
		lb_buzzed.setAlignment (Pos.CENTER_RIGHT);
		lb_buzzed.setMinWidth (940);
		lb_buzzed.setMinHeight (80);
		lb_buzzed.setLayoutX (0);
		lb_buzzed.setLayoutY (720);
		
		lb_playerbuzzed.setAlignment (Pos.CENTER_LEFT);
		lb_playerbuzzed.setMinWidth (960);
		lb_playerbuzzed.setMinHeight (80);
		lb_playerbuzzed.setLayoutX (980);
		lb_playerbuzzed.setLayoutY (720);
		
		lb_answertime.setAlignment (Pos.CENTER_RIGHT);
		lb_answertime.setMinWidth (940);
		lb_answertime.setMinHeight (80);
		lb_answertime.setLayoutX (0);
		lb_answertime.setLayoutY (800);
		
		lb_timeleft.setAlignment (Pos.CENTER_LEFT);
		lb_timeleft.setMinWidth (960);
		lb_timeleft.setMinHeight (80);
		lb_timeleft.setLayoutX (980);
		lb_timeleft.setLayoutY (800);
		
		btn_unlock.setMinWidth (420);
		btn_unlock.setMinHeight (80);
		btn_unlock.setLayoutX (520);
		btn_unlock.setLayoutY (900);
		
		btn_next.setMinWidth (420);
		btn_next.setMinHeight (80);
		btn_next.setLayoutX (980);
		btn_next.setLayoutY (900);
	}
	
	@Override
	public Pane getRoot () {
		this.backend.game.unlock ();
		
		this.backend.game.player.keys ().asIterator ().forEachRemaining (key -> {
			String playername = this.backend.game.player.get (key);
			Label player = new Label (playername);
			lb_clients.add (player);
			Label points = new Label ("0");
			lb_clientPoints.add (points);
		});
		
		for(Label l : lb_clients) {
			l.setAlignment (Pos.CENTER_RIGHT);
			l.setTextFill (Style.FOREGROUND.value ());
			l.setFont (Font.font ("Consolas", 24));
		}
		for(Label l : lb_clientPoints) {
			l.setAlignment (Pos.CENTER_LEFT);
			l.setTextFill (Style.FOREGROUND.value ());
			l.setFont (Font.font ("Consolas", 24));
		}
		
		root.getChildren ().addAll (lb_clients);
		root.getChildren ().addAll (lb_clientPoints);
		
		for(int i = 0; i < lb_clients.size (); i++) {
			Label l = lb_clients.get (i);
			Label p = lb_clientPoints.get (i);
			
			l.setMinWidth (320);
			l.setMinHeight (40);
			l.setLayoutX (1460);
			l.setLayoutY (980 - (i * 60));
			
			p.setMinWidth (40);
			p.setMinHeight (40);
			p.setLayoutX (1790);
			p.setLayoutY (980 - (i * 60));
		}
		
		return super.getRoot ();
	}
	
	private static final String[] letter = {"[A] ", "[B] ", "[C] ", "[D] "};
	
	@Override
	public void updateScene () {
		lb_title.setText (this.backend.game.questionpack.name);
		lb_progress.setText ((this.backend.game.questionId + 1) + " / " + this.backend.game.questionAmount);
		lb_question.setText (this.backend.game.getQuestion ());
		String[] answers = this.backend.game.getAnswers ();
		for(int i = 0; i < answers.length; i++) {
			Button b = btn_answers.get (i);
			b.setText (letter[i] + answers[i]);
		}
		
		lb_buzzed.setText (this.backend.game.buzzed ().equals ("") ? "" : "Gedrückt:");
		lb_playerbuzzed.setText (this.backend.game.buzzed ());
		lb_answertime.setText (this.backend.game.buzzed ().equals ("") ? "" : "Restliche Antwortzeit:");
		lb_timeleft.setText (this.backend.game.buzzed ().equals ("") ? "" : ("" + this.backend.game.answerTimeLeft()));
		
		for(int i = 0; i < lb_clients.size (); i++) {
			String player = lb_clients.get (i).getText ();
			Label p = lb_clientPoints.get (i);
			AtomicInteger clientId = new AtomicInteger (-1);
			this.backend.game.player.keys ().asIterator ().forEachRemaining (key -> {
				String name = this.backend.game.player.get (key);
				if(name.equals (player)) {
					clientId.set (key);
				}
			});
			if(clientId.get () == -1)
				continue;
			p.setText ("" + this.backend.game.scores.get (clientId.get ()));
		}
		
		// Reset button color
		if(answerSubmitted != 0L && (answerSubmitted + 5000L) < millis()) {
			for(Button b : btn_answers) {
				b.setTextFill (Style.FOREGROUND.value ());
			}
			answerSubmitted = 0L;
		}
	}
	
	@Override
	public void resetScene () {
	
	}
}
