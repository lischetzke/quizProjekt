package schule.fdslimburg.quiz.server.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import schule.fdslimburg.quiz.server.Game;
import schule.fdslimburg.quiz.server.backend.data.AnswerSerialized;
import schule.fdslimburg.quiz.server.backend.data.Data;
import schule.fdslimburg.quiz.server.backend.data.QuestionSerialized;
import schule.fdslimburg.quiz.server.comm.Communication;
import schule.fdslimburg.quiz.server.events.NewClientEventArgs;
import schule.fdslimburg.quiz.server.events.NewClientEventListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Backend implements NewClientEventListener {
	// The real shit
	
	public Communication comm;
	public List<Data> allQuestions;
	public Game game;
	// TODO: Get all new and removed clients through listener
	public Dictionary<Integer, String> clientNames = new Hashtable<> ();
	
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
			System.err.println (e.getMessage ());
			e.printStackTrace ();
		}
		
		try {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			
			Files.list (dirQuestions).forEach (path -> {
				System.out.println ("Try to read " + path);
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
					System.err.println (e.getMessage ());
					e.printStackTrace ();
				}
			});
			
			b64decodeAll();
			
			int questions = 0;
			for(Data d : allQuestions) {
				questions += d.questions.size ();
			}
			
			System.out.println (allQuestions.size () + " Questionpacks loaded!");
			System.out.println (questions + " Questions loaded!");
		} catch (IOException e) {
			System.err.println ("Error reading files!");
			System.err.println (e.getMessage ());
			e.printStackTrace ();
		}
	}
	
	private void b64decodeAll() {
		for(Data d : allQuestions) {
			d.name = b64d(d.name);
			
			for(QuestionSerialized q : d.questions) {
				q.question = b64d (q.question);
				
				for(AnswerSerialized a : q.answers) {
					a.answer = b64d (a.answer);
				}
			}
		}
	}
	
	public static String b64d(String in) {
		return new String(Base64.getDecoder().decode(in));
	}
	
	private void createQuestionsInitial () {
		String path = "/schule/fdslimburg/quiz/server/backend/questions/";
		String[] initalQuestions = new String[] {
				"init-01-wwm.json",
				"init-02-pm-6v3.json"
		};
		
		Path dirQuestions = Path.of ("./questions");
		try {
			if (!Files.exists (dirQuestions))
				Files.createDirectories (dirQuestions);
		} catch (IOException e) {
			System.err.println ("Error creating folder!");
			System.err.println (e.getMessage ());
			e.printStackTrace ();
		}
		
		for (String f : initalQuestions) {
			try {
				exportResource (path, f, "./questions/" + f);
			} catch (Exception e) {
				System.err.println ("Error creating/writing file " + f + "!");
				System.err.println (e.getMessage ());
				e.printStackTrace ();
			}
		}
	}
	
	private String exportResource (String resourcePath, String resourceName, String output) throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;
		String jarFolder;
		try {
			if(stream == null)
				try { stream = getClass ().getResourceAsStream (resourcePath + resourceName); } catch(Exception ignored) {}
			if(stream == null)
				try { stream = getClass ().getClassLoader ().getResourceAsStream (resourcePath + resourceName); } catch(Exception ignored) {}
			
			int readBytes;
			byte[] buffer = new byte[4096];
			jarFolder = new File (getClass ().getProtectionDomain ().getCodeSource ().getLocation ().toURI ().getPath ()).getParentFile ().getPath ()/*.replace ('\\', '/')*/;
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
	
	@Override
	public void triggerEvent (NewClientEventArgs ea) {
		String clientName = clientNames.get (ea.clientId);
		if(clientName == null) {
			// New client
			clientNames.put (ea.clientId, "Client " + ea.clientId + " (" + ea.clientAddress.toString () + ")");
		}
	}
}
