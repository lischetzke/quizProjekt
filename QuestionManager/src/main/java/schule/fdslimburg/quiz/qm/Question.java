package schule.fdslimburg.quiz.qm;

import javafx.beans.property.*;
import javafx.collections.ObservableList;

import java.util.List;

public class Question {
	private IntegerProperty ID;
	public void setID(int value) { IDProperty().set(value); }
	public int getID() { return IDProperty().get(); }
	public IntegerProperty IDProperty() {
		if (ID == null) ID = new SimpleIntegerProperty (this, "ID");
		return ID;
	}
	
	private StringProperty Question;
	public void setQuestion(String value) { QuestionProperty().set(value); }
	public String getQuestion() { return QuestionProperty().get(); }
	public StringProperty QuestionProperty() {
		if (Question == null) Question = new SimpleStringProperty (this, "Question");
		return Question;
	}
	
	private ListProperty<Answer> Answers;
	public void addAnswer(Answer value) { AnswersProperty ().add(value); }
	public void setAnswers(ObservableList<Answer> value) { AnswersProperty().set(value); }
	public List<Answer> getAnswers() { return AnswersProperty().get(); }
	public ListProperty<Answer> AnswersProperty() {
		if (Answers == null) Answers = new SimpleListProperty<> (this, "Answers");
		return Answers;
	}
}
