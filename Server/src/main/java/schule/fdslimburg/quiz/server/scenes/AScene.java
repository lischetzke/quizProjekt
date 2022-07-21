package schule.fdslimburg.quiz.server.scenes;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import schule.fdslimburg.quiz.server.backend.Backend;

public abstract class AScene {
	private static int _sceneId = 0;
	private int sceneId = -1;
	protected Pane root;
	protected SceneStatus sceneStatus;
	protected Backend backend;
	
	protected enum Style {
		BACKGROUND(Color.BLACK),
		FOREGROUND(Color.WHITESMOKE),
		RED(Color.RED),
		GREEN(Color.GREEN),
		WHITE(Color.WHITE);
		
		public Paint p;
		
		Style(Paint p) {
			this.p = p;
		}
		
		public Paint value() {
			return p;
		}
	}
	
	public AScene() {
		synchronized (AScene.class) {
			this.sceneId = _sceneId++;
		}
	}
	
	public int getSceneId() {
		return sceneId;
	}
	
	public SceneStatus getStatus() {
		SceneStatus status = sceneStatus;
		sceneStatus = SceneStatus.NONE;
		return status;
	}
	
	public Pane getRoot() {
		return root;
	}
	
	public abstract void createScene(Backend backend);
	public abstract void updateScene();
	public abstract void resetScene();
}
