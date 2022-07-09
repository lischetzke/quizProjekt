package schule.fdslimburg.quiz.qm;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MenuController {
	@FXML
	private Label welcomeText;
	
	@FXML
	protected void onHelloButtonClick () {
		welcomeText.setText ("VBox1");
	}
}
