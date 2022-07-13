package schule.fdslimburg.quiz.server.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import schule.fdslimburg.quiz.server.backend.questions.data.Data;
import schule.fdslimburg.quiz.server.comm.Communication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Backend {
	// The real shit
	
	public Communication comm;
	public List<Data> allQuestions;
	
	public Backend (Communication comm) {
		this.comm = comm;
		this.allQuestions = new ArrayList<> ();
		this.loadAllQuestions ();
	}
	
	private void loadAllQuestions () {
		// Do questions exists?
		Path dirQuestions = Path.of ("./questions");
		try {
			if (
					!Files.exists (dirQuestions) ||
							!Files.isDirectory (dirQuestions) ||
							Files.list (dirQuestions).findAny ().isEmpty ()
			) {
				createQuestionsInitial ();
			}
		} catch (IOException e) {
			// couldn't load folder
			System.err.println ("Error checking for questions!");
		}
		
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			
			Files.list (dirQuestions).forEach (path -> {
				try {
					BufferedReader br = new BufferedReader (new InputStreamReader (new FileInputStream (String.valueOf (path))));
					StringBuilder sb = new StringBuilder ();
					while (br.ready ()) {
						String l = br.readLine ();
						sb.append (l);
					}
					
					allQuestions.add(gson.fromJson (sb.toString (), Data.class));
				} catch (Exception e) {
					System.err.println ("Error reading files!");
				}
			});
			
			int questions = 0;
			for(Data d : allQuestions) {
				questions += d.questions.size ();
			}
			
			System.out.println (allQuestions.size () + " Questionpacks loaded!");
			System.out.println (questions + " Questions loaded!");
		} catch (IOException e) {
			System.err.println ("Error reading files!");
		}
	}
	
	private void createQuestionsInitial () {
		String path = "/schule/fdslimburg/quiz/server/backend/questions/";
		String[] initalQuestions = new String[]{
				"init-01.json"
		};
		
		Path dirQuestions = Path.of ("./questions");
		try {
			if (!Files.exists (dirQuestions))
				Files.createDirectories (dirQuestions);
		} catch (IOException e) {
			System.err.println ("Error creating folder!");
		}
		
		for (String f : initalQuestions) {
			try {
				exportResource (path + f, "./questions/" + f);
			} catch (Exception e) {
				System.err.println ("Error creating/writing file " + f + "!");
			}
		}
	}
	
	private String exportResource (String resourceName, String output) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try {
			stream = getClass ().getResourceAsStream (resourceName);
			if (stream == null) {
				throw new Exception ("Cannot get resource \"" + resourceName + "\" from Jar file.");
			}
			
			int readBytes;
			byte[] buffer = new byte[4096];
			jarFolder = new File (getClass ().getProtectionDomain ().getCodeSource ().getLocation ().toURI ().getPath ()).getParentFile ().getPath ().replace ('\\', '/');
			resStreamOut = new FileOutputStream (output);
			while ((readBytes = stream.read (buffer)) > 0) {
				resStreamOut.write (buffer, 0, readBytes);
			}
		} finally {
			assert stream != null;
			stream.close ();
			assert resStreamOut != null;
			resStreamOut.close ();
		}
		
		return jarFolder + resourceName;
	}
}
