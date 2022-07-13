package schule.fdslimburg.quiz.qm;

import com.google.gson.GsonBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import schule.fdslimburg.quiz.qm.data.AnswerSerialized;
import schule.fdslimburg.quiz.qm.data.Data;
import schule.fdslimburg.quiz.qm.data.QuestionSerialized;

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
	Dictionary<Question, List<Answer>> answers = new Hashtable<> ();
	
	MenuBar menubar;
	Menu mFile;
	SeparatorMenuItem miFileSep1;
	MenuItem miFileNew, miFileOpen, miFileSave, miFileExit;
	
	Label lbName;
	TextField tfName;
	
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
		lbName = new Label("Fragenkategorie: ");
		tfName = new TextField ();
		
		//====================
		btnQadd = new Button ("+");
		btnQdel = new Button ("-");
		
		btnQadd.setFont (fontMonospaced);
		btnQdel.setFont (fontMonospaced);
		
		tblQuestions = new TableView<> ();
		
		tcQid = new TableColumn<> ("ID");
		tcQid.setCellValueFactory (new PropertyValueFactory<> ("ID"));
		tcQid.setEditable (true);
		
		tcQquestion = new TableColumn<> ("Question");
		tcQquestion.setCellValueFactory (new PropertyValueFactory<> ("Question"));
		tcQquestion.setEditable (true);
		tcQquestion.setCellFactory(TextFieldTableCell.forTableColumn());
		tcQquestion.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setQuestion (e.getNewValue()));
		
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
		tcAid.setEditable (true);
		
		tcAanswer = new TableColumn<> ("Answer");
		tcAanswer.setCellValueFactory (new PropertyValueFactory<> ("Answer"));
		tcAanswer.setEditable (true);
		tcAanswer.setCellFactory(TextFieldTableCell.forTableColumn());
		tcAanswer.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setAnswer (e.getNewValue()));
		
		tcAcorrect = new TableColumn<> ("Correct");
		tcAcorrect.setCellValueFactory (new PropertyValueFactory<> ("Correct"));
		tcAcorrect.setEditable (true);
		tcAcorrect.setCellFactory(p -> new CheckBoxTableCell<>());
		tcAcorrect.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setCorrect (Objects.equals (e.getNewValue (), "true")));
		
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
		
		gridPane.add(lbName, 0, 0, 6, 1);
		gridPane.add(tfName, 6, 0, 6, 1);
		
		gridPane.add (btnQadd, 0, 1, 2, 1);
		gridPane.add (btnQdel, 2, 1, 2, 1);
		gridPane.add (tblQuestions, 0, 2, 6, 3);
		tblQuestions.prefWidthProperty ().bind (gridPane.widthProperty ().divide (2.0));
		tcQid.prefWidthProperty ().bind (tblQuestions.widthProperty ().multiply (0.2));
		tcQquestion.prefWidthProperty ().bind (tblQuestions.widthProperty ().multiply (0.8));
		
		gridPane.add (btnAadd, 6, 1, 2, 1);
		gridPane.add (btnAdel, 8, 1, 2, 1);
		gridPane.add (tblAnswers, 6, 2, 6, 3);
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
			answers.put (newQuestion, new ArrayList<> ());
			tblQuestions.getItems ().clear ();
			tblQuestions.getItems ().addAll (questions);
		});
		
		btnQdel.setOnAction (e -> {
			// Remove selected Question
			tblQuestions.getSelectionModel ().getSelectedItems ().forEach (question -> {
				questions.remove (question);
				tblQuestions.getItems ().clear ();
				tblQuestions.getItems ().addAll (questions);
			});
		});
		
		tblQuestions.getSelectionModel ().selectedItemProperty ().addListener ((obs, oldSelection, newSelection) -> {
			if (oldSelection != null) {
				// Save answers to old question
				System.out.println ("Save Answers to Question");
				answers.get (oldSelection).addAll (tblAnswers.getItems ());
			}
			
			tblAnswers.getItems ().clear ();
			
			if (newSelection != null) {
				// Load answers from new question
				System.out.println ("Load Answers from Question");
				tblAnswers.getItems ().addAll (answers.get (newSelection));
				answers.get (newSelection).clear ();
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
			// Generate JSON
			Data data = new Data ();
			data.name = tfName.getText ();
			data.questions = new ArrayList<> ();
			for(Question q : questions) {
				QuestionSerialized qs = new QuestionSerialized ();
				qs.question = q.getQuestion ();
				qs.answers = new ArrayList<> ();
				
				for(Answer a : answers.get (q)) {
					AnswerSerialized as = new AnswerSerialized ();
					as.answer = a.getAnswer ();
					as.correct = a.getCorrect ();
					
					qs.answers.add (as);
				}
				
				if(q == tblQuestions.getSelectionModel ().getSelectedItem ()) {
					for(Answer a : tblAnswers.getItems ()) {
						AnswerSerialized as = new AnswerSerialized ();
						as.answer = a.getAnswer ();
						as.correct = a.getCorrect ();
						
						qs.answers.add (as);
					}
				}
				
				data.questions.add (qs);
			}
			
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			System.out.println(gson.toJson(data));
			
			// TODO: Open FileSaveDialog
		});
		miFileExit.setOnAction (e -> {
			System.exit (0);
		});
	}
}
