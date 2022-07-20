package schule.fdslimburg.quiz.server.scenes;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import schule.fdslimburg.quiz.server.backend.Backend;

public class SceneQuiz extends AScene {
	
	// TODO: Stil und Fragen von WwM?
	// TODO: Multiple tries allowed
	// TODO: Tastenkombinationen!
	//          T -> Start timer after reading question
	//          1 - 4 -> Answer A - D after buzz
	//          C -> Close question
	//          N -> next question
	@Override
	public void createScene (Backend backend) {
		this.backend = backend;
		Border border = new Border(new BorderStroke (Style.FOREGROUND.value (), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths (4)));
		Background bg = new Background (new BackgroundFill (Style.BACKGROUND.value (), CornerRadii.EMPTY, Insets.EMPTY));
		this.backend.game.start ();
		
		// Create
		root = new Pane ();
		
		// Set
		root.setBackground (bg);
		root.setFocusTraversable(true);
		
		// Actions
		root.setOnKeyReleased(t -> {
			// TODO!
		});
		
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
