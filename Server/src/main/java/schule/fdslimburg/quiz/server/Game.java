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
	public int answerTime = 15;
	public long startAnswerTime = 0L;
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
		this.buzzerHandler.unlock ();
	}
	
	public String buzzed() {
		if(!buzzerHandler.locked)
			return "";
		startAnswerTime = Util.millis();
		return player.get(buzzerHandler.clientId);
	}
	
	public void unlock() {
		buzzerHandler.unlock ();
		startAnswerTime = 0L;
	}
	
	public long answerTimeLeft() {
		if(startAnswerTime == 0L)
			return answerTime;
		return Math.floorDiv (Math.max(0, startAnswerTime + (answerTime * 1000L) - Util.millis ()), 1000L);
	}
	
	public void reset() {
		this.buzzerHandler.unlock ();
		this.running = false;
		this.questionpack = null;
		this.questionAmount = 5;
		this.answerTime = 15;
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
	
	public void next() {
		questionId++;
	}
	
	// TODO: If true, disable all answer buttons in SceneGame
	public boolean answer(int answer) {
		int clientId = buzzerHandler.clientId;
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
		buzzerHandler.unlock ();
	}
}
