package schule.fdslimburg.quiz.qm;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QMApp extends Application {
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
	
	List<Question> questions = new ArrayList<> ();
	Dictionary<Question, Answer> answers = new Hashtable<> ();
	
	MenuBar menubar;
	Menu mFile;
	SeparatorMenuItem miFileSep1;
	MenuItem miFileNew, miFileOpen, miFileSave, miFileExit;
	
	Button btnQadd, btnQdel;
	TableView<Question> tblQuestions;
	TableColumn<Question, Integer> tcQid;
	TableColumn<Question, String> tcQquestion;
	
	Button btnAadd, btnAdel;
	TableView<Answer> tblAnswers;
	TableColumn<Answer, Integer> tcAid;
	TableColumn<Answer, String> tcAanswer;
	TableColumn<Answer, Boolean> tcAcorrect;
	
	@Override
	public void start (Stage stage) throws IOException {
		btnQadd = new Button ("+");
		btnQdel = new Button ("-");
		
		btnQadd.setFont (fontMonospaced);
		btnQdel.setFont (fontMonospaced);
		
		tblQuestions = new TableView<> ();
		
		tcQid = new TableColumn<> ("ID");
		tcQid.setCellValueFactory (new PropertyValueFactory<> ("ID"));
		
		tcQquestion = new TableColumn<> ("Question");
		tcQquestion.setCellValueFactory (new PropertyValueFactory<> ("Question"));
		
		tblQuestions.setEditable (true);
		tblQuestions.getColumns ().setAll (tcQid, tcQquestion);
		addLogicQuestions ();
		
		btnAadd = new Button ("+");
		btnAdel = new Button ("-");
		
		btnAadd.setFont (fontMonospaced);
		btnAdel.setFont (fontMonospaced);
		
		tblAnswers = new TableView<> ();
		
		tcAid = new TableColumn<> ("ID");
		tcAid.setCellValueFactory (new PropertyValueFactory<> ("ID"));
		
		tcAanswer = new TableColumn<> ("Answer");
		tcAanswer.setCellValueFactory (new PropertyValueFactory<> ("Answer"));
		
		tcAcorrect = new TableColumn<> ("Correct");
		tcAcorrect.setCellValueFactory (new PropertyValueFactory<> ("Correct"));
		
		tblAnswers.setEditable (true);
		tblAnswers.getColumns ().setAll (tcAid, tcAanswer, tcAcorrect);
		addLogicAnswers ();
		
		//====================
		menubar = new MenuBar ();
		
		mFile = new Menu ("Datei");
		miFileSep1 = new SeparatorMenuItem ();
		miFileNew = new MenuItem ("Neu");
		miFileOpen = new MenuItem ("Öffnen");
		miFileSave = new MenuItem ("Speichern unter");
		miFileExit = new MenuItem ("Beenden");
		
		mFile.getItems ().addAll (miFileNew, miFileOpen, miFileSave, miFileSep1, miFileExit);
		
		menubar.getMenus ().addAll (mFile);
		addLogicMenu ();
		//====================
		GridPane gridPane = new GridPane ();
		
		gridPane.setMinSize (820, 400);
		gridPane.setPadding (new Insets (10, 10, 10, 10));
		gridPane.setVgap (5);
		gridPane.setHgap (5);
		gridPane.setAlignment (Pos.CENTER);
		
		gridPane.add (btnQadd, 0, 0, 1, 1);
		gridPane.add (btnQdel, 1, 0, 1, 1);
		gridPane.add (tblQuestions, 0, 1, 5, 3);
		tblQuestions.prefWidthProperty ().bind (gridPane.widthProperty ().divide (2.0));
		tcQid.prefWidthProperty ().bind (tblQuestions.widthProperty ().multiply (0.2));
		tcQquestion.prefWidthProperty ().bind (tblQuestions.widthProperty ().multiply (0.8));
		
		gridPane.add (btnAadd, 5, 0, 1, 1);
		gridPane.add (btnAdel, 6, 0, 1, 1);
		gridPane.add (tblAnswers, 5, 1, 5, 3);
		tblAnswers.prefWidthProperty ().bind (gridPane.widthProperty ().divide (2.0));
		tcAid.prefWidthProperty ().bind (tblAnswers.widthProperty ().multiply (0.2));
		tcAanswer.prefWidthProperty ().bind (tblAnswers.widthProperty ().multiply (0.6));
		tcAcorrect.prefWidthProperty ().bind (tblAnswers.widthProperty ().multiply (0.2));
		
		VBox container = new VBox ();
		container.setFillWidth (true);
		container.getChildren ().addAll (menubar, gridPane);
		
		Scene scene = new Scene (container);
		
		stage.setTitle ("Quiz: Question Manager");
		stage.setScene (scene);
		stage.show ();
	}
	
	private void addLogicQuestions () {
		btnQadd.setOnAction (e -> {
			// Get next Question id
			AtomicInteger nextId = new AtomicInteger (1);
			tblQuestions.getItems ().forEach (question -> nextId.set (Math.max (nextId.get (), question.getID () + 1)));
			
			// Add entry with no question
			Question newQuestion = new Question ();
			newQuestion.setID (nextId.get ());
			newQuestion.setQuestion ("None");
			questions.add (newQuestion);
			tblQuestions.getItems ().add (newQuestion);
		});
		
		btnQdel.setOnAction (e -> {
			// Remove selected Question
			tblQuestions.getSelectionModel ().getSelectedItems ().forEach (question -> {
				tblQuestions.getItems ().remove (question);
				questions.remove (question);
			});
		});
		
		tblQuestions.getSelectionModel ().selectedItemProperty ().addListener ((obs, oldSelection, newSelection) -> {
			if (oldSelection != null) {
				// Save answers to old question
				System.out.println ("Save Answers to Question");
				questions.forEach (q -> {
					if (q == oldSelection) {
						System.out.println ("Save Answers to Question \"" + q.getQuestion () + "\"");
						System.out.println ("Tblsize: " + tblAnswers.getItems ().size ());
						q.setAnswers (tblAnswers.getItems ());
					}
				});
				//oldSelection.AnswersProperty ().set(tblAnswers.getItems ());
			}
			
			if (newSelection != null) {
				// Load answers from new question
				System.out.println ("Load Answers from Question");
				answers.remove ()
				tblAnswers.getItems ().clear ();
				tblAnswers.getItems ().setAll (answers.get (newSelection));
			}
		});
	}
	
	private void addLogicAnswers () {
		btnAadd.setOnAction (e -> {
			if (tblQuestions.getSelectionModel ().getSelectedItems ().isEmpty ())
				return;
			
			if (tblAnswers.getItems ().size () >= 4)
				return;
			
			// Get next Question id
			AtomicInteger nextId = new AtomicInteger (1);
			tblAnswers.getItems ().forEach (answer -> nextId.set (Math.max (nextId.get (), answer.getID () + 1)));
			
			// Add entry with no question
			Answer newAnswer = new Answer ();
			newAnswer.setID (nextId.get ());
			newAnswer.setAnswer ("None");
			newAnswer.setCorrect (false);
			tblAnswers.getItems ().add (newAnswer);
		});
		
		btnAdel.setOnAction (e -> {
			// Remove selected Question
			tblAnswers.getSelectionModel ().getSelectedItems ().forEach (answer -> {
				tblAnswers.getItems ().remove (answer);
			});
		});
	}
	
	private void addLogicMenu () {
		miFileNew.setOnAction (e -> {
			// TODO: Clear all table and input fields
		});
		miFileOpen.setOnAction (e -> {
			// TODO: Open FileOpenDialog
		});
		miFileSave.setOnAction (e -> {
			// TODO: Open FileSaveDialog
		});
		miFileExit.setOnAction (e -> {
			System.exit (0);
		});
	}
}
