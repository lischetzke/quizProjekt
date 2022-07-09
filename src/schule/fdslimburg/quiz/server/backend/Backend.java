package schule.fdslimburg.quiz.server.backend;

import schule.fdslimburg.quiz.server.comm.Communication;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class Backend {
	// The real shit
	
	public Communication comm;
	
	public Backend (Communication comm) {
		this.comm = comm;
		this.loadAllQuestions();
	}
	
	private void loadAllQuestions() {
		// Do questions exists?
		Path dirQuestions = Path.of ("./questions");
		try {
			if (
					!Files.exists (dirQuestions) ||
							!Files.isDirectory (dirQuestions) ||
							Files.list (dirQuestions).findAny ().isEmpty ()
			) {
				createQuestionsInitial();
			}
		} catch(IOException e) {
			// couldn't load folder
			System.err.println ("Error checking for questions!");
		}
		
		try {
			Files.list (dirQuestions).forEach (path -> {
				// Check if valid
				
			});
		} catch(IOException e) {
			System.err.println ("Error reading files!");
		}
	}
	
	private void createQuestionsInitial() {
		String path = "/schule/fdslimburg/quiz/server/backend/questions/";
		String[] initalQuestions = new String[] {
				"init-01.json"
		};
		
		for(String f : initalQuestions) {
			InputStream is = getClass().getResourceAsStream (path + f);
			if(is == null)
				continue;
			InputStreamReader isr = new InputStreamReader (is);
		}
	}
}
