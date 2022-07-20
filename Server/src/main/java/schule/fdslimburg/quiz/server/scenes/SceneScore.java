package schule.fdslimburg.quiz.server.scenes;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import schule.fdslimburg.quiz.server.backend.Backend;

public class SceneScore extends AScene {
	
	// TODO: Auto point calculation
	//          Correct: +<amount of players>
	//          Wrong: everyone gets one point
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		Border border = new Border(new BorderStroke (Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		
		// Create
		root = new Pane ();
		
		// Set
		
		// Actions
		
		// Add
		
		// Positions
	}
	
	@Override
	public void updateScene () {
	
	}
	
	@Override
	public void resetScene () {
	
	}
}
