package schule.fdslimburg.quiz.server;

import schule.fdslimburg.quiz.server.backend.Backend;
import schule.fdslimburg.quiz.server.comm.Client;
import schule.fdslimburg.quiz.server.comm.Communication;

import java.util.List;

public class QuizServer {
	public Backend backend;
	
	public static void main (String[] args) {
		// TODO: Start TCP server
		// TODO: Start backend with the new TCP server
		//          Load questions from file
		// TODO: Start frontend and register events from backend
	}
	
	public QuizServer() {
		Communication comm = new Communication (5555);
		this.backend = new Backend(comm);
	}
}
