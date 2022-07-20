package schule.fdslimburg.quiz.server.scenes.tableHelper;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableColumnClient {
	private IntegerProperty ID = null;
	private StringProperty Name = null;
	
	public TableColumnClient () {}
	
	public TableColumnClient (int id, String name) {
		IDProperty ().set (id);
		NameProperty ().set (name);
	}
	
	public IntegerProperty IDProperty() {
		if (ID == null) ID = new SimpleIntegerProperty (this, "ID");
		return ID;
	}
	
	public StringProperty NameProperty() {
		if (Name == null) Name = new SimpleStringProperty (this, "Name");
		return Name;
	}
	
	public void setID(int id) {
		IDProperty ().set (id);
	}
	
	public Integer getID() {
		return IDProperty ().get();
	}
	
	public void setName(String name) {
		NameProperty ().set (name);
	}
	
	public String getName() {
		return NameProperty ().get();
	}
}
