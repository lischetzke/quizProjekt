package schule.fdslimburg.quiz.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.comm.Communication;
import schule.fdslimburg.quiz.server.scenes.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Server extends Application {
	public static void main (String[] args) {
		Scanner s = new Scanner (System.in);
		
		new Thread(() -> {
			if(s.hasNextLine ()) {
				String line = s.nextLine ();
				if(line.equalsIgnoreCase ("exit")) {
					System.exit (0);
				}
			}
			try {
				Thread.sleep (200);
			} catch (InterruptedException ignore) {}
		}).start ();
		
		launch ();
	}
	
	private Scene root;
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
				if(currentScene == null) {
					try { Thread.sleep (50); } catch (InterruptedException e) { throw new RuntimeException (e); }
					continue;
				}
				
				Platform.runLater(() -> currentScene.updateScene ());
				
				// Because status gets reset after request
				SceneStatus ss = currentScene.getStatus();
				
				if(ss != null) {
					if(ss == SceneStatus.SCENE_NEXT) {
						int newSceneId = (currentScene.getSceneId () % 5) + 1;
						System.out.println ("Scene Switch to " + newSceneId + "!");
						//Platform.runLater(() -> root.setRoot (getScene (newSceneId)));
						root.setRoot (getScene (newSceneId));
					}
					
					if(ss == SceneStatus.RESET) {
						System.out.println ("Reset all scenes!");
						Platform.runLater(() -> root.setRoot (getScene (0)));
						Platform.runLater(() -> sceneJoin.resetScene ());
						Platform.runLater(() -> sceneSelect.resetScene ());
						Platform.runLater(() -> scenePrepare.resetScene ());
						Platform.runLater(() -> sceneQuiz.resetScene ());
						Platform.runLater(() -> sceneScore.resetScene ());
					}
					
					if(ss == SceneStatus.EXIT) {
						Platform.runLater(() -> root.setRoot (getScene (4)));
						// Disconnect all players
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
		root = new Scene(new Pane());
		sceneJoin = new SceneJoin ();
		System.out.println ("SceneJoin " + sceneJoin.getSceneId ());
		sceneSelect = new SceneSelect ();
		System.out.println ("SceneSelect " + sceneSelect.getSceneId ());
		scenePrepare = new ScenePrepare ();
		System.out.println ("ScenePrepare " + scenePrepare.getSceneId ());
		sceneQuiz = new SceneQuiz ();
		System.out.println ("SceneQuiz " + sceneQuiz.getSceneId ());
		sceneScore = new SceneScore ();
		System.out.println ("SceneScore " + sceneScore.getSceneId ());
		
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
		stage.setScene (root);
		root.setRoot (getScene (0));
		stage.show ();
	}
	
	private Pane getScene(int sceneId) {
		List<AScene> scenes = new ArrayList<> ();
		
		scenes.add(sceneJoin);
		scenes.add(sceneSelect);
		scenes.add(scenePrepare);
		scenes.add(sceneQuiz);
		scenes.add(sceneScore);
		
		AScene newScene = sceneSelect;
		
		for(AScene s : scenes) {
			if(s.getSceneId () == sceneId) {
				newScene = s;
				break;
			}
		}
		
		return (currentScene = newScene).getRoot ();
	}
}
