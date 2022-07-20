package schule.fdslimburg.quiz.server;

import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.backend.BuzzerHandler;
import schule.fdslimburg.quiz.server.backend.data.AnswerSerialized;
import schule.fdslimburg.quiz.server.backend.data.Data;
import schule.fdslimburg.quiz.server.backend.data.QuestionSerialized;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

public class Game {
	private Backend backend;
	public BuzzerHandler buzzerHandler;
	public boolean ready = false;
	public boolean running = false;
	
	// TODO: Player List
	public Dictionary<Integer, String> player;
	
	// TODO: Question List
	public Data questionpack;
	public int questionAmount = 5;
	public List<QuestionSerialized> questions;
	public int questionId = 0;
	
	// TODO: Scores
	public Dictionary<Integer, Integer> scores;
	
	public Game(Backend backend) {
		this.backend = backend;
		this.buzzerHandler = new BuzzerHandler (this.backend.comm);
		this.player = new Hashtable<> ();
		this.scores = new Hashtable<> ();
		this.questions = new ArrayList<> ();
	}
	
	public void start() {
		this.buzzerHandler.startModule();
	}
	
	public void reset() {
		this.buzzerHandler.unlock ();
		this.running = false;
		this.questionpack = null;
		this.questionAmount = 5;
		this.questions.clear ();
		this.questionId = 0;
		this.player = new Hashtable<> ();
		this.scores = new Hashtable<> ();
	}
	
	public void prepare() {
		// Select and randomize questions
		questions.clear ();
		while(true) {
			int questionId = (int) Math.floor(Math.random() * questionpack.questions.size ());
			if(questions.contains (questionpack.questions.get (questionId)))
				continue;
			
			questions.add (questionpack.questions.get (questionId));
			
			if(questions.size () >= questionAmount)
				break;
		}
		player.keys ().asIterator ().forEachRemaining (clientId -> {
			scores.put (clientId, 0);
		});
		ready = true;
	}
	
	public String getQuestion() {
		QuestionSerialized qs = questions.get (questionId);
		return qs.question;
	}
	
	public String[] getAnswers() {
		QuestionSerialized qs = questions.get (questionId);
		String[] answers = new String[4];
		int counter = 0;
		for(AnswerSerialized as : qs.answers) {
			answers[counter++] = as.answer;
		}
		return answers;
	}
	
	// TODO: If true, disable all answer buttons in SceneGame
	public boolean answer(int clientId, int answer) {
		QuestionSerialized qs = questions.get (questionId);
		if(qs.answers.get (answer).correct) {
			correct (clientId);
			// true for answered correctly
			return true;
		}
		wrong (clientId);
		return false;
	}
	
	public void correct(int clientId) {
		scores.put (clientId, scores.get (clientId) + player.size ());
		questionId++;
	}
	
	public void wrong(int clientId) {
		List<Integer> toAdd = new ArrayList<> ();
		
		scores.keys ().asIterator ().forEachRemaining (player -> {
			if(player == clientId)
				return;
			
			toAdd.add (player);
		});
		
		for(int i : toAdd) {
			scores.put (i, scores.get (i) + 1);
		}
	}
}
