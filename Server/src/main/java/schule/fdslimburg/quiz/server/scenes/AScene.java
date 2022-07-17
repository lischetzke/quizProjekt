package schule.fdslimburg.quiz.server.scenes;

import javafx.scene.Scene;
import schule.fdslimburg.quiz.server.backend.Backend;

public abstract class AScene {
	private static int _sceneId = 0;
	private int sceneId = -1;
	protected Scene scene;
	protected SceneStatus sceneStatus;
	
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
	
	public abstract void createScene(Backend backend);
	public abstract Scene getScene();
	public abstract void updateScene();
	public abstract void resetScene();
}
