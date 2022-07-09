package schule.fdslimburg.quiz.qm;

import javafx.beans.property.*;

public class Answer {
	private IntegerProperty ID;
	public void setID(int value) { IDProperty().set(value); }
	public int getID() { return IDProperty().get(); }
	public IntegerProperty IDProperty() {
		if (ID == null) ID = new SimpleIntegerProperty (this, "ID");
		return ID;
	}
	
	private StringProperty Answer;
	public void setAnswer(String value) { AnswerProperty().set(value); }
	public String getAnswer() { return AnswerProperty().get(); }
	public StringProperty AnswerProperty() {
		if (Answer == null) Answer = new SimpleStringProperty (this, "Answer");
		return Answer;
	}
	
	private SimpleBooleanProperty Correct;
	public void setCorrect(boolean value) { CorrectProperty().set(value); }
	public boolean getCorrect() { return CorrectProperty().get(); }
	public BooleanProperty CorrectProperty() {
		if (Correct == null) Correct = new SimpleBooleanProperty (this, "Correct");
		return Correct;
	}
}
