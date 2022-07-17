package schule.fdslimburg.quiz.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.comm.Communication;
import schule.fdslimburg.quiz.server.scenes.*;

public class Server extends Application {
	public static void main (String[] args) {
		launch ();
	}
	
	private Backend backend;
	private Thread threadUpdateUI;
	private boolean updateUI = false;
	private AScene sceneJoin, sceneSelect, scenePrepare, sceneQuiz, sceneScore;
	private AScene currentScene;
	
	@Override
	public void start (Stage stage) throws Exception {
		// Start Server and Server
		Communication comm = new Communication (5555);
		comm.startModule ();
		this.backend = new Backend(comm);
		
		threadUpdateUI = new Thread (() -> {
			while(true) {
				if(currentScene != null)
					Platform.runLater(() -> currentScene.updateScene ());
				
				if(currentScene == null) {
					try { Thread.sleep (50); } catch (InterruptedException e) { throw new RuntimeException (e); }
					continue;
				}
				
				// Because status gets reset after request
				SceneStatus ss = currentScene.getStatus();
				
				if(ss != null) {
					if(ss == SceneStatus.SCENE_NEXT) {
						Platform.runLater(() -> stage.setScene (getScene ((currentScene.getSceneId () % 5) + 1)));
					}
					
					if(ss == SceneStatus.RESET) {
						Platform.runLater(() -> stage.setScene (getScene (0)));
						Platform.runLater(() -> sceneJoin.resetScene ());
						Platform.runLater(() -> sceneSelect.resetScene ());
						Platform.runLater(() -> scenePrepare.resetScene ());
						Platform.runLater(() -> sceneQuiz.resetScene ());
						Platform.runLater(() -> sceneScore.resetScene ());
					}
					
					if(ss == SceneStatus.EXIT) {
						Platform.runLater(() -> stage.setScene (getScene (4)));
						// TODO: Disconnect all players
						comm.stopModule ();
						System.exit(0);
					}
				}
				try {
					Thread.sleep (50);
				} catch (InterruptedException e) {
					throw new RuntimeException (e);
				}
			}
		});
		threadUpdateUI.start ();
		
		// TODO: Player Join Scene (With Player Rename)
		// TODO: Questionpack Select Scene
		// TODO: Prepare Scene (All Players must be ready by pressing ENTER)
		// TODO: Quiz Scene
		// TODO: End Scene (Scores)
		// TODO: Show Player Join Scene
		sceneJoin = new SceneJoin ();
		sceneSelect = new SceneSelect ();
		scenePrepare = new ScenePrepare ();
		sceneQuiz = new SceneQuiz ();
		sceneScore = new SceneScore ();
		
		sceneJoin.createScene (backend);
		sceneSelect.createScene (backend);
		scenePrepare.createScene (backend);
		sceneQuiz.createScene (backend);
		sceneScore.createScene (backend);
		
		// TODO: On Reset @sceneScore -> Reset Backend Scoresystem too
		
		stage.setTitle ("Quiz: Server");
		stage.setResizable (false);
		stage.setMinHeight (1080);
		stage.setMinWidth (1920);
		stage.setMaxHeight (1080);
		stage.setMaxWidth (1920);
		stage.setFullScreen (true);
		stage.setScene (getScene (0));
		stage.show ();
	}
	
	private Scene getScene(int sceneId) {
		return null;
	}
}
